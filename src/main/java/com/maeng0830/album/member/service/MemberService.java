package com.maeng0830.album.member.service;

import static com.maeng0830.album.member.exception.MemberExceptionCode.EXIST_NICKNAME;
import static com.maeng0830.album.member.exception.MemberExceptionCode.EXIST_USERNAME;
import static com.maeng0830.album.member.exception.MemberExceptionCode.NOT_EXIST_MEMBER;
import static com.maeng0830.album.member.exception.MemberExceptionCode.NOT_SAME_PASSWORD_REPASSWORD;
import static com.maeng0830.album.member.exception.MemberExceptionCode.REQUIRED_LOGIN;

import com.maeng0830.album.common.exception.AlbumException;
import com.maeng0830.album.common.filedir.FileDir;
import com.maeng0830.album.common.model.image.Image;
import com.maeng0830.album.member.domain.Member;
import com.maeng0830.album.member.domain.MemberRole;
import com.maeng0830.album.member.domain.MemberStatus;
import com.maeng0830.album.member.dto.MemberDto;
import com.maeng0830.album.member.dto.request.MemberJoinForm;
import com.maeng0830.album.member.dto.request.MemberModifiedForm;
import com.maeng0830.album.member.repository.MemberRepository;
import com.maeng0830.album.security.dto.LoginType;
import java.io.File;
import java.io.IOException;
import java.util.List;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

@Slf4j
@Service
@RequiredArgsConstructor
public class MemberService {

	private final MemberRepository memberRepository;
	private final BCryptPasswordEncoder passwordEncoder;
	private final FileDir fileDir;

	public MemberDto join(MemberJoinForm memberJoinForm) {
		// username 존재 여부 확인
		List<Member> members = memberRepository.findByUsernameOrNickname(
				memberJoinForm.getUsername(), memberJoinForm.getNickname());

		for (Member member : members) {
			if (memberJoinForm.getUsername().equals(member.getUsername())) {
				throw new AlbumException(EXIST_USERNAME);
			} else if (memberJoinForm.getNickname().equals(member.getNickname())) {
				throw new AlbumException(EXIST_NICKNAME);
			}
		}

		// 비밀번호 암호화, 상태 및 권한 설정 -> DB 저장(회원가입)
		Member member = Member.builder()
				.username(memberJoinForm.getUsername())
				.nickname(memberJoinForm.getNickname())
				.password(passwordEncoder.encode(memberJoinForm.getPassword()))
				.phone(memberJoinForm.getPhone())
				.birthDate(memberJoinForm.getBirthDate())
				.status(MemberStatus.FIRST)
				.role(MemberRole.ROLE_MEMBER)
				.loginType(LoginType.FORM)
				.build();

		Member saveMember = memberRepository.save(member);

		// 회원가입 멤버 반환
		return MemberDto.from(saveMember);
	}

	public MemberDto withdraw(MemberDto memberDto) {

		if (memberDto == null) {
			throw new AlbumException(REQUIRED_LOGIN);
		}

		Member findMember = memberRepository.findById(memberDto.getId())
				.orElseThrow(() -> new AlbumException(NOT_EXIST_MEMBER));

		findMember.setStatus(MemberStatus.WITHDRAW);

		return MemberDto.from(findMember);
	}

	public List<MemberDto> getMembers() {
		List<Member> members = memberRepository.findAll();
		return members.stream().map(MemberDto::from).collect(Collectors.toList());
	}

	public MemberDto getMember(Long id) {
		Member findMember = memberRepository.findById(id)
				.orElseThrow(() -> new AlbumException(NOT_EXIST_MEMBER));

		return MemberDto.from(findMember);
	}

	public MemberDto modifiedMember(MemberDto loginMemberDto,
									MemberModifiedForm memberModifiedForm,
									MultipartFile imageFile) {
		if (loginMemberDto == null) {
			throw new AlbumException(REQUIRED_LOGIN);
		}

		Member findMember = memberRepository.findById(loginMemberDto.getId())
				.orElseThrow(() -> new AlbumException(
						NOT_EXIST_MEMBER));

		log.info("이미지= {}", imageFile.getOriginalFilename());

		// json 요청 데이터 처리
		findMember.setNickname(memberModifiedForm.getNickname());
		findMember.setPhone(memberModifiedForm.getPhone());

		// 비밀번호 확인
		if (memberModifiedForm.getPassword().equals(memberModifiedForm.getRePassword())) {
			findMember.setPassword(passwordEncoder.encode(memberModifiedForm.getPassword()));
		} else {
			throw new AlbumException(NOT_SAME_PASSWORD_REPASSWORD);
		}

		// multipart 요청 데이터(회원 이미지) 처리
		saveMemberImage(imageFile, findMember);

		return MemberDto.from(findMember);
	}

	@Transactional
	public MemberDto changeMemberStatus(Long id, MemberStatus memberStatus) {
		Member findMember = memberRepository.findById(id).orElseThrow(() -> new AlbumException(
				NOT_EXIST_MEMBER));

		findMember.setStatus(memberStatus);

		return MemberDto.from(findMember);
	}

	// 회원 이미지 저장
	public void saveMemberImage(MultipartFile imageFile, Member findMember) {
		if (imageFile != null) {
			findMember.setImage(new Image(imageFile, fileDir));

			try {
				imageFile.transferTo(new File(findMember.getImage().getImagePath()));
			} catch (IOException e) {
				throw new RuntimeException(e);
			}
		}
	}
}

package com.maeng0830.album.member.service;

import static com.maeng0830.album.member.exception.MemberExceptionCode.EXIST_NICKNAME;
import static com.maeng0830.album.member.exception.MemberExceptionCode.EXIST_USERNAME;
import static com.maeng0830.album.member.exception.MemberExceptionCode.NOT_EXIST_MEMBER;

import com.maeng0830.album.common.exception.AlbumException;
import com.maeng0830.album.common.filedir.FileDir;
import com.maeng0830.album.common.model.image.Image;
import com.maeng0830.album.member.domain.Member;
import com.maeng0830.album.member.domain.MemberRole;
import com.maeng0830.album.member.domain.MemberStatus;
import com.maeng0830.album.member.dto.MemberDto;
import com.maeng0830.album.member.repository.MemberRepository;
import com.maeng0830.album.security.dto.LoginType;
import java.io.File;
import java.io.IOException;
import java.util.List;
import java.util.Optional;
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

	public MemberDto join(MemberDto memberDto) {
		// username 존재 여부 확인
		Optional<Member> optionalMember = memberRepository.findByUsernameOrNickname(
				memberDto.getUsername(), memberDto.getNickname());
		if (optionalMember.isPresent()) {
			Member member = optionalMember.get();

			if (memberDto.getUsername().equals(member.getUsername())) {
				throw new AlbumException(EXIST_USERNAME);
			} else {
				throw new AlbumException(EXIST_NICKNAME);
			}
		}

		// 비밀번호 암호화, 상태 및 권한 설정 -> DB 저장(회원가입)
		memberDto.setPassword(passwordEncoder.encode(memberDto.getPassword()));
		memberDto.setStatus(MemberStatus.FIRST);
		memberDto.setRole(MemberRole.ROLE_MEMBER);
		memberDto.setLoginType(LoginType.FORM);
		Member saveMember = memberRepository.save(Member.from(memberDto));

		// 회원가입 멤버 반환
		return MemberDto.from(saveMember);
	}

	public MemberDto withdraw(MemberDto memberDto) {

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

	public MemberDto modifiedMember(Long id, MemberDto memberDto, MultipartFile imageFile) {
		Member findMember = memberRepository.findById(id).orElseThrow(() -> new AlbumException(
				NOT_EXIST_MEMBER));

		log.info("이미지= {}", imageFile.getOriginalFilename());

		// json 요청 데이터 처리
		findMember.setNickname(memberDto.getNickname());
		findMember.setPhone(memberDto.getPhone());
		findMember.setBirthDate(memberDto.getBirthDate());

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

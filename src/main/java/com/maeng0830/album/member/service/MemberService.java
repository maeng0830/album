package com.maeng0830.album.member.service;

import static com.maeng0830.album.member.domain.MemberStatus.*;
import static com.maeng0830.album.member.domain.MemberStatus.FIRST;
import static com.maeng0830.album.member.domain.MemberStatus.LOCKED;
import static com.maeng0830.album.member.domain.MemberStatus.WITHDRAW;
import static com.maeng0830.album.member.exception.MemberExceptionCode.ALREADY_SET_REQUIRED_OAUTH2_PASSWORD;
import static com.maeng0830.album.member.exception.MemberExceptionCode.EXIST_NICKNAME;
import static com.maeng0830.album.member.exception.MemberExceptionCode.EXIST_USERNAME;
import static com.maeng0830.album.member.exception.MemberExceptionCode.INCORRECT_PASSWORD;
import static com.maeng0830.album.member.exception.MemberExceptionCode.NOT_EXIST_MEMBER;
import static com.maeng0830.album.member.exception.MemberExceptionCode.NOT_OAUTH2_LOGIN_MEMBER;
import static com.maeng0830.album.member.exception.MemberExceptionCode.NOT_SAME_PASSWORD_REPASSWORD;
import static com.maeng0830.album.member.exception.MemberExceptionCode.NO_AUTHORITY;
import static com.maeng0830.album.member.exception.MemberExceptionCode.REQUIRED_LOGIN;

import com.maeng0830.album.common.aws.AwsS3Manager;
import com.maeng0830.album.common.exception.AlbumException;
import com.maeng0830.album.common.filedir.FileDir;
import com.maeng0830.album.common.image.DefaultImage;
import com.maeng0830.album.common.model.image.Image;
import com.maeng0830.album.member.domain.Member;
import com.maeng0830.album.member.domain.MemberRole;
import com.maeng0830.album.member.domain.MemberStatus;
import com.maeng0830.album.member.dto.MemberDto;
import com.maeng0830.album.member.dto.request.MemberChangeStatusForm;
import com.maeng0830.album.member.dto.request.MemberJoinForm;
import com.maeng0830.album.member.dto.request.MemberModifiedForm;
import com.maeng0830.album.member.dto.request.MemberPasswordModifiedForm;
import com.maeng0830.album.member.dto.request.MemberWithdrawForm;
import com.maeng0830.album.member.dto.request.Oauth2PasswordForm;
import com.maeng0830.album.member.repository.MemberRepository;
import com.maeng0830.album.security.dto.LoginType;
import java.util.List;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.domain.Sort.Order;
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
	private final AwsS3Manager awsS3Manager;
	private final FileDir fileDir;
	private final DefaultImage defaultImage;

	// 폼 회원가입
	public MemberDto join(MemberJoinForm memberJoinForm) {
		// username 및 nickname 존재 여부 확인
		List<Member> members = memberRepository.findByUsernameOrNickname(
				memberJoinForm.getUsername(), memberJoinForm.getNickname());

		for (Member member : members) {
			if (memberJoinForm.getUsername().equals(member.getUsername())) {
				throw new AlbumException(EXIST_USERNAME);
			} else if (memberJoinForm.getNickname().equals(member.getNickname())) {
				throw new AlbumException(EXIST_NICKNAME);
			}
		}

		// 비밀번호, 확인 비밀번호 일치 확인
		if (!memberJoinForm.getPassword().equals(memberJoinForm.getCheckedPassword())) {
			throw new AlbumException(NOT_SAME_PASSWORD_REPASSWORD);
		}

		// 비밀번호 암호화, 상태 및 권한 설정 -> DB 저장(회원가입)
		Member member = Member.builder()
				.username(memberJoinForm.getUsername())
				.nickname(memberJoinForm.getNickname())
				.password(passwordEncoder.encode(memberJoinForm.getPassword()))
				.phone(memberJoinForm.getPhone())
				.status(FIRST)
				.role(MemberRole.ROLE_MEMBER)
				.loginType(LoginType.FORM)
				.image(Image.createDefaultImage(fileDir, defaultImage.getMemberImage()))
				.build();

		Member saveMember = memberRepository.save(member);

		// 회원가입 멤버 반환
		return MemberDto.from(saveMember);
	}

	// 회원 탈퇴
	@Transactional
	public MemberDto withdraw(MemberDto memberDto, MemberWithdrawForm memberWithdrawForm) {

		if (memberDto == null) {
			throw new AlbumException(REQUIRED_LOGIN);
		}

		Member findMember = memberRepository.findById(memberDto.getId())
				.orElseThrow(() -> new AlbumException(NOT_EXIST_MEMBER));

		if (!memberWithdrawForm.getPassword().equals(memberWithdrawForm.getCheckedPassword())) {
			throw new AlbumException(NOT_SAME_PASSWORD_REPASSWORD);
		}

		if (!passwordEncoder.matches(memberWithdrawForm.getPassword(), findMember.getPassword())) {
			throw new AlbumException(INCORRECT_PASSWORD);
		}

		findMember.changeStatus(WITHDRAW);

		return MemberDto.from(findMember);
	}

	// 일반 회원용 회원 목록 조회
	public Page<MemberDto> getMembers(String searchText, Pageable pageable) {
		// 페이징 조건
		List<MemberStatus> statuses = List.of(NORMAL);
		PageRequest pageRequest = PageRequest.of(pageable.getPageNumber(), pageable.getPageSize(),
				Sort.by(Order.desc("createdAt"), Order.asc("status")));

		Page<Member> members = memberRepository.searchBySearchText(statuses, searchText, pageRequest);

		return members.map(MemberDto::from);
	}

	// 관리자용 회원 목록 조회
	public Page<MemberDto> getMembersForAdmin(MemberDto memberDto, String searchText,
											  Pageable pageable) {
		// 로그인 상태 및 권한 확인
		if (memberDto != null) {
			if (memberDto.getRole() != MemberRole.ROLE_ADMIN) {
				throw new AlbumException(NO_AUTHORITY);
			}
		} else {
			throw new AlbumException(REQUIRED_LOGIN);
		}

		// 데이터 조회 조건 설정
		List<MemberStatus> statuses = List.of(FIRST, NORMAL, LOCKED, WITHDRAW);

		PageRequest pageRequest = PageRequest.of(pageable.getPageNumber(), pageable.getPageSize(),
				Sort.by(Order.asc("status"), Order.desc("createdAt")));

		// 데이터 조회
		Page<Member> members = memberRepository.searchBySearchText(statuses, searchText,
				pageRequest);

		return members.map(MemberDto::from);
	}

	public MemberDto getMember(Long id) {
		Member findMember = memberRepository.findById(id)
				.orElseThrow(() -> new AlbumException(NOT_EXIST_MEMBER));

		return MemberDto.from(findMember);
	}

	@Transactional
	public MemberDto modifiedMember(MemberDto loginMemberDto,
									MemberModifiedForm memberModifiedForm,
									MultipartFile imageFile) {
		if (loginMemberDto == null) {
			throw new AlbumException(REQUIRED_LOGIN);
		}

		Member findMember = memberRepository.findById(loginMemberDto.getId())
				.orElseThrow(() -> new AlbumException(
						NOT_EXIST_MEMBER));

		// json 요청 데이터 처리
		// 닉네임 중복 체크
		Optional<Member> checkNickname = memberRepository.findByNickname(
				memberModifiedForm.getNickname());

		if (!findMember.getNickname().equals(memberModifiedForm.getNickname()) && checkNickname.isPresent()) {
			throw new AlbumException(EXIST_NICKNAME);
		}

		findMember.modifiedBasicInfo(memberModifiedForm);

		// multipart 요청 데이터(회원 이미지) 처리
		Image image = awsS3Manager.uploadImage(imageFile);

		saveMemberImage(image, findMember);

		return MemberDto.from(findMember);
	}

	@Transactional
	public MemberDto modifiedMemberPassword(MemberDto loginMemberDto,
											MemberPasswordModifiedForm memberPasswordModifiedForm) {
		if (loginMemberDto == null) {
			throw new AlbumException(REQUIRED_LOGIN);
		}

		Member findMember = memberRepository.findById(loginMemberDto.getId())
				.orElseThrow(() -> new AlbumException(
						NOT_EXIST_MEMBER));

		// 비밀번호 확인
		if (memberPasswordModifiedForm.getModPassword().equals(memberPasswordModifiedForm.getCheckedModPassword())) {
			if (passwordEncoder.matches(memberPasswordModifiedForm.getCurrentPassword(),
					findMember.getPassword())) {
				findMember.changePassword(passwordEncoder.encode(memberPasswordModifiedForm.getModPassword()));
			} else {
				throw new AlbumException(INCORRECT_PASSWORD);
			}
		} else {
			throw new AlbumException(NOT_SAME_PASSWORD_REPASSWORD);
		}

		return MemberDto.from(findMember);
	}

	@Transactional
	public MemberDto setOauth2Password(MemberDto loginMemberDto, Oauth2PasswordForm oauth2PasswordForm) {
		// 로그인 확인
		if (loginMemberDto == null) {
			throw new AlbumException(REQUIRED_LOGIN);
		}

		// 소셜 로그인 여부, 필수 비밀번호 세팅 완료 여부 확인
		if (loginMemberDto.getLoginType() == LoginType.FORM) {
			throw new AlbumException(NOT_OAUTH2_LOGIN_MEMBER);
		} else if (loginMemberDto.getStatus() != FIRST) {
			throw new AlbumException(ALREADY_SET_REQUIRED_OAUTH2_PASSWORD);
		}

		Member findMember = memberRepository.findById(loginMemberDto.getId())
				.orElseThrow(() -> new AlbumException(NOT_EXIST_MEMBER));

		// 비밀 번호 변경
		if (oauth2PasswordForm.getPassword().equals(oauth2PasswordForm.getCheckedPassword())) {
			findMember.changePassword(passwordEncoder.encode(oauth2PasswordForm.getPassword()));
		} else {
			throw new AlbumException(NOT_SAME_PASSWORD_REPASSWORD);
		}

		return MemberDto.from(findMember);
	}

	@Transactional
	public MemberDto changeMemberStatus(MemberChangeStatusForm memberChangeStatusForm) {
		Member findMember = memberRepository.findById(memberChangeStatusForm.getId()).orElseThrow(() -> new AlbumException(
				NOT_EXIST_MEMBER));

		findMember.changeStatus(memberChangeStatusForm.getMemberStatus());

		return MemberDto.from(findMember);
	}

	// 회원 이미지 저장
	private void saveMemberImage(Image image, Member findMember) {
		if (image != null) {
			findMember.changeImage(image);
		} else {
			findMember.changeImage(Image.createDefaultImage(fileDir, defaultImage.getMemberImage()));
		}
	}
}

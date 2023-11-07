package com.maeng0830.album.member.service;

import static com.maeng0830.album.member.domain.MemberRole.ROLE_ADMIN;
import static com.maeng0830.album.member.domain.MemberRole.ROLE_MEMBER;
import static com.maeng0830.album.member.domain.MemberStatus.FIRST;
import static com.maeng0830.album.member.domain.MemberStatus.LOCKED;
import static com.maeng0830.album.member.domain.MemberStatus.NORMAL;
import static com.maeng0830.album.member.domain.MemberStatus.WITHDRAW;
import static com.maeng0830.album.member.exception.MemberExceptionCode.ALREADY_SET_REQUIRED_OAUTH2_PASSWORD;
import static com.maeng0830.album.member.exception.MemberExceptionCode.EXIST_NICKNAME;
import static com.maeng0830.album.member.exception.MemberExceptionCode.EXIST_USERNAME;
import static com.maeng0830.album.member.exception.MemberExceptionCode.INCORRECT_PASSWORD;
import static com.maeng0830.album.member.exception.MemberExceptionCode.NOT_OAUTH2_LOGIN_MEMBER;
import static com.maeng0830.album.member.exception.MemberExceptionCode.NOT_SAME_PASSWORD_REPASSWORD;
import static com.maeng0830.album.security.dto.LoginType.FORM;
import static com.maeng0830.album.security.dto.LoginType.OAUTH_GOOGLE;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.BDDMockito.given;

import com.maeng0830.album.common.exception.AlbumException;
import com.maeng0830.album.common.filedir.FileDir;
import com.maeng0830.album.common.image.DefaultImage;
import com.maeng0830.album.common.model.image.Image;
import com.maeng0830.album.member.domain.Member;
import com.maeng0830.album.member.domain.MemberStatus;
import com.maeng0830.album.member.dto.MemberDto;
import com.maeng0830.album.member.dto.request.MemberChangeStatusForm;
import com.maeng0830.album.member.dto.request.MemberJoinForm;
import com.maeng0830.album.member.dto.request.MemberModifiedForm;
import com.maeng0830.album.member.dto.request.MemberPasswordModifiedForm;
import com.maeng0830.album.member.dto.request.MemberWithdrawForm;
import com.maeng0830.album.member.dto.request.Oauth2PasswordForm;
import com.maeng0830.album.member.repository.MemberRepository;
import com.maeng0830.album.member.service.MemberService;
import com.maeng0830.album.security.dto.LoginType;
import com.maeng0830.album.support.ServiceTestSupport;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

class MemberServiceTest extends ServiceTestSupport {

	@Autowired
	private MemberService memberService;
	@Autowired
	private MemberRepository memberRepository;
	@Autowired
	private BCryptPasswordEncoder passwordEncoder;
	@Autowired
	private FileDir fileDir;
	@Autowired
	private DefaultImage defaultImage;

	@DisplayName("회원가입을 할 수 있다.")
	@Test
	public void join() {
		// given
		MemberJoinForm memberJoinForm = MemberJoinForm.builder()
				.username("test@naver.com")
				.nickname("testNickname")
				.phone("01000000000")
				.password("!@asd123")
				.checkedPassword("!@asd123")
				.build();

		//when
		MemberDto result = memberService.join(memberJoinForm);

		//then
		assertThat(result)
				.extracting("username", "nickname", "phone", "status", "role", "loginType")
				.containsExactlyInAnyOrder(
						memberJoinForm.getUsername(),
						memberJoinForm.getNickname(),
						memberJoinForm.getPhone(),
						FIRST,
						ROLE_MEMBER,
						FORM);
		assertThat(passwordEncoder.matches(memberJoinForm.getPassword(), result.getPassword()))
				.isTrue();
		assertThat(result.getImage())
				.usingRecursiveComparison()
				.isEqualTo(Image.createDefaultImage(fileDir, defaultImage.getMemberImage()));
	}

	@DisplayName("username이 중복인 경우, 회원가입 시 예외가 발생한다.")
	@Test
	void join_existUsername() {
		// given
		// 기존 회원
		Member existMember = Member.builder()
				.username("test@naver.com")
				.nickname("testNickname")
				.build();
		memberRepository.save(existMember);

		// 회원가입 정보
		MemberJoinForm memberJoinForm = MemberJoinForm.builder()
				.username("test@naver.com")
				.nickname("testNickname2")
				.phone("01000000000")
				.password("!@asd123")
				.checkedPassword("!@asd123")
				.build();

		// when
		assertThatThrownBy(() -> memberService.join(memberJoinForm))
				.isInstanceOf(AlbumException.class)
				.hasMessage(EXIST_USERNAME.getMessage());
	}

	@DisplayName("nickname이 중복인 경우, 회원가입 시 예외가 발생한다.")
	@Test
	void join_existNickname() {
		// given
		// 기존 회원
		Member existMember = Member.builder()
				.username("test@naver.com")
				.nickname("testNickname")
				.build();
		memberRepository.save(existMember);

		// 회원가입 정보
		MemberJoinForm memberJoinForm = MemberJoinForm.builder()
				.username("test2@naver.com")
				.nickname("testNickname")
				.phone("01000000000")
				.password("!@asd123")
				.checkedPassword("!@asd123")
				.build();

		// when
		assertThatThrownBy(() -> memberService.join(memberJoinForm))
				.isInstanceOf(AlbumException.class)
				.hasMessage(EXIST_NICKNAME.getMessage());
	}

	@DisplayName("본인인 경우, 회원을 탈퇴할 수 있다(WITHDRAW).")
	@Test
	public void withdraw() {
		//given
		Member member = Member.builder()
				.password(passwordEncoder.encode("!@asd123"))
				.build();
		memberRepository.save(member);

		MemberDto memberDto = MemberDto.from(member);

		MemberWithdrawForm memberWithdrawForm = MemberWithdrawForm.builder()
				.password("!@asd123")
				.checkedPassword("!@asd123")
				.build();

		//when
		MemberDto result = memberService.withdraw(memberDto, memberWithdrawForm);

		//then
		assertThat(result.getStatus()).isEqualTo(WITHDRAW);
	}

	@DisplayName("일반회원은 NORMAL 상태인 회원 목록을 조회할 수 있다. "
			+ "searchText가 null이면 전체 회원 목록을, "
			+ "null이 아니면 searchText와 username 또는 nickname이 전방 일치하는 회원 목록을 조회한다.")
	@CsvSource(value = {", 15", "searchUsername, 5", "searchNickname, 10"})
	@ParameterizedTest
	public void getMembers(String searchText, int size) {
		//given
		List<Member> members = new ArrayList<>();

		// NORMAL 상태의 회원
		for (int i = 0; i < 15; i++) {
			if (i <= 4) {
				Member member = Member.builder()
						.username("searchUsername")
						.status(NORMAL)
						.build();
				members.add(member);
			} else {
				Member member = Member.builder()
						.nickname("searchNickname")
						.status(NORMAL)
						.build();
				members.add(member);
			}
		}

		// LOCKED 또는 WITHDRAW 상태의 회원
		for (int i = 0; i < 15; i++) {
			if (i <= 4) {
				Member member = Member.builder()
						.username("searchUsername")
						.status(LOCKED)
						.build();
				members.add(member);
			} else {
				Member member = Member.builder()
						.nickname("searchNickname")
						.status(WITHDRAW)
						.build();
				members.add(member);
			}
		}

		memberRepository.saveAll(members);

		//when
		Page<MemberDto> result = memberService.getMembers(searchText, PageRequest.of(0, 20));

		//then
		assertThat(result.getContent().size()).isEqualTo(size);
	}

	@DisplayName("관리자는 FIRST, NORMAL, LOCKED, WITHDRAW 상태인 회원 목록을 조회할 수 있다. "
			+ "searchText가 null이면 전체 회원 목록을, "
			+ "null이 아니면 searchText와 username 또는 nickname이 전방 일치하는 회원 목록을 조회한다.")
	@CsvSource(value = {", 15, 15, 15, 15", "searchUsername, 5, 5, 5, 5",
			"searchNickname, 10, 10, 10, 10"})
	@ParameterizedTest
	public void getMembersForAdmin(String searchText, int firstSize, int normalSize, int lockedSize,
								   int withdrawSize) {
		//given
		List<Member> members = new ArrayList<>();

		// FIRST 상태의 회원
		for (int i = 0; i < 15; i++) {
			if (i <= 4) {
				Member member = Member.builder()
						.username("searchUsername")
						.status(FIRST)
						.build();
				members.add(member);
			} else {
				Member member = Member.builder()
						.nickname("searchNickname")
						.status(FIRST)
						.build();
				members.add(member);
			}
		}

		// NORMAL 상태의 회원
		for (int i = 0; i < 15; i++) {
			if (i <= 4) {
				Member member = Member.builder()
						.username("searchUsername")
						.status(NORMAL)
						.build();
				members.add(member);
			} else {
				Member member = Member.builder()
						.nickname("searchNickname")
						.status(NORMAL)
						.build();
				members.add(member);
			}
		}

		// LOCKED 상태의 회원
		for (int i = 0; i < 15; i++) {
			if (i <= 4) {
				Member member = Member.builder()
						.username("searchUsername")
						.status(LOCKED)
						.build();
				members.add(member);
			} else {
				Member member = Member.builder()
						.nickname("searchNickname")
						.status(LOCKED)
						.build();
				members.add(member);
			}
		}

		// WITHDRAW 상태의 회원
		for (int i = 0; i < 15; i++) {
			if (i <= 4) {
				Member member = Member.builder()
						.username("searchUsername")
						.status(WITHDRAW)
						.build();
				members.add(member);
			} else {
				Member member = Member.builder()
						.nickname("searchNickname")
						.status(WITHDRAW)
						.build();
				members.add(member);
			}
		}

		memberRepository.saveAll(members);

		// 관리자 MemberDto
		MemberDto adminDto = MemberDto.builder()
				.role(ROLE_ADMIN)
				.build();

		//when
		Page<MemberDto> findMembers = memberService.getMembersForAdmin(adminDto, searchText,
				PageRequest.of(0, 60));
		List<MemberDto> firstMembers = findMembers.getContent().stream()
				.filter(m -> m.getStatus().equals(FIRST))
				.collect(Collectors.toList());
		List<MemberDto> normalMembers = findMembers.getContent().stream()
				.filter(m -> m.getStatus().equals(NORMAL))
				.collect(Collectors.toList());
		List<MemberDto> lockedMembers = findMembers.getContent().stream()
				.filter(m -> m.getStatus().equals(LOCKED))
				.collect(Collectors.toList());
		List<MemberDto> withdrawMembers = findMembers.getContent().stream()
				.filter(m -> m.getStatus().equals(WITHDRAW))
				.collect(Collectors.toList());

		//then
		assertThat(firstMembers).hasSize(firstSize);
		assertThat(firstMembers).hasSize(normalSize);
		assertThat(firstMembers).hasSize(lockedSize);
		assertThat(firstMembers).hasSize(withdrawSize);

	}

	@DisplayName("주어진 ID에 해당하는 회원을 조회할 수 있다.")
	@Test
	public void getMember() {
		//given
		Member member1 = Member.builder()
				.build();
		Member member2 = Member.builder()
				.build();
		Member member3 = Member.builder()
				.build();

		memberRepository.saveAll(List.of(member1, member2, member3));

		//when
		MemberDto result1 = memberService.getMember(member1.getId());
		MemberDto result2 = memberService.getMember(member2.getId());
		MemberDto result3 = memberService.getMember(member3.getId());

		//then
		assertThat(result1).usingRecursiveComparison().isEqualTo(MemberDto.from(member1));
		assertThat(result2).usingRecursiveComparison().isEqualTo(MemberDto.from(member2));
		assertThat(result3).usingRecursiveComparison().isEqualTo(MemberDto.from(member3));
	}

	@DisplayName("본인인 경우, 회원 정보를 수정할 수 있다.")
	@Test
	public void modifiedMember() throws IOException {
		//given
		// 로그인 회원
		Member loginMember = Member.builder()
				.nickname("prevNickname")
				.phone("01000000000")
				.password(passwordEncoder.encode("!@asd123"))
				.build();
		memberRepository.save(loginMember);
		MemberDto loginMemberDto = MemberDto.from(loginMember);

		// 수정 정보
		LocalDate modifiedBirthDate = LocalDate.now();
		MemberModifiedForm memberModifiedForm = MemberModifiedForm.builder()
				.nickname("nextNickname")
				.phone("01011111111")
				.birthDate(modifiedBirthDate)
				.build();

		MockMultipartFile imageFile = createImageFile("imageFile", "testImage.PNG",
				"multipart/mixed", fileDir);

		// Image 객체
		String storeName = UUID.randomUUID() + "." + imageFile.getOriginalFilename();

		Image image = Image.builder()
				.imageOriginalName(imageFile.getOriginalFilename())
				.imageStoreName(storeName)
				.imagePath(fileDir.getDir() + storeName)
				.build();

		// stub
		given(awsS3Manager.uploadImage(imageFile))
				.willReturn(
						image
				);

		//when
		MemberDto result = memberService.modifiedMember(loginMemberDto, memberModifiedForm,
				imageFile);

		//then
		assertThat(result.getNickname())
				.isEqualTo(memberModifiedForm.getNickname());
		assertThat(result.getPhone())
				.isEqualTo(memberModifiedForm.getPhone());
		assertThat(result.getBirthDate())
				.isEqualTo(memberModifiedForm.getBirthDate());
		assertThat(result.getImage().getImageOriginalName())
				.isEqualTo(image.getImageOriginalName());
		assertThat(result.getImage().getImageStoreName())
				.isEqualTo(image.getImageStoreName());
		assertThat(result.getImage().getImagePath())
				.isEqualTo(image.getImagePath());
	}

	@DisplayName("본인인 경우, 비밀번호를 수정할 수 있다.")
	@Test
	void modifiedMemberPassword() {
		// given
		// 로그인 회원
		Member loginMember = Member.builder()
				.password(passwordEncoder.encode("!@asd123"))
				.build();
		memberRepository.save(loginMember);
		MemberDto loginMemberDto = MemberDto.from(loginMember);

		MemberPasswordModifiedForm memberPasswordModifiedForm = MemberPasswordModifiedForm.builder()
				.currentPassword("!@asd123")
				.modPassword("!@asd1234")
				.checkedModPassword("!@asd1234")
				.build();

		// when
		MemberDto result = memberService.modifiedMemberPassword(loginMemberDto,
				memberPasswordModifiedForm);

		// then
		assertThat(passwordEncoder.matches(memberPasswordModifiedForm.getModPassword(),
				result.getPassword()))
				.isTrue();
	}

	@DisplayName("현재 비밀번호가 일치하지 않는 경우, 비밀번호 변경 시 예외가 발생한다.")
	@Test
	void modifiedMemberPassword_incorrectPassword() {
		// given
		// 로그인 회원
		Member loginMember = Member.builder()
				.password(passwordEncoder.encode("!@asd123"))
				.build();
		memberRepository.save(loginMember);
		MemberDto loginMemberDto = MemberDto.from(loginMember);

		MemberPasswordModifiedForm memberPasswordModifiedForm = MemberPasswordModifiedForm.builder()
				.currentPassword("!@asd1234")
				.modPassword("!@asd12345")
				.checkedModPassword("!@asd12345")
				.build();

		// when
		assertThatThrownBy(() -> memberService.modifiedMemberPassword(loginMemberDto,
				memberPasswordModifiedForm))
				.isInstanceOf(AlbumException.class)
				.hasMessage(INCORRECT_PASSWORD.getMessage());

		// then
	}

	@DisplayName("변경 비밀번호와 확인 변경 비밀번호가 일치하지 않는 경우, 비밀번호 변경 시 예외가 발생한다.")
	@Test
	void modifiedMemberPassword_notSamePasswordRepassword() {
		// given
		// 로그인 회원
		Member loginMember = Member.builder()
				.password(passwordEncoder.encode("!@asd123"))
				.build();
		memberRepository.save(loginMember);
		MemberDto loginMemberDto = MemberDto.from(loginMember);

		MemberPasswordModifiedForm memberPasswordModifiedForm = MemberPasswordModifiedForm.builder()
				.currentPassword("!@asd123")
				.modPassword("!@asd1234")
				.checkedModPassword("!@asd12345")
				.build();

		// when
		assertThatThrownBy(() -> memberService.modifiedMemberPassword(loginMemberDto,
				memberPasswordModifiedForm))
				.isInstanceOf(AlbumException.class)
				.hasMessage(NOT_SAME_PASSWORD_REPASSWORD.getMessage());

		// then
	}

	@DisplayName("소셜 로그인한 경우, 필수 비밀번호 세팅을 할 수 있다.")
	@CsvSource({"OAUTH_GOOGLE", "OAUTH_NAVER"})
	@ParameterizedTest
	void setOauth2Password(LoginType loginType) {
		// given
		// 로그인 회원
		Member loginMember = Member.builder()
				.password(passwordEncoder.encode(testOauth2Password))
				.loginType(loginType)
				.status(FIRST)
				.build();
		memberRepository.save(loginMember);
		MemberDto loginMemberDto = MemberDto.from(loginMember);

		Oauth2PasswordForm oauth2PasswordForm = Oauth2PasswordForm.builder()
				.password("!@asd123")
				.checkedPassword("!@asd123")
				.build();

		// when
		MemberDto result = memberService.setOauth2Password(loginMemberDto,
				oauth2PasswordForm);

		// then
		assertThat(passwordEncoder.matches(oauth2PasswordForm.getPassword(),
				result.getPassword()))
				.isTrue();
	}

	@DisplayName("소셜 로그인이 아닌 경우, 필수 비밀번호 세팅 시 예외가 발생한다")
	@Test
	void setOauth2Password_notOauth2LoginMember() {
		// given
		// 로그인 회원
		Member loginMember = Member.builder()
				.password(passwordEncoder.encode(testOauth2Password))
				.loginType(FORM)
				.status(FIRST)
				.build();
		memberRepository.save(loginMember);
		MemberDto loginMemberDto = MemberDto.from(loginMember);

		Oauth2PasswordForm oauth2PasswordForm = Oauth2PasswordForm.builder()
				.password("!@asd123")
				.checkedPassword("!@asd123")
				.build();

		// when
		assertThatThrownBy(() -> memberService.setOauth2Password(loginMemberDto,
				oauth2PasswordForm))
				.isInstanceOf(AlbumException.class)
				.hasMessage(NOT_OAUTH2_LOGIN_MEMBER.getMessage());
		// then
	}

	@DisplayName("회원 상태가 FIRST가 아닌 경우, 필수 비밀번호 세팅 시 예외가 발생한다")
	@CsvSource({"NORMAL", "LOCKED", "WITHDRAW"})
	@ParameterizedTest
	void setOauth2Password_alreadySetRequiredOauth2Password(MemberStatus memberStatus) {
		// given
		// 로그인 회원
		Member loginMember = Member.builder()
				.password(passwordEncoder.encode(testOauth2Password))
				.loginType(OAUTH_GOOGLE)
				.status(memberStatus)
				.build();
		memberRepository.save(loginMember);
		MemberDto loginMemberDto = MemberDto.from(loginMember);

		Oauth2PasswordForm oauth2PasswordForm = Oauth2PasswordForm.builder()
				.password("!@asd123")
				.checkedPassword("!@asd123")
				.build();

		// when
		assertThatThrownBy(() -> memberService.setOauth2Password(loginMemberDto,
				oauth2PasswordForm))
				.isInstanceOf(AlbumException.class)
				.hasMessage(ALREADY_SET_REQUIRED_OAUTH2_PASSWORD.getMessage());
		// then
	}

	@DisplayName("비밀번호와 확인 비밀번호가 일치하지 않는 경우, 필수 비밀번호 세팅 시 예외가 발생한다.")
	@Test
	void setOauth2Password_notSamePasswordRepassword() {
		// given
		// 로그인 회원
		Member loginMember = Member.builder()
				.password(passwordEncoder.encode(testOauth2Password))
				.loginType(OAUTH_GOOGLE)
				.status(FIRST)
				.build();
		memberRepository.save(loginMember);
		MemberDto loginMemberDto = MemberDto.from(loginMember);

		Oauth2PasswordForm oauth2PasswordForm = Oauth2PasswordForm.builder()
				.password("!@asd123")
				.checkedPassword("!@asd456")
				.build();

		// when
		assertThatThrownBy(() -> memberService.setOauth2Password(loginMemberDto,
				oauth2PasswordForm))
				.isInstanceOf(AlbumException.class)
				.hasMessage(NOT_SAME_PASSWORD_REPASSWORD.getMessage());
		// then
	}

	@DisplayName("회원 상태를 수정할 수 있다.")
	@CsvSource({"FIRST", "NORMAL", "LOCKED", "WITHDRAW"})
	@ParameterizedTest
	public void changeMemberStatus(MemberStatus status) {
		//given
		Member member = Member.builder()
				.build();
		memberRepository.save(member);

		// MemberChangeStatusForm 세팅
		MemberChangeStatusForm memberChangeStatusForm = MemberChangeStatusForm.builder()
				.id(member.getId())
				.memberStatus(status)
				.build();

		//when
		MemberDto result = memberService.changeMemberStatus(memberChangeStatusForm);

		//then
		assertThat(result.getId()).isEqualTo(member.getId());
		assertThat(result.getStatus()).isEqualTo(status);
	}

	private MockMultipartFile createImageFile(String name, String originalFilename,
											  String contentType, FileDir fileDir)
			throws IOException {
		String filePath = fileDir.getDir() + originalFilename;
		FileInputStream fileInputStream = new FileInputStream(new File(filePath));

		return new MockMultipartFile(name,
				originalFilename, contentType, fileInputStream);
	}
}
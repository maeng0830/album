package com.maeng0830.album.member.service;

import static com.maeng0830.album.member.domain.MemberRole.*;
import static com.maeng0830.album.member.domain.MemberStatus.*;
import static com.maeng0830.album.security.dto.LoginType.*;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.tuple;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.BDDMockito.given;

import com.maeng0830.album.common.filedir.FileDir;
import com.maeng0830.album.member.domain.Member;
import com.maeng0830.album.member.domain.MemberRole;
import com.maeng0830.album.member.domain.MemberStatus;
import com.maeng0830.album.member.dto.MemberDto;
import com.maeng0830.album.member.dto.request.MemberJoinForm;
import com.maeng0830.album.member.dto.request.MemberModifiedForm;
import com.maeng0830.album.member.repository.MemberRepository;
import com.maeng0830.album.security.dto.LoginType;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.annotation.Transactional;

@Transactional
@ActiveProfiles("test")
@SpringBootTest
class MemberServiceTest {

	@Autowired
	private MemberService memberService;

	@Autowired
	private MemberRepository memberRepository;

	@Autowired
	private BCryptPasswordEncoder passwordEncoder;

	@Autowired
	private FileDir fileDir;

	@DisplayName("회원가입을 할 수 있다.")
	@Test
	public void join() {
		// given
		MemberJoinForm memberJoinForm = MemberJoinForm.builder()
				.username("test@naver.com")
				.nickname("testNickname")
				.password("123")
				.build();

		//when
		MemberDto result = memberService.join(memberJoinForm);

		//then
		assertThat(result)
				.extracting("username", "nickname", "status", "role", "loginType")
				.containsExactlyInAnyOrder(
						memberJoinForm.getUsername(),
						memberJoinForm.getNickname(),
						FIRST,
						ROLE_MEMBER,
						FORM);
		assertThat(passwordEncoder.matches(memberJoinForm.getPassword(), result.getPassword()))
				.isTrue();
	}

	@DisplayName("본인인 경우, 회원을 탈퇴할 수 있다(WITHDRAW).")
	@Test
	public void withdraw() {
		//given
		Member member = Member.builder()
				.build();
		memberRepository.save(member);

		MemberDto memberDto = MemberDto.from(member);

		//when
		MemberDto result = memberService.withdraw(memberDto);

		//then
		assertThat(result.getStatus()).isEqualTo(WITHDRAW);
	}

	@DisplayName("전체 회원 목록을 조회할 수 있다.")
	@Test
	public void getMembers() {
		//given
		List<Member> members = new ArrayList<>();

		for (int i = 0; i < 10; i++) {
			Member member = Member.builder()
					.build();

			members.add(member);
		}

		memberRepository.saveAll(members);

		//when
		List<MemberDto> result = memberService.getMembers();

		//then
		assertThat(result.size()).isEqualTo(members.size());
		assertThat(result.get(0)).isInstanceOf(MemberDto.class);
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
		MemberDto result = memberService.getMember(member2.getId());

		//then
		assertThat(result).isInstanceOf(MemberDto.class);
		assertThat(result).usingRecursiveComparison().isEqualTo(MemberDto.from(member2));
	}

	@DisplayName("본인인 경우, 회원 정보를 수정할 수 있다.")
	@Test
	public void modifiedMember() throws IOException {
		//given
		Member loginMember = Member.builder()
				.nickname("prevNickname")
				.phone("010-0000-0000")
				.password(passwordEncoder.encode("123"))
				.build();
		memberRepository.save(loginMember);
		MemberDto loginMemberDto = MemberDto.from(loginMember);

		MemberModifiedForm memberModifiedForm = MemberModifiedForm.builder()
				.nickname("nextNickname")
				.phone("010-1111-1111")
				.password("456")
				.rePassword("456")
				.build();

		MockMultipartFile imageFile = createImageFile("imageFile", "testImage.png",
				"multipart/mixed", fileDir);

		//when
		MemberDto result = memberService.modifiedMember(loginMemberDto, memberModifiedForm,
				imageFile);

		//then
		assertThat(result.getNickname())
				.isEqualTo(memberModifiedForm.getNickname());
		assertThat(result.getPhone())
				.isEqualTo(memberModifiedForm.getPhone());
		assertThat(passwordEncoder.matches("456", result.getPassword()))
				.isTrue();
		assertThat(result.getImage().getImageOriginalName())
				.isEqualTo(imageFile.getOriginalFilename());
		assertThat(result.getImage().getImagePath())
				.isEqualTo(fileDir.getDir() + result.getImage().getImageStoreName());
	}

	@DisplayName("회원 상태를 수정할 수 있다.")
	@CsvSource({"FIRST", "NORMAL", "LOCKED", "WITHDRAW"})
	@ParameterizedTest
	public void changeMemberStatus(MemberStatus status) {
		//given
		Member member = Member.builder()
				.build();
		memberRepository.save(member);

		//when
		MemberDto result = memberService.changeMemberStatus(member.getId(), status);

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
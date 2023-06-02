package com.maeng0830.album.member.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.BDDMockito.given;

import com.maeng0830.album.common.filedir.FileDir;
import com.maeng0830.album.member.domain.Member;
import com.maeng0830.album.member.domain.MemberRole;
import com.maeng0830.album.member.domain.MemberStatus;
import com.maeng0830.album.member.dto.MemberDto;
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
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.test.context.ActiveProfiles;

@ActiveProfiles({"test"})
@ExtendWith(MockitoExtension.class)
class MemberServiceTest {

	@InjectMocks
	private MemberService memberService;

	@Mock
	private MemberRepository memberRepository;

	@Spy
	private BCryptPasswordEncoder passwordEncoder;

	@Spy
	private FileDir fileDir;

	@DisplayName("회원가입-성공")
	@Test
	public void join() {
		// given
		MemberDto memberDto = MemberDto.builder()
				.username("test@naver.com")
				.nickname("testNickname")
				.password("123")
				.build();

		given(memberRepository.findByUsernameOrNickname(anyString(), anyString()))
				.willReturn(Optional.empty());

		Member member = Member.builder()
				.id(1L)
				.username(memberDto.getUsername())
				.nickname(memberDto.getNickname())
				.password(passwordEncoder.encode(memberDto.getPassword()))
				.status(MemberStatus.FIRST)
				.role(MemberRole.ROLE_MEMBER)
				.loginType(LoginType.FORM)
				.build();

		given(memberRepository.save(any())).willReturn(member);

		//when
		MemberDto result = memberService.join(memberDto);

		//then
		assertThat(result).usingRecursiveComparison().isEqualTo(MemberDto.from(member));
	}

	@DisplayName("회원 탈퇴-성공")
	@Test
	public void withdraw() {
		//given
		MemberDto memberDto = MemberDto.builder()
				.id(1L)
				.status(MemberStatus.NORMAL)
				.build();

		Member member = Member.builder()
				.id(memberDto.getId())
				.status(memberDto.getStatus())
				.build();

		given(memberRepository.findById(anyLong())).willReturn(Optional.of(member));

		//when
		MemberDto result = memberService.withdraw(memberDto);

		//then
		assertThat(result.getStatus()).isEqualTo(MemberStatus.WITHDRAW);
	}

	@DisplayName("회원 목록 조회-성공")
	@Test
	public void getMembers() {
		//given
		List<Member> members = new ArrayList<>();

		for (int i = 0; i < 10; i++) {
			Member member = Member.builder()
					.id((long) (i + 1))
					.build();

			members.add(member);
		}

		given(memberRepository.findAll()).willReturn(members);

		//when
		List<MemberDto> result = memberService.getMembers();

		//then
		assertThat(result.size()).isEqualTo(members.size());
		assertThat(result.get(0)).isInstanceOf(MemberDto.class);
	}

	@DisplayName("특정 회원 조회-성공")
	@Test
	public void getMember() {
		//given
		long id = 1L;
		Member member = Member.builder()
				.id(1L)
				.build();

		given(memberRepository.findById(id)).willReturn(Optional.of(member));

		//when
		MemberDto result = memberService.getMember(id);

		//then
		assertThat(result).isInstanceOf(MemberDto.class);
		assertThat(result.getId()).isEqualTo(member.getId());
	}

	@DisplayName("회원 정보 수정-성공")
	@Test
	public void modifiedMember() throws IOException {
		//given
		long id = 1L;

		MemberDto memberDto = MemberDto.builder()
				.nickname("testNickName")
				.phone("010-0000-0000")
				.birthDate(LocalDateTime.now())
				.build();

		MockMultipartFile imageFile = createImageFile("imageFile", "testImage.png",
				"multipart/mixed", fileDir);

		given(memberRepository.findById(id)).willReturn(
				Optional.of(Member.builder().id(id).build()));

		//when
		MemberDto result = memberService.modifiedMember(id, memberDto, imageFile);

		//then
		assertThat(result.getNickname()).isEqualTo(memberDto.getNickname());
		assertThat(result.getPhone()).isEqualTo(memberDto.getPhone());
		assertThat(result.getBirthDate()).isEqualTo(memberDto.getBirthDate());
		assertThat(result.getImage().getImageOriginalName()).isEqualTo(
				imageFile.getOriginalFilename());
		assertThat(result.getImage().getImagePath()).isEqualTo(
				fileDir.getDir() + result.getImage().getImageOriginalName());
	}

	@DisplayName("회원 상태 수정-성공")
	@Test
	public void changeMemberStatus() {
		//given
		long id = 1L;
		MemberStatus memberStatus = MemberStatus.LOCKED;

		given(memberRepository.findById(id)).willReturn(Optional.of(Member.builder()
				.id(id)
				.build()));

		//when
		MemberDto result = memberService.changeMemberStatus(id, memberStatus);

		//then
		assertThat(result.getId()).isEqualTo(id);
		assertThat(result.getStatus()).isEqualTo(memberStatus);
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
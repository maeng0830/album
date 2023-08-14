package com.maeng0830.album.member.controller;

import static org.springframework.http.MediaType.APPLICATION_JSON;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.user;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.multipart;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.maeng0830.album.common.filedir.FileDir;
import com.maeng0830.album.member.dto.request.MemberJoinForm;
import com.maeng0830.album.member.dto.request.MemberModifiedForm;
import com.maeng0830.album.member.dto.request.MemberPasswordModifiedForm;
import com.maeng0830.album.support.ControllerTestSupport;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.time.LocalDate;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpMethod;
import org.springframework.mock.web.MockMultipartFile;

class MemberControllerTest extends ControllerTestSupport {

	@DisplayName("폼 회원가입을 할 수 있다.")
	@Test
	void join() throws Exception {
		// given
		MemberJoinForm memberJoinForm = MemberJoinForm.builder()
				.username("testUsername")
				.nickname("testNickname")
				.password("123")
				.checkedPassword("123")
				.build();

		// when

		// then
		mockMvc.perform(
						post("/form-signup")
								.with(csrf())
								.content(objectMapper.writeValueAsString(memberJoinForm))
								.contentType(APPLICATION_JSON)
				)
				.andDo(print())
				.andExpect(status().isOk());
	}

	@DisplayName("폼 회원가입 시, username은 필수 값이다.")
	@Test
	void join_BlankUsername() throws Exception {
		// given
		MemberJoinForm memberJoinForm = MemberJoinForm.builder()
				.nickname("testNickname")
				.password("123")
				.checkedPassword("123")
				.build();

		// when

		// then
		mockMvc.perform(
						post("/form-signup")
								.with(csrf())
								.content(objectMapper.writeValueAsString(memberJoinForm))
								.contentType(APPLICATION_JSON)
				)
				.andDo(print())
				.andExpect(status().isBadRequest())
				.andExpect(jsonPath("$[0].code").value("NotBlank"))
				.andExpect(jsonPath(("$[0].message")).value("username, 값을 입력해주시기 바랍니다."));
	}

	@DisplayName("폼 회원가입 시, nickname은 필수 값이다.")
	@Test
	void join_BlankNickname() throws Exception {
		// given
		MemberJoinForm memberJoinForm = MemberJoinForm.builder()
				.username("testUsername")
				.password("123")
				.checkedPassword("123")
				.build();

		// when

		// then
		mockMvc.perform(
						post("/form-signup")
								.with(csrf())
								.content(objectMapper.writeValueAsString(memberJoinForm))
								.contentType(APPLICATION_JSON)
				)
				.andDo(print())
				.andExpect(status().isBadRequest())
				.andExpect(jsonPath("$[0].code").value("NotBlank"))
				.andExpect(jsonPath(("$[0].message")).value("nickname, 값을 입력해주시기 바랍니다."));
	}

	@DisplayName("폼 회원가입 시, password는 필수 값이다.")
	@Test
	void join_BlankPassword() throws Exception {
		// given
		MemberJoinForm memberJoinForm = MemberJoinForm.builder()
				.username("testUsername")
				.nickname("testNickname")
				.checkedPassword("123")
				.build();

		// when

		// then
		mockMvc.perform(
						post("/form-signup")
								.with(csrf())
								.content(objectMapper.writeValueAsString(memberJoinForm))
								.contentType(APPLICATION_JSON)
				)
				.andDo(print())
				.andExpect(status().isBadRequest())
				.andExpect(jsonPath("$[0].code").value("NotBlank"))
				.andExpect(jsonPath(("$[0].message")).value("password, 값을 입력해주시기 바랍니다."));
	}

	@DisplayName("폼 회원가입 시, checkedPassword는 필수 값이다.")
	@Test
	void join_BlankCheckedPassword() throws Exception {
		// given
		MemberJoinForm memberJoinForm = MemberJoinForm.builder()
				.username("testUsername")
				.nickname("testNickname")
				.password("123")
				.build();

		// when

		// then
		mockMvc.perform(
						post("/form-signup")
								.with(csrf())
								.content(objectMapper.writeValueAsString(memberJoinForm))
								.contentType(APPLICATION_JSON)
				)
				.andDo(print())
				.andExpect(status().isBadRequest())
				.andExpect(jsonPath("$[0].code").value("NotBlank"))
				.andExpect(jsonPath(("$[0].message")).value("checkedPassword, 값을 입력해주시기 바랍니다."));
	}

	@DisplayName("로그인한 경우, 회원탈퇴 할 수 있다.")
	@Test
	void withdrawMember() throws Exception {
		// given

		// when

		// then
		mockMvc.perform(
						delete("/members")
								.with(csrf())
								.with(user(memberPrincipalDetails))
				)
				.andDo(print())
				.andExpect(status().isOk());
	}

	@DisplayName("로그인한 경우, 전제 회원을 조회할 수 있다. "
			+ "페이징 기능을 지원한다. "
			+ "검색어를 사용할 경우, 검색어와 username 또는 nickname이 일치하는 회원을 조회할 수 있다.")
	@Test
	void getMembers() throws Exception {
		// given

		// when

		// then
		mockMvc.perform(
						get("/members?searchText=text&page=0&size=20")
								.with(csrf())
								.with(user(memberPrincipalDetails))
				)
				.andDo(print())
				.andExpect(status().isOk());
	}

	@DisplayName("로그인한 경우, 특정 회원을 조회할 수 있다.")
	@Test
	void getMember() throws Exception {
		// given

		// when

		// then
		mockMvc.perform(
						get("/members/1")
								.with(csrf())
								.with(user(memberPrincipalDetails))
				)
				.andDo(print())
				.andExpect(status().isOk());
	}

	@DisplayName("로그인한 경우, 회원 정보를 수정할 수 있다.")
	@Test
	void modifiedMember() throws Exception {
		// given
		MemberModifiedForm memberModifiedForm = MemberModifiedForm.builder()
				.nickname("modNickname")
				.phone("010-2222-2222")
				.birthDate(LocalDate.now())
				.build();

		String content = objectMapper.writeValueAsString(memberModifiedForm);

		MockMultipartFile json = new MockMultipartFile("memberModifiedForm",
				"jsondata", "application/json", content.getBytes(
				StandardCharsets.UTF_8));

		MockMultipartFile imageFile = createImageFile("imageFile", "testImage.PNG",
				"multipart/mixed", fileDir);

		// when

		// then
		mockMvc.perform(
						multipart(HttpMethod.PUT, "/members")
								.file(imageFile)
								.file(json)
								.contentType("multipart/form-data")
								.with(csrf())
								.with(user(memberPrincipalDetails))
				)
				.andDo(print())
				.andExpect(status().isOk());
	}

	@DisplayName("회원 정보를 수정할 때 nickname은 필수다.")
	@Test
	void modifiedMember_blankNickname() throws Exception {
		// given
		MemberModifiedForm memberModifiedForm = MemberModifiedForm.builder()
				.phone("010-1111-1111")
				.birthDate(LocalDate.now())
				.build();

		String content = objectMapper.writeValueAsString(memberModifiedForm);

		MockMultipartFile json = new MockMultipartFile("memberModifiedForm",
				"jsondata", "application/json", content.getBytes(
				StandardCharsets.UTF_8));

		MockMultipartFile imageFile = createImageFile("imageFile", "testImage.PNG",
				"multipart/mixed", fileDir);

		// when

		// then
		mockMvc.perform(
						multipart(HttpMethod.PUT, "/members")
								.file(imageFile)
								.file(json)
								.contentType("multipart/form-data")
								.with(csrf())
								.with(user(memberPrincipalDetails))
				)
				.andDo(print())
				.andExpect(status().isBadRequest())
				.andExpect(jsonPath("$[0].code").value("NotBlank"))
				.andExpect(jsonPath("$[0].message").value("nickname, 값을 입력해주시기 바랍니다."));
	}

	@DisplayName("로그인한 경우, 비밀번호를 변경할 수 있다.")
	@Test
	void modifiedMemberPassword() throws Exception {
		// given
		MemberPasswordModifiedForm memberPasswordModifiedForm = MemberPasswordModifiedForm.builder()
				.currentPassword("123")
				.modPassword("1234")
				.checkedModPassword("1234")
				.build();

		// when

		// then
		mockMvc.perform(
						put("/members/password")
								.with(csrf())
								.with(user(memberPrincipalDetails))
								.content(objectMapper.writeValueAsString(memberPasswordModifiedForm))
								.contentType(APPLICATION_JSON)
				)
				.andDo(print())
				.andExpect(status().isOk());
	}

	@DisplayName("비밀번호를 변경할 때, 현재 비밀번호는 필수다.")
	@Test
	void modifiedMemberPassword_blankCurrentPassword() throws Exception {
		// given
		MemberPasswordModifiedForm memberPasswordModifiedForm = MemberPasswordModifiedForm.builder()
				.modPassword("1234")
				.checkedModPassword("1234")
				.build();

		// when

		// then
		mockMvc.perform(
						put("/members/password")
								.with(csrf())
								.with(user(memberPrincipalDetails))
								.content(objectMapper.writeValueAsString(memberPasswordModifiedForm))
								.contentType(APPLICATION_JSON)
				)
				.andDo(print())
				.andExpect(status().isBadRequest())
				.andExpect(jsonPath("$[0].code").value("NotBlank"))
				.andExpect(jsonPath("$[0].message").value("currentPassword, 값을 입력해주시기 바랍니다."));
	}

	@DisplayName("비밀번호를 변경할 때, 변경 비밀번호는 필수다.")
	@Test
	void modifiedMemberPassword_blankModPassword() throws Exception {
		// given
		MemberPasswordModifiedForm memberPasswordModifiedForm = MemberPasswordModifiedForm.builder()
				.currentPassword("123")
				.checkedModPassword("1234")
				.build();

		// when

		// then
		mockMvc.perform(
						put("/members/password")
								.with(csrf())
								.with(user(memberPrincipalDetails))
								.content(objectMapper.writeValueAsString(memberPasswordModifiedForm))
								.contentType(APPLICATION_JSON)
				)
				.andDo(print())
				.andExpect(status().isBadRequest())
				.andExpect(jsonPath("$[0].code").value("NotBlank"))
				.andExpect(jsonPath("$[0].message").value("modPassword, 값을 입력해주시기 바랍니다."));
	}

	@DisplayName("비밀번호를 변경할 때, 변경 확인 비밀번호는 필수다.")
	@Test
	void modifiedMemberPassword_blankCheckedModPassword() throws Exception {
		// given
		MemberPasswordModifiedForm memberPasswordModifiedForm = MemberPasswordModifiedForm.builder()
				.currentPassword("123")
				.modPassword("1234")
				.build();

		// when

		// then
		mockMvc.perform(
						put("/members/password")
								.with(csrf())
								.with(user(memberPrincipalDetails))
								.content(objectMapper.writeValueAsString(memberPasswordModifiedForm))
								.contentType(APPLICATION_JSON)
				)
				.andDo(print())
				.andExpect(status().isBadRequest())
				.andExpect(jsonPath("$[0].code").value("NotBlank"))
				.andExpect(jsonPath("$[0].message").value("checkedModPassword, 값을 입력해주시기 바랍니다."));
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
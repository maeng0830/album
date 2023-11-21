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
import com.maeng0830.album.member.dto.request.MemberWithdrawForm;
import com.maeng0830.album.member.dto.request.Oauth2PasswordForm;
import com.maeng0830.album.support.ControllerTestSupport;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.time.LocalDate;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
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
				.phone("01011111111")
				.password("!@asd123")
				.checkedPassword("!@asd123")
				.build();

		// when

		// then
		mockMvc.perform(
						post("/api/members")
								.with(csrf())
								.content(objectMapper.writeValueAsString(memberJoinForm))
								.contentType(APPLICATION_JSON)
				)
				.andDo(print())
				.andExpect(status().isOk());
	}

	@DisplayName("폼 회원가입 시, username은 영문과 숫자를 사용할 수 있습니다. 길이는 5 ~ 16이어야 합니다.")
	@CsvSource(value = {"empty", "asde", "1234", "as12"}, emptyValue = "empty")
	@ParameterizedTest
	void join_incorrectPatternUsername(String username) throws Exception {
		// given
		MemberJoinForm memberJoinForm = MemberJoinForm.builder()
				.username(username)
				.nickname("testNickname")
				.phone("01011111111")
				.password("!@asd123")
				.checkedPassword("!@asd123")
				.build();

		// when

		// then
		mockMvc.perform(
						post("/api/members")
								.with(csrf())
								.content(objectMapper.writeValueAsString(memberJoinForm))
								.contentType(APPLICATION_JSON)
				)
				.andDo(print())
				.andExpect(status().isBadRequest())
				.andExpect(jsonPath("$[0].code").value("Pattern"))
				.andExpect(jsonPath(("$[0].message")).value("아이디는 영문과 숫자를 사용할 수 있습니다. 길이는 5 ~ 16이어야 합니다."));
	}

	@DisplayName("폼 회원가입 시, nickname은 영문과 숫자를 사용할 수 있습니다. 길이는 5 ~ 16이어야 합니다.")
	@CsvSource(value = {"empty", "asde", "1234", "as12"}, emptyValue = "empty")
	@ParameterizedTest
	void join_incorrectPatternNickname(String nickname) throws Exception {
		// given
		MemberJoinForm memberJoinForm = MemberJoinForm.builder()
				.username("testUsername")
				.nickname(nickname)
				.phone("01011111111")
				.password("!@asd123")
				.checkedPassword("!@asd123")
				.build();

		// when

		// then
		mockMvc.perform(
						post("/api/members")
								.with(csrf())
								.content(objectMapper.writeValueAsString(memberJoinForm))
								.contentType(APPLICATION_JSON)
				)
				.andDo(print())
				.andExpect(status().isBadRequest())
				.andExpect(jsonPath("$[0].code").value("Pattern"))
				.andExpect(jsonPath(("$[0].message")).value("닉네임은 영문과 숫자를 사용할 수 있습니다. 길이는 5 ~ 16이어야 합니다."));
	}

	@DisplayName("폼 회원가입 시, phone은 숫자만 사용할 수 있습니다. 길이는 9 ~ 12이어야 합니다.")
	@CsvSource(value = {"empty", "12345678", "1234567891234", "010-111-1111"}, emptyValue = "empty")
	@ParameterizedTest
	void join_incorrectPatternPhone(String phone) throws Exception {
		// given
		MemberJoinForm memberJoinForm = MemberJoinForm.builder()
				.username("testUsername")
				.nickname("testNickname")
				.phone(phone)
				.password("!@asd123")
				.checkedPassword("!@asd123")
				.build();

		// when

		// then
		mockMvc.perform(
						post("/api/members")
								.with(csrf())
								.content(objectMapper.writeValueAsString(memberJoinForm))
								.contentType(APPLICATION_JSON)
				)
				.andDo(print())
				.andExpect(status().isBadRequest())
				.andExpect(jsonPath("$[0].code").value("Pattern"))
				.andExpect(jsonPath(("$[0].message")).value("연락처는 숫자만 사용할 수 있습니다. 길이는 9 ~ 12이어야 합니다."));
	}

	@DisplayName("폼 회원가입 시, password는 영문, 숫자, 특수문자를 1개이상 포함해야합니다. 길이는 8 ~ 16이어야 합니다.")
	@CsvSource(value = {"empty", "abcdefg", "1234567", "!@#$%^&", "abc123!"}, emptyValue = "empty")
	@ParameterizedTest
	void join_incorrectPatternPassword(String password) throws Exception {
		// given
		MemberJoinForm memberJoinForm = MemberJoinForm.builder()
				.username("testUsername")
				.nickname("testNickname")
				.phone("01011111111")
				.password(password)
				.checkedPassword("!@asd123")
				.build();

		// when

		// then
		mockMvc.perform(
						post("/api/members")
								.with(csrf())
								.content(objectMapper.writeValueAsString(memberJoinForm))
								.contentType(APPLICATION_JSON)
				)
				.andDo(print())
				.andExpect(status().isBadRequest())
				.andExpect(jsonPath("$[0].code").value("Pattern"))
				.andExpect(jsonPath(("$[0].message")).value("비밀번호는 영문, 숫자, 특수문자를 1개이상 포함해야합니다. 길이는 8 ~ 16이어야 합니다."));
	}

	@DisplayName("폼 회원가입 시, checkedPassword는 영문, 숫자, 특수문자를 1개이상 포함해야합니다. 길이는 8 ~ 16이어야 합니다.")
	@CsvSource(value = {"empty", "abcdefg", "1234567", "!@#$%^&", "abc123!"}, emptyValue = "empty")
	@ParameterizedTest
	void join_incorrectPatternCheckedPassword(String checkedPassword) throws Exception {
		// given
		MemberJoinForm memberJoinForm = MemberJoinForm.builder()
				.username("testUsername")
				.nickname("testNickname")
				.phone("01011111111")
				.password("!@asd123")
				.checkedPassword(checkedPassword)
				.build();

		// when

		// then
		mockMvc.perform(
						post("/api/members")
								.with(csrf())
								.content(objectMapper.writeValueAsString(memberJoinForm))
								.contentType(APPLICATION_JSON)
				)
				.andDo(print())
				.andExpect(status().isBadRequest())
				.andExpect(jsonPath("$[0].code").value("Pattern"))
				.andExpect(jsonPath(("$[0].message")).value("확인 비밀번호는 영문, 숫자, 특수문자를 1개이상 포함해야합니다. 길이는 8 ~ 16이어야 합니다."));
	}

	@DisplayName("로그인한 경우, 회원탈퇴 할 수 있다.")
	@Test
	void withdrawMember() throws Exception {
		// given
		MemberWithdrawForm memberWithdrawForm = MemberWithdrawForm.builder()
				.password("!@asd123")
				.checkedPassword("!@asd123")
				.build();

		// when

		// then
		mockMvc.perform(
						delete("/api/members")
								.with(csrf())
								.with(user(memberPrincipalDetails))
								.content(objectMapper.writeValueAsString(memberWithdrawForm))
								.contentType(APPLICATION_JSON)
				)
				.andDo(print())
				.andExpect(status().isOk());
	}

	@DisplayName("회원 탈퇴 시, password는 영문, 숫자, 특수문자를 1개이상 포함해야합니다. 길이는 8 ~ 16이어야 합니다.")
	@CsvSource(value = {"empty", "abcdefg", "1234567", "!@#$%^&", "abc123!"}, emptyValue = "empty")
	@ParameterizedTest
	void withdrawMember_incorrectPatternPassword(String password) throws Exception {
		// given
		MemberWithdrawForm memberWithdrawForm = MemberWithdrawForm.builder()
				.password(password)
				.checkedPassword("!@asd123")
				.build();

		// when

		// then
		mockMvc.perform(
						delete("/api/members")
								.with(csrf())
								.with(user(memberPrincipalDetails))
								.content(objectMapper.writeValueAsString(memberWithdrawForm))
								.contentType(APPLICATION_JSON)
				)
				.andDo(print())
				.andExpect(status().isBadRequest())
				.andExpect(jsonPath("$[0].code").value("Pattern"))
				.andExpect(jsonPath(("$[0].message")).value("비밀번호는 영문, 숫자, 특수문자를 1개이상 포함해야합니다. 길이는 8 ~ 16이어야 합니다."));
	}

	@DisplayName("회원 탈퇴 시, checkedPassword는 확인 비밀번호는 영문, 숫자, 특수문자를 1개이상 포함해야합니다. 길이는 8 ~ 16이어야 합니다.")
	@CsvSource(value = {"empty", "abcdefg", "1234567", "!@#$%^&", "abc123!"}, emptyValue = "empty")
	@ParameterizedTest
	void withdrawMember_incorrectPatternCheckedPassword(String checkedPassword) throws Exception {
		// given
		MemberWithdrawForm memberWithdrawForm = MemberWithdrawForm.builder()
				.password("!@asd123")
				.checkedPassword(checkedPassword)
				.build();

		// when

		// then
		mockMvc.perform(
						delete("/api/members")
								.with(csrf())
								.with(user(memberPrincipalDetails))
								.content(objectMapper.writeValueAsString(memberWithdrawForm))
								.contentType(APPLICATION_JSON)
				)
				.andDo(print())
				.andExpect(status().isBadRequest())
				.andExpect(jsonPath("$[0].code").value("Pattern"))
				.andExpect(jsonPath(("$[0].message")).value("확인 비밀번호는 영문, 숫자, 특수문자를 1개이상 포함해야합니다. 길이는 8 ~ 16이어야 합니다."));
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
						get("/api/members?searchText=text&page=0&size=20")
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
						get("/api/members/1")
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
				.phone("01022222222")
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
						multipart(HttpMethod.PUT, "/api/members")
								.file(imageFile)
								.file(json)
								.contentType("multipart/form-data")
								.with(csrf())
								.with(user(memberPrincipalDetails))
				)
				.andDo(print())
				.andExpect(status().isOk());
	}

	@DisplayName("회원 정보를 수정할 때 nickname은 영문과 숫자를 사용할 수 있습니다. 길이는 5 ~ 16이어야 합니다.")
	@CsvSource(value = {"empty", "asde", "1234", "as12"}, emptyValue = "empty")
	@ParameterizedTest
	void modifiedMember_incorrectPatternNickname(String nickname) throws Exception {
		// given
		MemberModifiedForm memberModifiedForm = MemberModifiedForm.builder()
				.nickname(nickname)
				.phone("01011111111")
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
						multipart(HttpMethod.PUT, "/api/members")
								.file(imageFile)
								.file(json)
								.contentType("multipart/form-data")
								.with(csrf())
								.with(user(memberPrincipalDetails))
				)
				.andDo(print())
				.andExpect(status().isBadRequest())
				.andExpect(jsonPath("$[0].code").value("Pattern"))
				.andExpect(jsonPath("$[0].message").value("닉네임은 영문과 숫자를 사용할 수 있습니다. 길이는 5 ~ 16이어야 합니다."));
	}

	@DisplayName("회원 정보를 수정할 때 연락처는 숫자만 사용할 수 있습니다. 길이는 9 ~ 12이어야 합니다.")
	@CsvSource(value = {"empty", "12345678", "1234567891234", "010-111-1111"}, emptyValue = "empty")
	@ParameterizedTest
	void modifiedMember_incorrectPatternPhone(String phone) throws Exception {
		// given
		MemberModifiedForm memberModifiedForm = MemberModifiedForm.builder()
				.nickname("modNickname")
				.phone(phone)
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
						multipart(HttpMethod.PUT, "/api/members")
								.file(imageFile)
								.file(json)
								.contentType("multipart/form-data")
								.with(csrf())
								.with(user(memberPrincipalDetails))
				)
				.andDo(print())
				.andExpect(status().isBadRequest())
				.andExpect(jsonPath("$[0].code").value("Pattern"))
				.andExpect(jsonPath("$[0].message").value("연락처는 숫자만 사용할 수 있습니다. 길이는 9 ~ 12이어야 합니다."));
	}

	@DisplayName("로그인한 경우, 비밀번호를 변경할 수 있다.")
	@Test
	void modifiedMemberPassword() throws Exception {
		// given
		MemberPasswordModifiedForm memberPasswordModifiedForm = MemberPasswordModifiedForm.builder()
				.currentPassword("!@asd123")
				.modPassword("!@asd1234")
				.checkedModPassword("!@asd1234")
				.build();

		// when

		// then
		mockMvc.perform(
						put("/api/members/password")
								.with(csrf())
								.with(user(memberPrincipalDetails))
								.content(objectMapper.writeValueAsString(memberPasswordModifiedForm))
								.contentType(APPLICATION_JSON)
				)
				.andDo(print())
				.andExpect(status().isOk());
	}

	@DisplayName("비밀번호를 변경할 때, 현재 비밀번호는 영문, 숫자, 특수문자를 1개이상 포함해야합니다. 길이는 8 ~ 16이어야 합니다.")
	@CsvSource(value = {"empty", "abcdefg", "1234567", "!@#$%^&", "abc123!"}, emptyValue = "empty")
	@ParameterizedTest
	void modifiedMemberPassword_incorrectPatternCurrentPassword(String currentPassword) throws Exception {
		// given
		MemberPasswordModifiedForm memberPasswordModifiedForm = MemberPasswordModifiedForm.builder()
				.currentPassword(currentPassword)
				.modPassword("!@asd123")
				.checkedModPassword("!@asd123")
				.build();

		// when

		// then
		mockMvc.perform(
						put("/api/members/password")
								.with(csrf())
								.with(user(memberPrincipalDetails))
								.content(objectMapper.writeValueAsString(memberPasswordModifiedForm))
								.contentType(APPLICATION_JSON)
				)
				.andDo(print())
				.andExpect(status().isBadRequest())
				.andExpect(jsonPath("$[0].code").value("Pattern"))
				.andExpect(jsonPath("$[0].message").value("현재 비밀번호는 영문, 숫자, 특수문자를 1개이상 포함해야합니다. 길이는 8 ~ 16이어야 합니다."));
	}

	@DisplayName("비밀번호를 변경할 때, 변경 비밀번호는 영문, 숫자, 특수문자를 1개이상 포함해야합니다. 길이는 8 ~ 16이어야 합니다.")
	@CsvSource(value = {"empty", "abcdefg", "1234567", "!@#$%^&", "abc123!"}, emptyValue = "empty")
	@ParameterizedTest
	void modifiedMemberPassword_incorrectPatternModPassword(String modPassword) throws Exception {
		// given
		MemberPasswordModifiedForm memberPasswordModifiedForm = MemberPasswordModifiedForm.builder()
				.currentPassword("!@asd123")
				.modPassword(modPassword)
				.checkedModPassword("!@asd1234")
				.build();

		// when

		// then
		mockMvc.perform(
						put("/api/members/password")
								.with(csrf())
								.with(user(memberPrincipalDetails))
								.content(objectMapper.writeValueAsString(memberPasswordModifiedForm))
								.contentType(APPLICATION_JSON)
				)
				.andDo(print())
				.andExpect(status().isBadRequest())
				.andExpect(jsonPath("$[0].code").value("Pattern"))
				.andExpect(jsonPath("$[0].message").value("변경 비밀번호는 영문, 숫자, 특수문자를 1개이상 포함해야합니다. 길이는 8 ~ 16이어야 합니다."));
	}

	@DisplayName("비밀번호를 변경할 때, 확인 변경 비밀번호는 영문, 숫자, 특수문자를 1개이상 포함해야합니다. 길이는 8 ~ 16이어야 합니다.")
	@CsvSource(value = {"empty", "abcdefg", "1234567", "!@#$%^&", "abc123!"}, emptyValue = "empty")
	@ParameterizedTest
	void modifiedMemberPassword_incorrectPatternCheckedModPassword(String checkedModPassword) throws Exception {
		// given
		MemberPasswordModifiedForm memberPasswordModifiedForm = MemberPasswordModifiedForm.builder()
				.currentPassword("!@asd123")
				.modPassword("!@asd1234")
				.checkedModPassword(checkedModPassword)
				.build();

		// when

		// then
		mockMvc.perform(
						put("/api/members/password")
								.with(csrf())
								.with(user(memberPrincipalDetails))
								.content(objectMapper.writeValueAsString(memberPasswordModifiedForm))
								.contentType(APPLICATION_JSON)
				)
				.andDo(print())
				.andExpect(status().isBadRequest())
				.andExpect(jsonPath("$[0].code").value("Pattern"))
				.andExpect(jsonPath("$[0].message").value("확인 변경 비밀번호는 영문, 숫자, 특수문자를 1개이상 포함해야합니다. 길이는 8 ~ 16이어야 합니다."));
	}

	@DisplayName("소셜 로그인한 경우, 필수 비밀번호 변경을 할 수 있다.")
	@Test
	void setOauth2Password() throws Exception {
		// given
		Oauth2PasswordForm oauth2PasswordForm = Oauth2PasswordForm.builder()
				.password("!@asd123")
				.checkedPassword("!@asd123")
				.build();

		// when

		// then
		mockMvc.perform(
						put("/api/members/oauth2-password")
								.with(csrf())
								.with(user(oauth2MemberPrincipalDetails))
								.content(objectMapper.writeValueAsString(oauth2PasswordForm))
								.contentType(APPLICATION_JSON)
				)
				.andDo(print())
				.andExpect(status().isOk());
	}

	@DisplayName("필수 비밀번호 변경 시, password는 영문, 숫자, 특수문자를 1개이상 포함해야합니다. 길이는 8 ~ 16이어야 합니다.")
	@CsvSource(value = {"empty", "abcdefg", "1234567", "!@#$%^&", "abc123!"}, emptyValue = "empty")
	@ParameterizedTest
	void setOauth2Password_incorrectPatternPassword(String password) throws Exception {
		// given
		Oauth2PasswordForm oauth2PasswordForm = Oauth2PasswordForm.builder()
				.password(password)
				.checkedPassword("!@asd123")
				.build();

		// when

		// then
		mockMvc.perform(
						put("/api/members/oauth2-password")
								.with(csrf())
								.with(user(memberPrincipalDetails))
								.content(objectMapper.writeValueAsString(oauth2PasswordForm))
								.contentType(APPLICATION_JSON)
				)
				.andDo(print())
				.andExpect(status().isBadRequest())
				.andExpect(jsonPath("$[0].code").value("Pattern"))
				.andExpect(jsonPath(("$[0].message")).value("비밀번호는 영문, 숫자, 특수문자를 1개이상 포함해야합니다. 길이는 8 ~ 16이어야 합니다."));
	}

	@DisplayName("필수 비밀번호 변경 시, checkedPassword는 확인 비밀번호는 영문, 숫자, 특수문자를 1개이상 포함해야합니다. 길이는 8 ~ 16이어야 합니다.")
	@CsvSource(value = {"empty", "abcdefg", "1234567", "!@#$%^&", "abc123!"}, emptyValue = "empty")
	@ParameterizedTest
	void setOauth2Password_incorrectPatternCheckedPassword(String checkedPassword) throws Exception {
		// given
		Oauth2PasswordForm oauth2PasswordForm = Oauth2PasswordForm.builder()
				.password("!@asd123")
				.checkedPassword(checkedPassword)
				.build();

		// when

		// then
		mockMvc.perform(
						put("/api/members/oauth2-password")
								.with(csrf())
								.with(user(memberPrincipalDetails))
								.content(objectMapper.writeValueAsString(oauth2PasswordForm))
								.contentType(APPLICATION_JSON)
				)
				.andDo(print())
				.andExpect(status().isBadRequest())
				.andExpect(jsonPath("$[0].code").value("Pattern"))
				.andExpect(jsonPath(("$[0].message")).value("확인 비밀번호는 영문, 숫자, 특수문자를 1개이상 포함해야합니다. 길이는 8 ~ 16이어야 합니다."));
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
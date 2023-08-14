package com.maeng0830.album.docs;

import static com.maeng0830.album.member.domain.MemberRole.ROLE_MEMBER;
import static com.maeng0830.album.member.domain.MemberStatus.FIRST;
import static com.maeng0830.album.member.domain.MemberStatus.NORMAL;
import static com.maeng0830.album.member.domain.MemberStatus.WITHDRAW;
import static com.maeng0830.album.security.dto.LoginType.FORM;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.springframework.http.MediaType.APPLICATION_JSON;
import static org.springframework.restdocs.mockmvc.MockMvcRestDocumentation.document;
import static org.springframework.restdocs.operation.preprocess.Preprocessors.preprocessRequest;
import static org.springframework.restdocs.operation.preprocess.Preprocessors.preprocessResponse;
import static org.springframework.restdocs.operation.preprocess.Preprocessors.prettyPrint;
import static org.springframework.restdocs.payload.JsonFieldType.BOOLEAN;
import static org.springframework.restdocs.payload.JsonFieldType.NULL;
import static org.springframework.restdocs.payload.JsonFieldType.NUMBER;
import static org.springframework.restdocs.payload.JsonFieldType.OBJECT;
import static org.springframework.restdocs.payload.JsonFieldType.STRING;
import static org.springframework.restdocs.payload.PayloadDocumentation.fieldWithPath;
import static org.springframework.restdocs.payload.PayloadDocumentation.requestFields;
import static org.springframework.restdocs.payload.PayloadDocumentation.requestPartFields;
import static org.springframework.restdocs.payload.PayloadDocumentation.responseFields;
import static org.springframework.restdocs.request.RequestDocumentation.parameterWithName;
import static org.springframework.restdocs.request.RequestDocumentation.partWithName;
import static org.springframework.restdocs.request.RequestDocumentation.pathParameters;
import static org.springframework.restdocs.request.RequestDocumentation.requestParameters;
import static org.springframework.restdocs.request.RequestDocumentation.requestParts;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.user;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.multipart;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.maeng0830.album.common.filedir.FileDir;
import com.maeng0830.album.common.model.image.Image;
import com.maeng0830.album.member.domain.MemberRole;
import com.maeng0830.album.member.dto.MemberDto;
import com.maeng0830.album.member.dto.request.MemberJoinForm;
import com.maeng0830.album.member.dto.request.MemberModifiedForm;
import com.maeng0830.album.member.dto.request.MemberPasswordModifiedForm;
import com.maeng0830.album.security.dto.LoginType;
import com.maeng0830.album.support.DocsTestSupport;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.domain.Sort.Order;
import org.springframework.http.HttpMethod;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders;

public class MemberControllerDocsTest extends DocsTestSupport {

	@DisplayName("폼 회원가입 API")
	@Test
	void join() throws Exception {
		// given
		MemberJoinForm memberJoinForm = MemberJoinForm.builder()
				.username("testUsername")
				.nickname("testNickname")
				.password("123")
				.checkedPassword("123")
				.build();

		LocalDateTime now = LocalDateTime.now();

		given(memberService.join(any(MemberJoinForm.class)))
				.willReturn(
						MemberDto.builder()
								.id(1L)
								.username(memberJoinForm.getUsername())
								.nickname(memberJoinForm.getNickname())
								.password(passwordEncoder.encode(memberJoinForm.getPassword()))
								.phone(null)
								.status(FIRST)
								.role(ROLE_MEMBER)
								.image(Image.createDefaultImage(fileDir,
										defaultImage.getMemberImage()))
								.loginType(FORM)
								.createdAt(now)
								.modifiedAt(now)
								.modifiedBy(memberJoinForm.getUsername())
								.build()
				);

		mockMvc.perform(
						post("/form-signup")
								.content(objectMapper.writeValueAsString(memberJoinForm))
								.contentType(APPLICATION_JSON)
				)
				.andDo(print())
				.andExpect(status().isOk())
				.andDo(document("form-join",
						preprocessRequest(prettyPrint()),
						preprocessResponse(prettyPrint()),
						requestFields(
								fieldWithPath("username").type(STRING)
										.description("아이디"),
								fieldWithPath("nickname").type(STRING)
										.description("닉네임"),
								fieldWithPath("password").type(STRING)
										.description("비밀번호"),
								fieldWithPath("checkedPassword").type(STRING)
										.description("확인 비밀번호")
						),
						responseFields(
								fieldWithPath("id").type(NUMBER)
										.description("회원 번호"),
								fieldWithPath("username").type(STRING)
										.description("아이디"),
								fieldWithPath("nickname").type(STRING)
										.description("닉네임"),
								fieldWithPath("password").type(STRING)
										.description("암호화 비밀번호"),
								fieldWithPath("phone").type(NULL)
										.description("연락처"),
								fieldWithPath("birthDate").type(NULL)
										.description("생년월일"),
								fieldWithPath("status").type(STRING)
										.description("상태"),
								fieldWithPath("role").type(STRING)
										.description("권한"),
								fieldWithPath("image").type(OBJECT)
										.description("이미지"),
								fieldWithPath("image.imageOriginalName").type(STRING)
										.description("원본 이름"),
								fieldWithPath("image.imageStoreName").type(STRING)
										.description("저장 이름"),
								fieldWithPath("image.imagePath").type(STRING)
										.description("경로"),
								fieldWithPath("loginType").type(STRING)
										.description("로그인 타입"),
								fieldWithPath("createdAt").type(STRING)
										.description("가입 일자"),
								fieldWithPath("modifiedAt").type(STRING)
										.description("수정 일자"),
								fieldWithPath("modifiedBy").type(STRING)
										.description("수정자")
						)
				));
	}

	@DisplayName("회원 탈퇴 API")
	@Test
	void withdrawMember() throws Exception {
		// given
		MemberDto memberDto = memberPrincipalDetails.getMemberDto();

		given(memberService.withdraw(any()))
				.willReturn(
						MemberDto.builder()
								.id(memberDto.getId())
								.username(memberDto.getUsername())
								.nickname(memberDto.getNickname())
								.password(passwordEncoder.encode(memberDto.getPassword()))
								.phone(memberDto.getPhone())
								.status(WITHDRAW)
								.role(ROLE_MEMBER)
								.image(Image.createDefaultImage(fileDir,
										defaultImage.getMemberImage()))
								.loginType(FORM)
								.birthDate(memberDto.getBirthDate())
								.createdAt(memberDto.getCreatedAt())
								.modifiedAt(LocalDateTime.now())
								.modifiedBy(memberDto.getModifiedBy())
								.build()
				);
		// when

		// then
		mockMvc.perform(
						delete("/members")
								.with(user(memberPrincipalDetails))
				)
				.andDo(print())
				.andExpect(status().isOk())
				.andDo(document("withdraw-member",
						preprocessResponse(prettyPrint()),
						responseFields(
								fieldWithPath("id").type(NUMBER)
										.description("회원 번호"),
								fieldWithPath("username").type(STRING)
										.description("아이디"),
								fieldWithPath("nickname").type(STRING)
										.description("닉네임"),
								fieldWithPath("password").type(STRING)
										.description("암호화 비밀번호"),
								fieldWithPath("phone").type(STRING)
										.description("연락처"),
								fieldWithPath("birthDate").type(STRING)
										.description("생년월일"),
								fieldWithPath("status").type(STRING)
										.description("상태"),
								fieldWithPath("role").type(STRING)
										.description("권한"),
								fieldWithPath("image").type(OBJECT)
										.description("이미지"),
								fieldWithPath("image.imageOriginalName").type(STRING)
										.description("원본 이름"),
								fieldWithPath("image.imageStoreName").type(STRING)
										.description("저장 이름"),
								fieldWithPath("image.imagePath").type(STRING)
										.description("경로"),
								fieldWithPath("loginType").type(STRING)
										.description("로그인 타입"),
								fieldWithPath("createdAt").type(STRING)
										.description("가입 일자"),
								fieldWithPath("modifiedAt").type(STRING)
										.description("수정 일자"),
								fieldWithPath("modifiedBy").type(STRING)
										.description("수정자")
						)
				));
	}

	@DisplayName("회원 목록 조회 API")
	@Test
	void getMembers() throws Exception {
		// given
		// response data
		List<MemberDto> memberDtos = new ArrayList<>();
		for (int i = 0; i < 5; i++) {
			MemberDto memberDto = MemberDto.builder()
					.id((long) i)
					.username("username" + i)
					.nickname("nickname" + i)
					.status(NORMAL)
					.role(MemberRole.ROLE_MEMBER)
					.loginType(LoginType.FORM)
					.password("123")
					.phone("010-1111-1111")
					.image(Image.createDefaultImage(fileDir, defaultImage.getMemberImage()))
					.birthDate(LocalDate.now())
					.createdAt(LocalDateTime.now())
					.modifiedAt(LocalDateTime.now())
					.modifiedBy("testMember@naver.com")
					.build();
			memberDtos.add(memberDto);
		}

		PageRequest pageRequest = PageRequest.of(0, 20,
				Sort.by(Order.desc("createdAt"), Order.asc("status")));

		Page<MemberDto> memberDtoPage = new PageImpl<>(memberDtos, pageRequest, 5);

		given(memberService.getMembers(any(String.class), any(Pageable.class)))
				.willReturn(
						memberDtoPage
				);

		// when

		// then
		mockMvc.perform(
						get("/members")
								.queryParam("searchText", "nickname")
								.queryParam("page", "0")
								.queryParam("size", "20")
				)
				.andDo(print())
				.andExpect(status().isOk())
				.andDo(document("get-members",
						preprocessRequest(prettyPrint()),
						preprocessResponse(prettyPrint()),
						requestParameters(
								parameterWithName("searchText").description("검색어(닉네임, 전방 일치)"),
								parameterWithName("page").description("페이지 번호"),
								parameterWithName("size").description("페이지 당 데이터 개수")
						),
						responseFields(
								fieldWithPath("content.[].id").type(NUMBER)
										.description("회원 번호"),
								fieldWithPath("content.[].username").type(STRING)
										.description("아이디"),
								fieldWithPath("content.[].nickname").type(STRING)
										.description("닉네임"),
								fieldWithPath("content.[].password").type(STRING)
										.description("암호화 비밀번호"),
								fieldWithPath("content.[].phone").type(STRING)
										.description("연락처"),
								fieldWithPath("content.[].birthDate").type(STRING)
										.description("생년월일"),
								fieldWithPath("content.[].status").type(STRING)
										.description("상태"),
								fieldWithPath("content.[].role").type(STRING)
										.description("권한"),
								fieldWithPath("content.[].image").type(OBJECT)
										.description("이미지"),
								fieldWithPath("content.[].image.imageOriginalName").type(STRING)
										.description("원본 이름"),
								fieldWithPath("content.[].image.imageStoreName").type(STRING)
										.description("저장 이름"),
								fieldWithPath("content.[].image.imagePath").type(STRING)
										.description("경로"),
								fieldWithPath("content.[].loginType").type(STRING)
										.description("로그인 타입"),
								fieldWithPath("content.[].createdAt").type(STRING)
										.description("가입 일자"),
								fieldWithPath("content.[].modifiedAt").type(STRING)
										.description("수정 일자"),
								fieldWithPath("content.[].modifiedBy").type(STRING)
										.description("수정자"),

								fieldWithPath("pageable.sort.sorted").type(BOOLEAN)
										.description("정렬"),
								fieldWithPath("pageable.sort.empty").type(BOOLEAN)
										.description("데이터 비어있는지 여부"),
								fieldWithPath("pageable.sort.unsorted").type(BOOLEAN)
										.description("비정렬"),

								fieldWithPath("pageable.offset").type(NUMBER)
										.description("첫 데이터 인덱스"),
								fieldWithPath("pageable.pageNumber").type(NUMBER)
										.description("현재 페이지 번호"),
								fieldWithPath("pageable.pageSize").type(NUMBER)
										.description("페이지 당 데이터 개수"),
								fieldWithPath("pageable.paged").type(BOOLEAN)
										.description("페이징 정보 포함"),
								fieldWithPath("pageable.unpaged").type(BOOLEAN)
										.description("페이징 정보 비포함"),

								fieldWithPath("last").type(BOOLEAN).description("마지막 페이지 여부"),
								fieldWithPath("totalPages").type(NUMBER).description("전체 페이지 개수"),
								fieldWithPath("totalElements").type(NUMBER)
										.description("전체 데이터 개수"),
								fieldWithPath("first").type(BOOLEAN).description("첫번째 페이지 여부"),
								fieldWithPath("numberOfElements").type(NUMBER).description(
										"현재 페이지에서 조회된 데이터 개수"),
								fieldWithPath("number").type(NUMBER).description("현재 페이지 번호"),
								fieldWithPath("size").type(NUMBER).description("페이지 당 데이터 개수"),

								fieldWithPath("sort.sorted").type(BOOLEAN).description("정렬"),
								fieldWithPath("sort.unsorted").type(BOOLEAN).description("비정렬"),
								fieldWithPath("sort.empty").type(BOOLEAN)
										.description("데이터 비어있는지 여부"),

								fieldWithPath("empty").type(BOOLEAN).description("데이터 비어있는지 여부")
						)
				));
	}

	@DisplayName("특정 회원 조회 API")
	@Test
	void getMember() throws Exception {
		// given
		MemberDto memberDto = memberPrincipalDetails.getMemberDto();

		given(memberService.getMember(any(Long.class)))
				.willReturn(
						memberDto
				);

		// when

		// then
		mockMvc.perform(
						RestDocumentationRequestBuilders.get("/members/{id}", 1)
				)
				.andDo(print())
				.andExpect(status().isOk())
				.andDo(document("get-member",
						preprocessResponse(prettyPrint()),
						pathParameters(
								parameterWithName("id").description("회원 번호")
						),
						responseFields(
								fieldWithPath("id").type(NUMBER)
										.description("회원 번호"),
								fieldWithPath("username").type(STRING)
										.description("아이디"),
								fieldWithPath("nickname").type(STRING)
										.description("닉네임"),
								fieldWithPath("password").type(STRING)
										.description("암호화 비밀번호"),
								fieldWithPath("phone").type(STRING)
										.description("연락처"),
								fieldWithPath("birthDate").type(STRING)
										.description("생년월일"),
								fieldWithPath("status").type(STRING)
										.description("상태"),
								fieldWithPath("role").type(STRING)
										.description("권한"),
								fieldWithPath("image").type(OBJECT)
										.description("이미지"),
								fieldWithPath("image.imageOriginalName").type(STRING)
										.description("원본 이름"),
								fieldWithPath("image.imageStoreName").type(STRING)
										.description("저장 이름"),
								fieldWithPath("image.imagePath").type(STRING)
										.description("경로"),
								fieldWithPath("loginType").type(STRING)
										.description("로그인 타입"),
								fieldWithPath("createdAt").type(STRING)
										.description("가입 일자"),
								fieldWithPath("modifiedAt").type(STRING)
										.description("수정 일자"),
								fieldWithPath("modifiedBy").type(STRING)
										.description("수정자")
						)));
	}

	@DisplayName("회원 정보 수정")
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

		// response 데이터
		MemberDto memberDto = memberPrincipalDetails.getMemberDto();
		memberDto.modifiedBasicInfo(memberModifiedForm);

		given(memberService.modifiedMember(any(), any(MemberModifiedForm.class), any(
				MockMultipartFile.class)))
				.willReturn(
						memberDto
				);

		// when

		// then
		mockMvc.perform(
						multipart(HttpMethod.PUT, "/members")
								.file(imageFile)
								.file(json)
								.contentType("multipart/form-data")
								.with(user(memberPrincipalDetails))
				)
				.andDo(print())
				.andExpect(status().isOk())
				.andDo(document("modified-basicInfo",
						preprocessRequest(prettyPrint()),
						preprocessResponse(prettyPrint()),
						requestParts(
								partWithName("imageFile").description("이미지 파일"),
								partWithName("memberModifiedForm").description("수정 내용")
						),
						requestPartFields("memberModifiedForm",
								fieldWithPath("nickname").description("수정 닉네임"),
								fieldWithPath("phone").description("수정 연락처"),
								fieldWithPath("birthDate").description("수정 생년월일")
						),
						responseFields(
								fieldWithPath("id").type(NUMBER)
										.description("회원 번호"),
								fieldWithPath("username").type(STRING)
										.description("아이디"),
								fieldWithPath("nickname").type(STRING)
										.description("닉네임"),
								fieldWithPath("password").type(STRING)
										.description("암호화 비밀번호"),
								fieldWithPath("phone").type(STRING)
										.description("연락처"),
								fieldWithPath("birthDate").type(STRING)
										.description("생년월일"),
								fieldWithPath("status").type(STRING)
										.description("상태"),
								fieldWithPath("role").type(STRING)
										.description("권한"),
								fieldWithPath("image").type(OBJECT)
										.description("이미지"),
								fieldWithPath("image.imageOriginalName").type(STRING)
										.description("원본 이름"),
								fieldWithPath("image.imageStoreName").type(STRING)
										.description("저장 이름"),
								fieldWithPath("image.imagePath").type(STRING)
										.description("경로"),
								fieldWithPath("loginType").type(STRING)
										.description("로그인 타입"),
								fieldWithPath("createdAt").type(STRING)
										.description("가입 일자"),
								fieldWithPath("modifiedAt").type(STRING)
										.description("수정 일자"),
								fieldWithPath("modifiedBy").type(STRING)
										.description("수정자")
						)
				));
	}

	@DisplayName("비밀번호 변경 API")
	@Test
	void modifiedMemberPassword() throws Exception {
		// given
		MemberPasswordModifiedForm memberPasswordModifiedForm = MemberPasswordModifiedForm.builder()
				.currentPassword("123")
				.modPassword("1234")
				.checkedModPassword("1234")
				.build();

		// response 데이터
		MemberDto memberDto = modPassword(memberPrincipalDetails.getMemberDto(),
				memberPasswordModifiedForm);

		given(memberService.modifiedMemberPassword(any(), any(MemberPasswordModifiedForm.class)))
				.willReturn(
						memberDto
				);

		// when

		// then
		mockMvc.perform(
						put("/members/password")
								.with(user(memberPrincipalDetails))
								.content(objectMapper.writeValueAsString(memberPasswordModifiedForm))
								.contentType(APPLICATION_JSON)
				)
				.andDo(print())
				.andExpect(status().isOk())
				.andDo(document("modified-password",
						preprocessRequest(prettyPrint()),
						preprocessResponse(prettyPrint()),
						requestFields(
								fieldWithPath("currentPassword").description("현재 비밀번호"),
								fieldWithPath("modPassword").description("변경 비밀번호"),
								fieldWithPath("checkedModPassword").description("변경 비밀번호 확인")
						),
						responseFields(
								fieldWithPath("id").type(NUMBER)
										.description("회원 번호"),
								fieldWithPath("username").type(STRING)
										.description("아이디"),
								fieldWithPath("nickname").type(STRING)
										.description("닉네임"),
								fieldWithPath("password").type(STRING)
										.description("암호화 비밀번호"),
								fieldWithPath("phone").type(STRING)
										.description("연락처"),
								fieldWithPath("birthDate").type(STRING)
										.description("생년월일"),
								fieldWithPath("status").type(STRING)
										.description("상태"),
								fieldWithPath("role").type(STRING)
										.description("권한"),
								fieldWithPath("image").type(OBJECT)
										.description("이미지"),
								fieldWithPath("image.imageOriginalName").type(STRING)
										.description("원본 이름"),
								fieldWithPath("image.imageStoreName").type(STRING)
										.description("저장 이름"),
								fieldWithPath("image.imagePath").type(STRING)
										.description("경로"),
								fieldWithPath("loginType").type(STRING)
										.description("로그인 타입"),
								fieldWithPath("createdAt").type(STRING)
										.description("가입 일자"),
								fieldWithPath("modifiedAt").type(STRING)
										.description("수정 일자"),
								fieldWithPath("modifiedBy").type(STRING)
										.description("수정자")
						)
				));
	}

	private MockMultipartFile createImageFile(String name, String originalFilename,
											  String contentType, FileDir fileDir)
			throws IOException {
		String filePath = fileDir.getDir() + originalFilename;
		FileInputStream fileInputStream = new FileInputStream(new File(filePath));

		return new MockMultipartFile(name,
				originalFilename, contentType, fileInputStream);
	}

	private MemberDto modPassword(MemberDto memberDto,
								  MemberPasswordModifiedForm memberPasswordModifiedForm) {
		return MemberDto.builder()
				.id(memberDto.getId())
				.username(memberDto.getUsername())
				.nickname(memberDto.getNickname())
				.password(passwordEncoder.encode(memberPasswordModifiedForm.getModPassword()))
				.phone(memberDto.getPhone())
				.status(memberDto.getStatus())
				.role(memberDto.getRole())
				.image(memberDto.getImage())
				.loginType(memberDto.getLoginType())
				.birthDate(memberDto.getBirthDate())
				.createdAt(memberDto.getCreatedAt())
				.modifiedAt(LocalDateTime.now())
				.modifiedBy(memberDto.getModifiedBy())
				.build();
	}
}

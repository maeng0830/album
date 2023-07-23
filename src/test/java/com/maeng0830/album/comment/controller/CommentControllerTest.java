package com.maeng0830.album.comment.controller;

import static org.springframework.http.MediaType.APPLICATION_JSON;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.user;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.maeng0830.album.comment.dto.request.CommentAccuseForm;
import com.maeng0830.album.comment.dto.request.CommentModifiedForm;
import com.maeng0830.album.comment.dto.request.CommentPostForm;
import com.maeng0830.album.comment.service.CommentService;
import com.maeng0830.album.common.filedir.FileDir;
import com.maeng0830.album.common.image.DefaultImage;
import com.maeng0830.album.common.util.AlbumUtil;
import com.maeng0830.album.security.formlogin.PrincipalDetails;
import com.maeng0830.album.security.formlogin.handler.FormLoginFailureHandler;
import com.maeng0830.album.security.formlogin.handler.FormLoginSuccessHandler;
import com.maeng0830.album.security.oauthlogin.handler.OAuthLoginFailureHandler;
import com.maeng0830.album.security.oauthlogin.handler.OAuthLoginSuccessHandler;
import com.maeng0830.album.support.TestConfig;
import com.maeng0830.album.support.TestPrincipalDetailsService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Import;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.test.web.servlet.setup.SecurityMockMvcConfigurers;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;

@Import(TestConfig.class)
@ActiveProfiles("test")
@WebMvcTest(controllers = CommentController.class,
		includeFilters = @ComponentScan.Filter(classes = {EnableWebSecurity.class}))
class CommentControllerTest {

	@Autowired
	private WebApplicationContext context;

	@Autowired
	private ObjectMapper objectMapper;

	private MockMvc mockMvc;

	private PrincipalDetails memberPrincipalDetails;

	private PrincipalDetails adminPrincipalDetails;

	private final TestPrincipalDetailsService testPrincipalDetailsService =
			new TestPrincipalDetailsService();

	@MockBean
	private CommentService commentService;
	@MockBean
	private AlbumUtil albumUtil;
	@MockBean
	private FormLoginSuccessHandler formLoginSuccessHandler;
	@MockBean
	private FormLoginFailureHandler formLoginFailureHandler;
	@MockBean
	private OAuthLoginSuccessHandler oAuthLoginSuccessHandler;
	@MockBean
	private OAuthLoginFailureHandler oAuthLoginFailureHandler;

	@Autowired
	private FileDir fileDir;
	@Autowired
	private DefaultImage defaultImage;

	@BeforeEach
	public void setup() {
		mockMvc = MockMvcBuilders
				.webAppContextSetup(context)
				.apply(SecurityMockMvcConfigurers.springSecurity())
				.build();

		memberPrincipalDetails =
				(PrincipalDetails) testPrincipalDetailsService.loadUserByUsername("member");

		adminPrincipalDetails =
				(PrincipalDetails) testPrincipalDetailsService.loadUserByUsername("admin");
	}

	@DisplayName("특정 피드의 댓글 목록을 조회할 수 있다."
			+ "페이징 기능을 사용할 수 있다.")
	@Test
	void getFeedComments() throws Exception {
		// given

		// when

		// then
		mockMvc.perform(
						get("/comments?feedId=1&page=0&size=20")
								.with(csrf())
				)
				.andDo(print())
				.andExpect(status().isOk());
	}

	@DisplayName("특정 댓글을 조회할 수 있다.")
	@Test
	void getComment() throws Exception {
		// given

		// when

		// then
		mockMvc.perform(
						get("/comments/1")
								.with(csrf())
				)
				.andDo(print())
				.andExpect(status().isOk());
	}

	@DisplayName("로그인한 경우, 댓글을 등록할 수 있다.")
	@Test
	void comment() throws Exception {
		// given
		CommentPostForm commentPostForm = CommentPostForm.builder()
				.feedId(1L)
				.groupId(1L)
				.parentId(1L)
				.content("testContent")
				.build();

		// when

		// then
		mockMvc.perform(
						post("/comments")
								.with(csrf())
								.with(user(memberPrincipalDetails))
								.content(objectMapper.writeValueAsString(commentPostForm))
								.contentType(APPLICATION_JSON)
				)
				.andDo(print())
				.andExpect(status().isOk());
	}

	@DisplayName("댓글 등록 시, 피드아이디는 필수다.")
	@Test
	void comment_nullFeedId() throws Exception {
		// given
		CommentPostForm commentPostForm = CommentPostForm.builder()
				.groupId(1L)
				.parentId(1L)
				.content("testContent")
				.build();

		// when

		// then
		mockMvc.perform(
						post("/comments")
								.with(csrf())
								.with(user(memberPrincipalDetails))
								.content(objectMapper.writeValueAsString(commentPostForm))
								.contentType(APPLICATION_JSON)
				)
				.andDo(print())
				.andExpect(status().isBadRequest())
				.andExpect(jsonPath("$[0].code").value("NotNull"))
				.andExpect(jsonPath("$[0].message").value("feedId, 값을 입력해주시기 바랍니다."));
	}

	@DisplayName("댓글 등록 시, 댓글내용은 필수다.")
	@Test
	void comment_blankContent() throws Exception {
		// given
		CommentPostForm commentPostForm = CommentPostForm.builder()
				.feedId(1L)
				.groupId(1L)
				.parentId(1L)
				.build();

		// when

		// then
		mockMvc.perform(
						post("/comments")
								.with(csrf())
								.with(user(memberPrincipalDetails))
								.content(objectMapper.writeValueAsString(commentPostForm))
								.contentType(APPLICATION_JSON)
				)
				.andDo(print())
				.andExpect(status().isBadRequest())
				.andExpect(jsonPath("$[0].code").value("NotBlank"))
				.andExpect(jsonPath("$[0].message").value("content, 값을 입력해주시기 바랍니다."));
	}

	@DisplayName("로그인한 경우, 본인이 작성한 댓글을 수정할 수 있다.")
	@Test
	void modifiedComment() throws Exception {
		// given
		CommentModifiedForm commentModifiedForm = CommentModifiedForm.builder()
				.id(1L)
				.content("testContent")
				.build();

		// when

		// then
		mockMvc.perform(
						put("/comments")
								.with(csrf())
								.with(user(memberPrincipalDetails))
								.content(objectMapper.writeValueAsString(commentModifiedForm))
								.contentType(APPLICATION_JSON)
				)
				.andDo(print())
				.andExpect(status().isOk());
	}

	@DisplayName("댓글 수정 시, 댓글아이디는 필수다.")
	@Test
	void modifiedComment_nullId() throws Exception {
		// given
		CommentModifiedForm commentModifiedForm = CommentModifiedForm.builder()
				.content("testContent")
				.build();

		// when

		// then
		mockMvc.perform(
						put("/comments")
								.with(csrf())
								.with(user(memberPrincipalDetails))
								.content(objectMapper.writeValueAsString(commentModifiedForm))
								.contentType(APPLICATION_JSON)
				)
				.andDo(print())
				.andExpect(status().isBadRequest())
				.andExpect(jsonPath("$[0].code").value("NotNull"))
				.andExpect(jsonPath("$[0].message").value("id, 값을 입력해주시기 바랍니다."));
	}

	@DisplayName("댓글 수정 시, 댓글내용은 필수다.")
	@Test
	void modifiedComment_blankContent() throws Exception {
		// given
		CommentModifiedForm commentModifiedForm = CommentModifiedForm.builder()
				.id(1L)
				.build();

		// when

		// then
		mockMvc.perform(
						put("/comments")
								.with(csrf())
								.with(user(memberPrincipalDetails))
								.content(objectMapper.writeValueAsString(commentModifiedForm))
								.contentType(APPLICATION_JSON)
				)
				.andDo(print())
				.andExpect(status().isBadRequest())
				.andExpect(jsonPath("$[0].code").value("NotBlank"))
				.andExpect(jsonPath("$[0].message").value("content, 값을 입력해주시기 바랍니다."));
	}

	@DisplayName("로그인한 경우, 댓글을 신고할 수 있다.")
	@Test
	void accuseComment() throws Exception {
		// given
		CommentAccuseForm commentAccuseForm = CommentAccuseForm.builder()
				.content("testContent")
				.build();

		// when

		// then
		mockMvc.perform(
						put("/comments/1/accuse")
								.with(csrf())
								.with(user(memberPrincipalDetails))
								.content(objectMapper.writeValueAsString(commentAccuseForm))
								.contentType(APPLICATION_JSON)
				)
				.andDo(print())
				.andExpect(status().isOk());
	}

	@DisplayName("댓글 신고 시, 신고내용은 필수다.")
	@Test
	void accuseComment_blankContent() throws Exception {
		// given
		CommentAccuseForm commentAccuseForm = CommentAccuseForm.builder()
				.build();

		// when

		// then
		mockMvc.perform(
						put("/comments/1/accuse")
								.with(csrf())
								.with(user(memberPrincipalDetails))
								.content(objectMapper.writeValueAsString(commentAccuseForm))
								.contentType(APPLICATION_JSON)
				)
				.andDo(print())
				.andExpect(status().isBadRequest())
				.andExpect(jsonPath("$[0].code").value("NotBlank"))
				.andExpect(jsonPath("$[0].message").value("content, 값을 입력해주시기 바랍니다."));

	}

	@DisplayName("로그인한 경우, 본인이 작성한 댓글을 삭제할 수 있다.")
	@Test
	void deleteComment() throws Exception {
		// given

		// when

		// then
		mockMvc.perform(
						delete("/comments/1")
								.with(csrf())
								.with(user(memberPrincipalDetails))
				)
				.andDo(print())
				.andExpect(status().isOk());
	}
}
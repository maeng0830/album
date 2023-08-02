package com.maeng0830.album.member.controller;

import static org.springframework.http.MediaType.APPLICATION_JSON;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.user;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.maeng0830.album.comment.service.CommentService;
import com.maeng0830.album.common.filedir.FileDir;
import com.maeng0830.album.common.image.DefaultImage;
import com.maeng0830.album.common.util.AlbumUtil;
import com.maeng0830.album.feed.service.FeedService;
import com.maeng0830.album.member.service.MemberService;
import com.maeng0830.album.security.formlogin.PrincipalDetails;
import com.maeng0830.album.security.formlogin.handler.FormLoginFailureHandler;
import com.maeng0830.album.security.formlogin.handler.FormLoginSuccessHandler;
import com.maeng0830.album.security.oauthlogin.handler.OAuthLoginFailureHandler;
import com.maeng0830.album.security.oauthlogin.handler.OAuthLoginSuccessHandler;
import com.maeng0830.album.support.TestConfig;
import com.maeng0830.album.support.TestPrincipalDetailsService;
import java.util.HashMap;
import java.util.Map;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Import;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.test.web.servlet.setup.SecurityMockMvcConfigurers;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;

@Import(TestConfig.class)
@ActiveProfiles("test")
@WebMvcTest(controllers = AdminController.class,
		includeFilters = @ComponentScan.Filter(classes = {EnableWebSecurity.class}))
class AdminControllerTest {

	@Autowired
	private WebApplicationContext context;

	@Autowired
	private ObjectMapper objectMapper;

	private MockMvc mockMvc;

	@Autowired
	private FileDir fileDir;
	@Autowired
	private DefaultImage defaultImage;
	@Autowired
	private TestPrincipalDetailsService testPrincipalDetailsService;

	private PrincipalDetails memberPrincipalDetails;

	private PrincipalDetails adminPrincipalDetails;

	private BCryptPasswordEncoder passwordEncoder = new BCryptPasswordEncoder();

	@MockBean
	private CommentService commentService;
	@MockBean
	private FeedService feedService;
	@MockBean
	private MemberService memberService;
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

	@DisplayName("관리자인 경우, 피드 목록을 조회할 수 있다."
			+ "검색어를 통해, 검색어와 피드 작성자의 유저네임 및 닉네임이 전방일치하는 피드 목록을 조회할 수 있다."
			+ "페이징 기능을 사용할 수 있다.")
	@Test
	void getFeedsForAdmin() throws Exception {
		// given

		// when

		// then
		mockMvc.perform(
						get("/admin/feeds?searchText=nickname&page=0&size=20")
								.with(csrf())
								.with(user(adminPrincipalDetails))
				)
				.andDo(print())
				.andExpect(status().isOk());
	}

	@DisplayName("관리자인 경우, 특정 피드의 신고내역 목록을 조회할 수있다.")
	@Test
	void getFeedAccuses() throws Exception {
		// given

		// when

		// then
		mockMvc.perform(
						get("/admin/feeds/1/accuses")
								.with(csrf())
								.with(user(adminPrincipalDetails))
				)
				.andDo(print())
				.andExpect(status().isOk());
	}

	@DisplayName("관리자인 경우, 피드의 상태를 변경할 수 있다.")
	@Test
	void changeFeedStatus() throws Exception {
		// given
		Map<String, String> map = new HashMap<>();
		map.put("feedStatus", "DELETE");

		// when

		// then
		mockMvc.perform(
						put("/admin/feeds/1/status")
								.with(csrf())
								.with(user(adminPrincipalDetails))
								.content(objectMapper.writeValueAsString(map))
								.contentType(APPLICATION_JSON)
				)
				.andDo(print())
				.andExpect(status().isOk());
	}

	@DisplayName("관리자인 경우, 댓글 목록을 조회할 수 있다."
			+ "검색어를 통해, 검색어와 댓글 작성자의 유저네임 및 닉네임이 전방일치하는 피드 목록을 조회할 수 있다."
			+ "페이징 기능을 사용할 수 있다.")
	@Test
	void getCommentsForAdmin() throws Exception {
		// given

		// when

		// then
		mockMvc.perform(
						get("/admin/comments?searchText=nickname&page=0&size=20")
								.with(csrf())
								.with(user(adminPrincipalDetails))
				)
				.andDo(print())
				.andExpect(status().isOk());
	}

	@DisplayName("관리자인 경우, 댓글의 신고내역 목록을 조회할 수있다.")
	@Test
	void getCommentAccuses() throws Exception {
		// given

		// when

		// then
		mockMvc.perform(
						get("/admin/comments/1/accuses")
								.with(csrf())
								.with(user(adminPrincipalDetails))
				)
				.andDo(print())
				.andExpect(status().isOk());
	}

	@DisplayName("관리자인 경우, 댓글의 상태를 변경할 수 있다.")
	@Test
	void changeCommentStatus() throws Exception {
		// given
		Map<String, String> map = new HashMap<>();
		map.put("commentStatus", "DELETE");

		// when

		// then
		mockMvc.perform(
						put("/admin/comments/1/status")
								.with(csrf())
								.with(user(adminPrincipalDetails))
								.content(objectMapper.writeValueAsString(map))
								.contentType(APPLICATION_JSON)
				)
				.andDo(print())
				.andExpect(status().isOk());
	}

	@DisplayName("관리자인 경우, 회원 목록을 조회할 수 있다."
			+ "검색어를 통해, 검색어와 회원의 유저네임 및 닉네임이 전방일치하는 피드 목록을 조회할 수 있다."
			+ "페이징 기능을 사용할 수 있다.")
	@Test
	void getMembersForAdmin() throws Exception {
		// given

		// when

		// then
		mockMvc.perform(
						get("/admin/members?searchText=nickname&page=0&size=20")
								.with(csrf())
								.with(user(adminPrincipalDetails))
				)
				.andDo(print())
				.andExpect(status().isOk());
	}

	@DisplayName("관리자인 경우, 회원의 상태를 변경할 수 있다.")
	@Test
	void changeMemberStatus() throws Exception {
		// given
		Map<String, String> map = new HashMap<>();
		map.put("memberStatus", "LOCKED");

		// when

		// then
		mockMvc.perform(
						put("/admin/members/1/status")
								.with(csrf())
								.with(user(adminPrincipalDetails))
								.content(objectMapper.writeValueAsString(map))
								.contentType(APPLICATION_JSON)
				)
				.andDo(print())
				.andExpect(status().isOk());
	}
}
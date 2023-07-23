package com.maeng0830.album.follow.controller;

import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.user;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.fasterxml.jackson.databind.ObjectMapper;
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
@WebMvcTest(controllers = FollowController.class,
		includeFilters = @ComponentScan.Filter(classes = {EnableWebSecurity.class}))
class FollowControllerTest {

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
	private FollowController followController;
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

	@DisplayName("로그인한 경우, 특정 회원을 팔로우 할 수 있다.")
	@Test
	void follow() throws Exception {
		// given

		// when

		// then
		mockMvc.perform(
						post("/follows/1")
								.with(csrf())
								.with(user(memberPrincipalDetails))
				)
				.andDo(print())
				.andExpect(status().isOk());
	}

	@DisplayName("로그인한 경우, 특정 회원에 대한 팔로우를 끊을 수 있다.")
	@Test
	void cancelFollow() throws Exception {
		// given

		// when

		// then
		mockMvc.perform(
						delete("/follows/1")
								.with(csrf())
								.with(user(memberPrincipalDetails))
				)
				.andDo(print())
				.andExpect(status().isOk());
	}

	@DisplayName("로그인한 경우, 특정 회원의 팔로잉 목록을 조회할 수 있다."
			+ "검색어를 사용하면, 검색어와 닉네임이 전방 일치하는 팔로잉 목록을 조회할 수 있다."
			+ "페이징 기능을 사용할 수 있다.")
	@Test
	void getFollowings() throws Exception {
		// given

		// when

		// then
		mockMvc.perform(
						get("/follows/following/1?searchText=nickname&page=0&size=20")
								.with(csrf())
								.with(user(memberPrincipalDetails))
				)
				.andDo(print())
				.andExpect(status().isOk());
	}

	@DisplayName("로그인한 경우, 특정 회원의 팔로워 목록을 조회할 수 있다."
			+ "검색어를 사용하면, 검색어와 닉네임이 전방 일치하는 팔로워 목록을 조회할 수 있다."
			+ "페이징 기능을 사용할 수 있다.")
	@Test
	void getFollowers() throws Exception {
		// given

		// when

		// then
		mockMvc.perform(
						get("/follows/follower/1?searchText=nickname&page=0&size=20")
								.with(csrf())
								.with(user(memberPrincipalDetails))
				)
				.andDo(print())
				.andExpect(status().isOk());
	}
}
package com.maeng0830.album.support;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.maeng0830.album.comment.controller.CommentController;
import com.maeng0830.album.comment.service.CommentService;
import com.maeng0830.album.common.filedir.FileDir;
import com.maeng0830.album.common.image.DefaultImage;
import com.maeng0830.album.common.util.AlbumUtil;
import com.maeng0830.album.feed.controller.FeedController;
import com.maeng0830.album.feed.service.FeedService;
import com.maeng0830.album.follow.controller.FollowController;
import com.maeng0830.album.follow.service.FollowService;
import com.maeng0830.album.member.controller.AdminController;
import com.maeng0830.album.member.controller.MemberController;
import com.maeng0830.album.member.service.MemberService;
import com.maeng0830.album.security.formlogin.PrincipalDetails;
import com.maeng0830.album.security.formlogin.handler.FormLoginFailureHandler;
import com.maeng0830.album.security.formlogin.handler.FormLoginSuccessHandler;
import com.maeng0830.album.security.oauthlogin.handler.OAuthLoginFailureHandler;
import com.maeng0830.album.security.oauthlogin.handler.OAuthLoginSuccessHandler;
import com.maeng0830.album.support.config.ControllerAndDocsTestConfig;
import com.maeng0830.album.support.config.TestPrincipalDetailsService;
import com.maeng0830.album.support.util.TestFileManager;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
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

@Import(ControllerAndDocsTestConfig.class)
@ActiveProfiles("test")
@WebMvcTest(controllers = {AdminController.class, MemberController.class, FollowController.class,
		FeedController.class, CommentController.class},
		includeFilters = @ComponentScan.Filter(classes = {EnableWebSecurity.class}))
public abstract class ControllerTestSupport {
	@Autowired
	protected WebApplicationContext context;

	@Autowired
	protected ObjectMapper objectMapper;

	protected MockMvc mockMvc;

	@Autowired
	protected FileDir fileDir;
	@Autowired
	protected DefaultImage defaultImage;
	@Autowired
	protected TestPrincipalDetailsService testPrincipalDetailsService;

	protected PrincipalDetails memberPrincipalDetails;

	protected PrincipalDetails adminPrincipalDetails;

	@MockBean
	protected MemberService memberService;
	@MockBean
	protected FollowService followService;
	@MockBean
	protected FeedService feedService;
	@MockBean
	protected CommentService commentService;
	@MockBean
	protected AlbumUtil albumUtil;
	@MockBean
	protected FormLoginSuccessHandler formLoginSuccessHandler;
	@MockBean
	protected FormLoginFailureHandler formLoginFailureHandler;
	@MockBean
	protected OAuthLoginSuccessHandler oAuthLoginSuccessHandler;
	@MockBean
	protected OAuthLoginFailureHandler oAuthLoginFailureHandler;

	@Autowired
	protected TestFileManager testFileManager;

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

	@AfterEach
	void cleanUp() {
		testFileManager.deleteTestFile();
	}
}

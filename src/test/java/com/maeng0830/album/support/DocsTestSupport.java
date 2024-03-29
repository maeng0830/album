package com.maeng0830.album.support;

import static org.springframework.restdocs.mockmvc.MockMvcRestDocumentation.documentationConfiguration;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.maeng0830.album.comment.controller.CommentController;
import com.maeng0830.album.comment.service.CommentService;
import com.maeng0830.album.common.aop.AopConfig;
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
import com.maeng0830.album.support.util.TestFileManager;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.EnableAspectJAutoProxy;
import org.springframework.context.annotation.Import;
import org.springframework.restdocs.RestDocumentationContextProvider;
import org.springframework.restdocs.RestDocumentationExtension;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.test.web.servlet.setup.SecurityMockMvcConfigurers;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;

@EnableAspectJAutoProxy
@Import({ControllerAndDocsTestConfig.class, AopConfig.class})
@ExtendWith(RestDocumentationExtension.class)
@ActiveProfiles("test")
@WebMvcTest(controllers = {AdminController.class, MemberController.class, FollowController.class,
		FeedController.class, CommentController.class},
		includeFilters = @ComponentScan.Filter(classes = {EnableWebSecurity.class}))
public abstract class DocsTestSupport {

	@Autowired
	protected WebApplicationContext context;

	protected MockMvc mockMvc;

	@Autowired
	protected ObjectMapper objectMapper;

	@Autowired
	protected FileDir fileDir;
	@Autowired
	protected DefaultImage defaultImage;
	@Autowired
	protected UserDetailsService testPrincipalDetailsService;

	protected PrincipalDetails memberPrincipalDetails;

	protected PrincipalDetails adminPrincipalDetails;

	protected PrincipalDetails oauth2MemberPrincipalDetails;

	protected BCryptPasswordEncoder passwordEncoder = new BCryptPasswordEncoder();

	@MockBean
	protected MemberService memberService;
	@MockBean
	protected FollowService followService;
	@MockBean
	protected FeedService feedService;
	@MockBean
	protected CommentService commentService;
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
	void setUp(RestDocumentationContextProvider provider) {
		this.mockMvc = MockMvcBuilders.webAppContextSetup(context)
				.apply(documentationConfiguration(provider))
				.apply(SecurityMockMvcConfigurers.springSecurity())
				.build();

		memberPrincipalDetails =
				(PrincipalDetails) testPrincipalDetailsService.loadUserByUsername("member");

		adminPrincipalDetails =
				(PrincipalDetails) testPrincipalDetailsService.loadUserByUsername("admin");

		oauth2MemberPrincipalDetails =
				(PrincipalDetails) testPrincipalDetailsService.loadUserByUsername("oauth2Member");
	}

	@AfterEach
	void cleanUp() {
		testFileManager.deleteTestFile();
	}
}

package com.maeng0830.album.feed.controller;

import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.user;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.multipart;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.maeng0830.album.common.filedir.FileDir;
import com.maeng0830.album.common.image.DefaultImage;
import com.maeng0830.album.common.util.AlbumUtil;
import com.maeng0830.album.feed.dto.request.FeedAccuseRequestForm;
import com.maeng0830.album.feed.dto.request.FeedModifiedForm;
import com.maeng0830.album.feed.dto.request.FeedPostForm;
import com.maeng0830.album.feed.service.FeedService;
import com.maeng0830.album.security.formlogin.PrincipalDetails;
import com.maeng0830.album.security.formlogin.handler.FormLoginFailureHandler;
import com.maeng0830.album.security.formlogin.handler.FormLoginSuccessHandler;
import com.maeng0830.album.security.oauthlogin.handler.OAuthLoginFailureHandler;
import com.maeng0830.album.security.oauthlogin.handler.OAuthLoginSuccessHandler;
import com.maeng0830.album.support.TestConfig;
import com.maeng0830.album.support.TestPrincipalDetailsService;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Import;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.test.web.servlet.setup.SecurityMockMvcConfigurers;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;

@Import(TestConfig.class)
@ActiveProfiles("test")
@WebMvcTest(controllers = FeedController.class,
		includeFilters = @ComponentScan.Filter(classes = {EnableWebSecurity.class}))
class FeedControllerTest {

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
	private FeedService feedService;
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

	@DisplayName("전체 피드 목록을 조회할 수 있다."
			+ "검색어를 통해 검색어와 작성자 닉네임이 일치하는 피드 목록을 조회할 수 있다."
			+ "페이징 기능을 사용할 수 있다")
	@Test
	void getFeedsForMain() throws Exception {
		// given

		// when

		// then
		mockMvc.perform(
						get("/feeds?searchText=nickname&page=0&size=20")
								.with(csrf())
				)
				.andDo(print())
				.andExpect(status().isOk());
	}

	@DisplayName("특정 피드를 조회할 수 있다.")
	@Test
	void getFeed() throws Exception {
		// given

		// when

		// then
		mockMvc.perform(
						get("/feeds/1")
								.with(csrf())
				)
				.andDo(print())
				.andExpect(status().isOk());
	}

	@DisplayName("로그인한 경우, 피드를 작성할 수 있다.")
	@Test
	void feed() throws Exception {
		// given
		FeedPostForm feedPostForm = FeedPostForm.builder()
				.title("testTitle")
				.content("testContent")
				.build();

		String content = objectMapper.writeValueAsString(feedPostForm);

		MockMultipartFile json = new MockMultipartFile(
				"feedPostForm", "jsondata",
				"application/json", content.getBytes(StandardCharsets.UTF_8));

		List<MockMultipartFile> imageFiles = createImageFiles("imageFiles", "testImage.png",
				"multipart/mixed", fileDir, 2);

		// when

		// then
		mockMvc.perform(
						multipart(HttpMethod.POST, "/feeds")
								.file(imageFiles.get(0))
								.file(imageFiles.get(1))
								.file(json)
								.with(csrf())
								.with(user(memberPrincipalDetails))
				)
				.andDo(print())
				.andExpect(status().isOk());
	}

	@DisplayName("피드 작성 시, 제목은 필수다.")
	@Test
	void feed_blankTitle() throws Exception {
		// given
		FeedPostForm feedPostForm = FeedPostForm.builder()
				.content("testContent")
				.build();

		String content = objectMapper.writeValueAsString(feedPostForm);

		MockMultipartFile json = new MockMultipartFile(
				"feedPostForm", "jsondata",
				"application/json", content.getBytes(StandardCharsets.UTF_8));

		List<MockMultipartFile> imageFiles = createImageFiles("imageFiles", "testImage.png",
				"multipart/mixed", fileDir, 2);

		// when

		// then
		mockMvc.perform(
						multipart(HttpMethod.POST, "/feeds")
								.file(imageFiles.get(0))
								.file(imageFiles.get(1))
								.file(json)
								.with(csrf())
								.with(user(memberPrincipalDetails))
				)
				.andDo(print())
				.andExpect(status().isBadRequest())
				.andExpect(jsonPath("$[0].code").value("NotBlank"))
				.andExpect(jsonPath("$[0].message").value("title, 값을 입력해주시기 바랍니다."));
	}

	@DisplayName("피드 작성 시, 내용은 필수다.")
	@Test
	void feed_nullContent() throws Exception {
		// given
		FeedPostForm feedPostForm = FeedPostForm.builder()
				.title("testTitle")
				.build();

		String content = objectMapper.writeValueAsString(feedPostForm);

		MockMultipartFile json = new MockMultipartFile(
				"feedPostForm", "jsondata",
				"application/json", content.getBytes(StandardCharsets.UTF_8));

		List<MockMultipartFile> imageFiles = createImageFiles("imageFiles", "testImage.png",
				"multipart/mixed", fileDir, 2);

		// when

		// then
		mockMvc.perform(
						multipart(HttpMethod.POST, "/feeds")
								.file(imageFiles.get(0))
								.file(imageFiles.get(1))
								.file(json)
								.with(csrf())
								.with(user(memberPrincipalDetails))
				)
				.andDo(print())
				.andExpect(status().isBadRequest())
				.andExpect(jsonPath("$[0].code").value("NotNull"))
				.andExpect(jsonPath("$[0].message").value("content, 값을 입력해주시기 바랍니다."));
		;
	}

	@DisplayName("로그인한 경우, 피드를 삭제할 수 있다.")
	@Test
	void deleteFeed() throws Exception {
		// given

		// when

		// then
		mockMvc.perform(
						delete("/feeds/1")
								.with(csrf())
								.with(user(memberPrincipalDetails))
				)
				.andDo(print())
				.andExpect(status().isOk());
	}

	@DisplayName("로그인한 경우, 피드를 수정할 수 있다.")
	@Test
	void modifiedFeed() throws Exception {
		// given
		FeedModifiedForm feedModifiedForm = FeedModifiedForm.builder()
				.id(1L)
				.title("testTitle")
				.content("testContent")
				.build();

		String content = objectMapper.writeValueAsString(feedModifiedForm);

		MockMultipartFile json = new MockMultipartFile(
				"feedModifiedForm", "jsondata",
				"application/json", content.getBytes(StandardCharsets.UTF_8));

		List<MockMultipartFile> imageFiles = createImageFiles("imageFiles", "testImage.png",
				"multipart/mixed", fileDir, 2);

		// when

		// then
		mockMvc.perform(
						multipart(HttpMethod.PUT, "/feeds")
								.file(imageFiles.get(0))
								.file(imageFiles.get(1))
								.file(json)
								.with(csrf())
								.with(user(memberPrincipalDetails))
				)
				.andDo(print())
				.andExpect(status().isOk());
	}

	@DisplayName("피드 수정 시, 피드번호는 필수다.")
	@Test
	void modifiedFeed_nullId() throws Exception {
		// given
		FeedModifiedForm feedModifiedForm = FeedModifiedForm.builder()
				.title("testTitle")
				.content("testContent")
				.build();

		String content = objectMapper.writeValueAsString(feedModifiedForm);

		MockMultipartFile json = new MockMultipartFile(
				"feedModifiedForm", "jsondata",
				"application/json", content.getBytes(StandardCharsets.UTF_8));

		List<MockMultipartFile> imageFiles = createImageFiles("imageFiles", "testImage.png",
				"multipart/mixed", fileDir, 2);

		// when

		// then
		mockMvc.perform(
						multipart(HttpMethod.PUT, "/feeds")
								.file(imageFiles.get(0))
								.file(imageFiles.get(1))
								.file(json)
								.with(csrf())
								.with(user(memberPrincipalDetails))
				)
				.andDo(print())
				.andExpect(status().isBadRequest())
				.andExpect(jsonPath("$[0].code").value("NotNull"))
				.andExpect(jsonPath("$[0].message").value("id, 값을 입력해주시기 바랍니다."));
	}

	@DisplayName("피드 수정 시, 제목은 필수다.")
	@Test
	void modifiedFeed_blankTitle() throws Exception {
		// given
		FeedModifiedForm feedModifiedForm = FeedModifiedForm.builder()
				.id(1L)
				.content("testContent")
				.build();

		String content = objectMapper.writeValueAsString(feedModifiedForm);

		MockMultipartFile json = new MockMultipartFile(
				"feedModifiedForm", "jsondata",
				"application/json", content.getBytes(StandardCharsets.UTF_8));

		List<MockMultipartFile> imageFiles = createImageFiles("imageFiles", "testImage.png",
				"multipart/mixed", fileDir, 2);

		// when

		// then
		mockMvc.perform(
						multipart(HttpMethod.PUT, "/feeds")
								.file(imageFiles.get(0))
								.file(imageFiles.get(1))
								.file(json)
								.with(csrf())
								.with(user(memberPrincipalDetails))
				)
				.andDo(print())
				.andExpect(status().isBadRequest())
				.andExpect(jsonPath("$[0].code").value("NotBlank"))
				.andExpect(jsonPath("$[0].message").value("title, 값을 입력해주시기 바랍니다."));
	}

	@DisplayName("피드 수정 시, 내용은 필수다.")
	@Test
	void modifiedFeed_nullContent() throws Exception {
		// given
		FeedModifiedForm feedModifiedForm = FeedModifiedForm.builder()
				.id(1L)
				.title("testTitle")
				.build();

		String content = objectMapper.writeValueAsString(feedModifiedForm);

		MockMultipartFile json = new MockMultipartFile(
				"feedModifiedForm", "jsondata",
				"application/json", content.getBytes(StandardCharsets.UTF_8));

		List<MockMultipartFile> imageFiles = createImageFiles("imageFiles", "testImage.png",
				"multipart/mixed", fileDir, 2);

		// when

		// then
		mockMvc.perform(
						multipart(HttpMethod.PUT, "/feeds")
								.file(imageFiles.get(0))
								.file(imageFiles.get(1))
								.file(json)
								.with(csrf())
								.with(user(memberPrincipalDetails))
				)
				.andDo(print())
				.andExpect(status().isBadRequest())
				.andExpect(jsonPath("$[0].code").value("NotNull"))
				.andExpect(jsonPath("$[0].message").value("content, 값을 입력해주시기 바랍니다."));
	}

	@DisplayName("로그인한 경우, 피드를 신고할 수 있다.")
	@Test
	void accuseFeed() throws Exception {
		// given
		FeedAccuseRequestForm feedAccuseRequestForm = FeedAccuseRequestForm.builder()
				.content("testContent")
				.build();

		// when

		// then
		mockMvc.perform(
						put("/feeds/1/accuse")
								.with(csrf())
								.with(user(memberPrincipalDetails))
								.content(objectMapper.writeValueAsString(feedAccuseRequestForm))
								.contentType(MediaType.APPLICATION_JSON)
				)
				.andDo(print())
				.andExpect(status().isOk());
	}

	@DisplayName("피드 신고 시, 신고 내용은 필수다.")
	@Test
	void accuseFeed_blankContent() throws Exception {
		// given
		FeedAccuseRequestForm feedAccuseRequestForm = FeedAccuseRequestForm.builder()
				.build();

		// when

		// then
		mockMvc.perform(
						put("/feeds/1/accuse")
								.with(csrf())
								.with(user(memberPrincipalDetails))
								.content(objectMapper.writeValueAsString(feedAccuseRequestForm))
								.contentType(MediaType.APPLICATION_JSON)
				)
				.andDo(print())
				.andExpect(status().isBadRequest())
				.andExpect(jsonPath("$[0].code").value("NotBlank"))
				.andExpect(jsonPath("$[0].message").value("content, 값을 입력해주시기 바랍니다."));
	}

	@DisplayName("로그인한 경우, 본인이 작성한 피드 목록을 조회할 수 있다."
			+ "페이징 기능을 사용할 수 있다.")
	@Test
	void getMyFeeds() throws Exception {
		// given

		// when

		// then
		mockMvc.perform(
						get("/feeds/members/1?page=0&size=20")
								.with(csrf())
								.with(user(memberPrincipalDetails))
				)
				.andDo(print())
				.andExpect(status().isOk());
	}

	private MockMultipartFile createImageFile(String name, String originalFilename,
											  String contentType, FileDir fileDir)
			throws IOException {
		String filePath = fileDir.getDir() + originalFilename;
		FileInputStream fileInputStream = new FileInputStream(new File(filePath));

		return new MockMultipartFile(name,
				originalFilename, contentType, fileInputStream);
	}

	private List<MockMultipartFile> createImageFiles(String name, String originalFilename,
													 String contentType, FileDir fileDir, int count)
			throws IOException {
		List<MockMultipartFile> imageFiles = new ArrayList<>();

		for (int i = 0; i < count; i++) {
			imageFiles.add(createImageFile(name, originalFilename,
					contentType, fileDir));
		}

		return imageFiles;
	}
}
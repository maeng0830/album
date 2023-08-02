package com.maeng0830.album.docs.member;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.springframework.http.MediaType.APPLICATION_JSON;
import static org.springframework.restdocs.mockmvc.MockMvcRestDocumentation.document;
import static org.springframework.restdocs.mockmvc.MockMvcRestDocumentation.documentationConfiguration;
import static org.springframework.restdocs.operation.preprocess.Preprocessors.preprocessRequest;
import static org.springframework.restdocs.operation.preprocess.Preprocessors.preprocessResponse;
import static org.springframework.restdocs.operation.preprocess.Preprocessors.prettyPrint;
import static org.springframework.restdocs.payload.PayloadDocumentation.fieldWithPath;
import static org.springframework.restdocs.payload.PayloadDocumentation.requestFields;
import static org.springframework.restdocs.payload.PayloadDocumentation.responseFields;
import static org.springframework.restdocs.request.RequestDocumentation.parameterWithName;
import static org.springframework.restdocs.request.RequestDocumentation.pathParameters;
import static org.springframework.restdocs.request.RequestDocumentation.requestParameters;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.user;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.maeng0830.album.comment.controller.CommentController;
import com.maeng0830.album.comment.domain.CommentStatus;
import com.maeng0830.album.comment.dto.CommentAccuseDto;
import com.maeng0830.album.comment.dto.CommentDto;
import com.maeng0830.album.comment.dto.request.CommentAccuseForm;
import com.maeng0830.album.comment.dto.request.CommentModifiedForm;
import com.maeng0830.album.comment.dto.request.CommentPostForm;
import com.maeng0830.album.comment.dto.response.BasicComment;
import com.maeng0830.album.comment.dto.response.GroupComment;
import com.maeng0830.album.comment.service.CommentService;
import com.maeng0830.album.common.filedir.FileDir;
import com.maeng0830.album.common.image.DefaultImage;
import com.maeng0830.album.common.model.image.Image;
import com.maeng0830.album.common.util.AlbumUtil;
import com.maeng0830.album.feed.domain.FeedStatus;
import com.maeng0830.album.feed.dto.FeedDto;
import com.maeng0830.album.member.domain.MemberRole;
import com.maeng0830.album.member.domain.MemberStatus;
import com.maeng0830.album.member.dto.MemberDto;
import com.maeng0830.album.security.dto.LoginType;
import com.maeng0830.album.security.formlogin.PrincipalDetails;
import com.maeng0830.album.support.TestConfig;
import com.maeng0830.album.support.TestPrincipalDetailsService;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.data.domain.Pageable;
import org.springframework.restdocs.RestDocumentationContextProvider;
import org.springframework.restdocs.RestDocumentationExtension;
import org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;

@ActiveProfiles("test")
@Import(TestConfig.class)
@ExtendWith(RestDocumentationExtension.class)
@WebMvcTest(controllers = CommentController.class)
public class CommentControllerDocsTest {

	@Autowired
	private WebApplicationContext context;

	private MockMvc mockMvc;

	@Autowired
	private ObjectMapper objectMapper;

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
	private AlbumUtil albumUtil;

	@BeforeEach
	void setUp(RestDocumentationContextProvider provider) {
		this.mockMvc = MockMvcBuilders.webAppContextSetup(context)
				.apply(documentationConfiguration(provider))
				.build();

		memberPrincipalDetails =
				(PrincipalDetails) testPrincipalDetailsService.loadUserByUsername("member");

		adminPrincipalDetails =
				(PrincipalDetails) testPrincipalDetailsService.loadUserByUsername("admin");
	}

	@DisplayName("특정 피드 댓글 조회 API")
	@Test
	void getFeedComments() throws Exception {
		// given
		// 댓글 작성자 세팅
		MemberDto writer1 = MemberDto.builder()
				.id(1L)
				.username("writer1@naver.com")
				.nickname("writer1")
				.password(passwordEncoder.encode("123"))
				.phone("010-1111-1111")
				.birthDate(LocalDate.now())
				.status(MemberStatus.NORMAL)
				.role(MemberRole.ROLE_MEMBER)
				.loginType(LoginType.FORM)
				.image(Image.createDefaultImage(fileDir, defaultImage.getMemberImage()))
				.createdAt(LocalDateTime.now())
				.modifiedAt(LocalDateTime.now())
				.modifiedBy("writer1@naver.com")
				.build();

		MemberDto writer2 = MemberDto.builder()
				.id(1L)
				.username("writer2@naver.com")
				.nickname("writer2")
				.password(passwordEncoder.encode("1234"))
				.phone("010-2222-2222")
				.birthDate(LocalDate.now())
				.status(MemberStatus.NORMAL)
				.role(MemberRole.ROLE_MEMBER)
				.loginType(LoginType.FORM)
				.image(Image.createDefaultImage(fileDir, defaultImage.getMemberImage()))
				.createdAt(LocalDateTime.now())
				.modifiedAt(LocalDateTime.now())
				.modifiedBy("writer2@naver.com")
				.build();

		// group 댓글 세팅
		GroupComment groupComment1 = GroupComment.builder()
				.id(1L)
				.feedId(1L)
				.groupId(1L)
				.parentId(1L)
				.member(writer1)
				.content("testContent")
				.status(CommentStatus.NORMAL)
				.basicComments(new ArrayList<>())
				.createdAt(LocalDateTime.now())
				.modifiedAt(LocalDateTime.now())
				.modifiedBy(writer1.getUsername())
				.build();

		GroupComment groupComment2 = GroupComment.builder()
				.id(2L)
				.feedId(1L)
				.groupId(2L)
				.parentId(2L)
				.member(writer2)
				.content("testContent")
				.status(CommentStatus.ACCUSE)
				.basicComments(new ArrayList<>())
				.createdAt(LocalDateTime.now())
				.modifiedAt(LocalDateTime.now())
				.modifiedBy(writer2.getUsername())
				.build();

		// basic 댓글 세팅
		BasicComment basicComment1 = BasicComment.builder()
				.id(3L)
				.feedId(1L)
				.groupId(1L)
				.parentId(1L)
				.parentMember(writer1.getNickname())
				.member(writer2)
				.content("testContent")
				.status(CommentStatus.NORMAL)
				.createdAt(LocalDateTime.now())
				.modifiedAt(LocalDateTime.now())
				.modifiedBy(writer2.getUsername())
				.build();

		BasicComment basicComment2 = BasicComment.builder()
				.id(4L)
				.feedId(1L)
				.groupId(1L)
				.parentId(3L)
				.parentMember(writer2.getNickname())
				.member(writer1)
				.content("testContent")
				.status(CommentStatus.NORMAL)
				.createdAt(LocalDateTime.now())
				.modifiedAt(LocalDateTime.now())
				.modifiedBy(writer1.getUsername())
				.build();

		// 응답 데이터 세팅
		groupComment1.addBasicComments(List.of(basicComment1, basicComment2));

		List<GroupComment> groupComments = List.of(groupComment1, groupComment2);

		given(commentService.getFeedComments(any(Long.class), any(Pageable.class)))
				.willReturn(
						groupComments
				);

		// when

		// then
		mockMvc.perform(
						get("/comments")
								.queryParam("feedId", "1")
								.queryParam("page", "0")
								.queryParam("size", "20")
				)
				.andDo(print())
				.andExpect(status().isOk())
				.andDo(document("get-comments-for-feed",
						preprocessRequest(prettyPrint()),
						preprocessResponse(prettyPrint()),
						requestParameters(
								parameterWithName("feedId").description("피드 번호"),
								parameterWithName("page").description("현재 페이지"),
								parameterWithName("size").description("페이지 당 데이터 개수")
						),
						responseFields(
								fieldWithPath("[].createdAt").description("작성 일자"),
								fieldWithPath("[].modifiedAt").description("수정 일자"),
								fieldWithPath("[].modifiedBy").description("수정자"),
								fieldWithPath("[].id").description("댓글 번호"),
								fieldWithPath("[].feedId").description("피드 번호"),
								fieldWithPath("[].groupId").description("그룹 댓글 번호"),
								fieldWithPath("[].parentId").description("부모 댓글 번호"),
								fieldWithPath("[].content").description("댓글 내용"),
								fieldWithPath("[].status").description("댓글 상태"),
								// member
								fieldWithPath("[].member").description("댓글 작성자"),
								fieldWithPath("[].member.createdAt").description("생성 일자"),
								fieldWithPath("[].member.modifiedAt").description("수정 일자"),
								fieldWithPath("[].member.modifiedBy").description("수정자"),
								fieldWithPath("[].member.id").description("회원 번호"),
								fieldWithPath("[].member.username").description("아이디"),
								fieldWithPath("[].member.nickname").description("닉네임"),
								fieldWithPath("[].member.password").description("암호화 비밀번호"),
								fieldWithPath("[].member.phone").description("연락처"),
								fieldWithPath("[].member.birthDate").description("생년월일"),
								fieldWithPath("[].member.status").description("상태"),
								fieldWithPath("[].member.role").description("권한"),
								fieldWithPath("[].member.image").description("회원 이미지"),
								fieldWithPath("[].member.image.imageOriginalName").description(
										"원본 파일 이름"),
								fieldWithPath("[].member.image.imageStoreName").description(
										"저장 파일 이름"),
								fieldWithPath("[].member.image.imagePath").description("파일 경로"),
								fieldWithPath("[].member.loginType").description("로그인 타입"),
								// basicComments
								fieldWithPath("[].basicComments").description("자식 댓글 목록"),
								fieldWithPath("[].basicComments.[].createdAt").description("작성 일자"),
								fieldWithPath("[].basicComments.[].modifiedAt").description(
										"수정 일자"),
								fieldWithPath("[].basicComments.[].modifiedBy").description("수정자"),
								fieldWithPath("[].basicComments.[].createdAt").description("작성 일자"),
								fieldWithPath("[].basicComments.[].id").description("댓글 번호"),
								fieldWithPath("[].basicComments.[].feedId").description("피드 번호"),
								fieldWithPath("[].basicComments.[].groupId").description(
										"그룹 댓글 번호"),
								fieldWithPath("[].basicComments.[].parentId").description(
										"부모 댓글 번호"),
								fieldWithPath("[].basicComments.[].parentMember").description(
										"부모 댓글 작성자 닉네임"),
								fieldWithPath("[].basicComments.[].content").description("댓글 내용"),
								fieldWithPath("[].basicComments.[].status").description("댓글 상태"),
								fieldWithPath("[].basicComments.[].member").description("댓글 작성자"),
								fieldWithPath("[].basicComments.[].member.createdAt").description(
										"생성 일자"),
								fieldWithPath("[].basicComments.[].member.modifiedAt").description(
										"수정 일자"),
								fieldWithPath("[].basicComments.[].member.modifiedBy").description(
										"수정자"),
								fieldWithPath("[].basicComments.[].member.id").description("회원 번호"),
								fieldWithPath("[].basicComments.[].member.username").description(
										"아이디"),
								fieldWithPath("[].basicComments.[].member.nickname").description(
										"닉네임"),
								fieldWithPath("[].basicComments.[].member.password").description(
										"암호화 비밀번호"),
								fieldWithPath("[].basicComments.[].member.phone").description(
										"연락처"),
								fieldWithPath("[].basicComments.[].member.birthDate").description(
										"생년월일"),
								fieldWithPath("[].basicComments.[].member.status").description(
										"상태"),
								fieldWithPath("[].basicComments.[].member.role").description("권한"),
								fieldWithPath("[].basicComments.[].member.image").description(
										"회원 이미지"),
								fieldWithPath(
										"[].basicComments.[].member.image.imageOriginalName").description(
										"원본 파일 이름"),
								fieldWithPath(
										"[].basicComments.[].member.image.imageStoreName").description(
										"저장 파일 이름"),
								fieldWithPath(
										"[].basicComments.[].member.image.imagePath").description(
										"파일 경로"),
								fieldWithPath("[].basicComments.[].member.loginType").description(
										"로그인 타입")
						)
				));
	}

	@DisplayName("특정 댓글 조회 API")
	@Test
	void getComment() throws Exception {
		// given
		MemberDto writer = MemberDto.builder()
				.id(1L)
				.username("writer@naver.com")
				.nickname("writer")
				.password(passwordEncoder.encode("123"))
				.phone("010-1111-1111")
				.birthDate(LocalDate.now())
				.status(MemberStatus.NORMAL)
				.role(MemberRole.ROLE_MEMBER)
				.loginType(LoginType.FORM)
				.image(Image.createDefaultImage(fileDir, defaultImage.getMemberImage()))
				.createdAt(LocalDateTime.now())
				.modifiedAt(LocalDateTime.now())
				.modifiedBy("writer@naver.com")
				.build();

		BasicComment basicComment = BasicComment.builder()
				.id(1L)
				.feedId(1L)
				.groupId(1L)
				.parentId(1L)
				.parentMember(writer.getNickname())
				.member(writer)
				.content("testContent")
				.status(CommentStatus.NORMAL)
				.createdAt(LocalDateTime.now())
				.modifiedAt(LocalDateTime.now())
				.modifiedBy(writer.getUsername())
				.build();

		given(commentService.getComment(any(Long.class)))
				.willReturn(
						basicComment
				);
		// when

		// then
		mockMvc.perform(
						RestDocumentationRequestBuilders.get("/comments/{commentId}", 1)
				)
				.andDo(print())
				.andExpect(status().isOk())
				.andDo(document("get-comment",
						preprocessRequest(prettyPrint()),
						preprocessResponse(prettyPrint()),
						pathParameters(
								parameterWithName("commentId").description("댓글 번호")
						),
						responseFields(
								fieldWithPath("createdAt").description("작성 일자"),
								fieldWithPath("modifiedAt").description("수정 일자"),
								fieldWithPath("modifiedBy").description("수정자"),
								fieldWithPath("createdAt").description("작성 일자"),
								fieldWithPath("id").description("댓글 번호"),
								fieldWithPath("feedId").description("피드 번호"),
								fieldWithPath("groupId").description("그룹 댓글 번호"),
								fieldWithPath("parentId").description("부모 댓글 번호"),
								fieldWithPath("parentMember").description("부모 댓글 작성자 닉네임"),
								fieldWithPath("content").description("댓글 내용"),
								fieldWithPath("status").description("댓글 상태"),
								fieldWithPath("member").description("댓글 작성자"),
								fieldWithPath("member.createdAt").description("생성 일자"),
								fieldWithPath("member.modifiedAt").description("수정 일자"),
								fieldWithPath("member.modifiedBy").description("수정자"),
								fieldWithPath("member.id").description("회원 번호"),
								fieldWithPath("member.username").description("아이디"),
								fieldWithPath("member.nickname").description("닉네임"),
								fieldWithPath("member.password").description("암호화 비밀번호"),
								fieldWithPath("member.phone").description("연락처"),
								fieldWithPath("member.birthDate").description("생년월일"),
								fieldWithPath("member.status").description("상태"),
								fieldWithPath("member.role").description("권한"),
								fieldWithPath("member.image").description("회원 이미지"),
								fieldWithPath("member.image.imageOriginalName").description(
										"원본 파일 이름"),
								fieldWithPath("member.image.imageStoreName").description(
										"저장 파일 이름"),
								fieldWithPath("member.image.imagePath").description("파일 경로"),
								fieldWithPath("member.loginType").description("로그인 타입")
						)
				));
	}

	@DisplayName("댓글 작성 API")
	@Test
	void comment() throws Exception {
		// given
		MemberDto memberDto = memberPrincipalDetails.getMemberDto();

		CommentPostForm commentPostForm = CommentPostForm.builder()
				.feedId(1L)
				.groupId(1L)
				.parentId(1L)
				.content("testContent")
				.build();

		BasicComment basicComment = BasicComment.builder()
				.id(1L)
				.feedId(commentPostForm.getFeedId())
				.groupId(commentPostForm.getGroupId())
				.parentId(commentPostForm.getParentId())
				.parentMember("parentCommentWriter")
				.member(memberDto)
				.content(commentPostForm.getContent())
				.status(CommentStatus.NORMAL)
				.createdAt(LocalDateTime.now())
				.modifiedAt(LocalDateTime.now())
				.modifiedBy(memberDto.getUsername())
				.build();

		given(commentService.comment(any(CommentPostForm.class), any()))
				.willReturn(
						basicComment
				);
		// when

		// then
		mockMvc.perform(
						post("/comments")
								.with(user(memberPrincipalDetails))
								.content(objectMapper.writeValueAsString(commentPostForm))
								.contentType(APPLICATION_JSON)
				)
				.andDo(print())
				.andExpect(status().isOk())
				.andDo(document("comment",
						preprocessRequest(prettyPrint()),
						preprocessResponse(prettyPrint()),
						requestFields(
								fieldWithPath("feedId").description("피드 번호"),
								fieldWithPath("groupId").description("그룹 댓글 번호"),
								fieldWithPath("parentId").description("부모 댓글 번호"),
								fieldWithPath("content").description("댓글 내용")
						),
						responseFields(
								fieldWithPath("createdAt").description("작성 일자"),
								fieldWithPath("modifiedAt").description("수정 일자"),
								fieldWithPath("modifiedBy").description("수정자"),
								fieldWithPath("createdAt").description("작성 일자"),
								fieldWithPath("id").description("댓글 번호"),
								fieldWithPath("feedId").description("피드 번호"),
								fieldWithPath("groupId").description("그룹 댓글 번호"),
								fieldWithPath("parentId").description("부모 댓글 번호"),
								fieldWithPath("parentMember").description("부모 댓글 작성자 닉네임"),
								fieldWithPath("content").description("댓글 내용"),
								fieldWithPath("status").description("댓글 상태"),
								fieldWithPath("member").description("댓글 작성자"),
								fieldWithPath("member.createdAt").description("생성 일자"),
								fieldWithPath("member.modifiedAt").description("수정 일자"),
								fieldWithPath("member.modifiedBy").description("수정자"),
								fieldWithPath("member.id").description("회원 번호"),
								fieldWithPath("member.username").description("아이디"),
								fieldWithPath("member.nickname").description("닉네임"),
								fieldWithPath("member.password").description("암호화 비밀번호"),
								fieldWithPath("member.phone").description("연락처"),
								fieldWithPath("member.birthDate").description("생년월일"),
								fieldWithPath("member.status").description("상태"),
								fieldWithPath("member.role").description("권한"),
								fieldWithPath("member.image").description("회원 이미지"),
								fieldWithPath("member.image.imageOriginalName").description(
										"원본 파일 이름"),
								fieldWithPath("member.image.imageStoreName").description(
										"저장 파일 이름"),
								fieldWithPath("member.image.imagePath").description("파일 경로"),
								fieldWithPath("member.loginType").description("로그인 타입")
						)
				));
	}

	@DisplayName("댓글 수정 API")
	@Test
	void modifiedComment() throws Exception {
		// given
		MemberDto memberDto = memberPrincipalDetails.getMemberDto();

		CommentModifiedForm commentModifiedForm = CommentModifiedForm.builder()
				.id(1L)
				.content("modifiedContent")
				.build();

		BasicComment basicComment = BasicComment.builder()
				.id(commentModifiedForm.getId())
				.feedId(1L)
				.groupId(1L)
				.parentId(1L)
				.parentMember("parentCommentWriter")
				.member(memberDto)
				.content(commentModifiedForm.getContent())
				.status(CommentStatus.NORMAL)
				.createdAt(LocalDateTime.now())
				.modifiedAt(LocalDateTime.now())
				.modifiedBy(memberDto.getUsername())
				.build();

		given(commentService.modifiedComment(any(CommentModifiedForm.class), any()))
				.willReturn(
						basicComment
				);
		// when

		// then
		mockMvc.perform(
						put("/comments")
								.with(user(memberPrincipalDetails))
								.content(objectMapper.writeValueAsString(commentModifiedForm))
								.contentType(APPLICATION_JSON)
				)
				.andDo(print())
				.andExpect(status().isOk())
				.andDo(document("modified-comment",
						preprocessRequest(prettyPrint()),
						preprocessResponse(prettyPrint()),
						requestFields(
								fieldWithPath("id").description("댓글 번호"),
								fieldWithPath("content").description("수정 댓글 내용")
						),
						responseFields(
								fieldWithPath("createdAt").description("작성 일자"),
								fieldWithPath("modifiedAt").description("수정 일자"),
								fieldWithPath("modifiedBy").description("수정자"),
								fieldWithPath("createdAt").description("작성 일자"),
								fieldWithPath("id").description("댓글 번호"),
								fieldWithPath("feedId").description("피드 번호"),
								fieldWithPath("groupId").description("그룹 댓글 번호"),
								fieldWithPath("parentId").description("부모 댓글 번호"),
								fieldWithPath("parentMember").description("부모 댓글 작성자 닉네임"),
								fieldWithPath("content").description("댓글 내용"),
								fieldWithPath("status").description("댓글 상태"),
								fieldWithPath("member").description("댓글 작성자"),
								fieldWithPath("member.createdAt").description("생성 일자"),
								fieldWithPath("member.modifiedAt").description("수정 일자"),
								fieldWithPath("member.modifiedBy").description("수정자"),
								fieldWithPath("member.id").description("회원 번호"),
								fieldWithPath("member.username").description("아이디"),
								fieldWithPath("member.nickname").description("닉네임"),
								fieldWithPath("member.password").description("암호화 비밀번호"),
								fieldWithPath("member.phone").description("연락처"),
								fieldWithPath("member.birthDate").description("생년월일"),
								fieldWithPath("member.status").description("상태"),
								fieldWithPath("member.role").description("권한"),
								fieldWithPath("member.image").description("회원 이미지"),
								fieldWithPath("member.image.imageOriginalName").description(
										"원본 파일 이름"),
								fieldWithPath("member.image.imageStoreName").description(
										"저장 파일 이름"),
								fieldWithPath("member.image.imagePath").description("파일 경로"),
								fieldWithPath("member.loginType").description("로그인 타입")
						)
				));
	}

	@DisplayName("댓글 신고 API")
	@Test
	void accuseComment() throws Exception {
		// given
		CommentAccuseForm commentAccuseForm = CommentAccuseForm.builder()
				.content("accuseContent")
				.build();

		MemberDto feedWriter = MemberDto.builder()
				.id(3L)
				.username("feedWriter@naver.com")
				.nickname("feedWriter")
				.password(passwordEncoder.encode("123"))
				.phone("010-1111-1111")
				.birthDate(LocalDate.now())
				.status(MemberStatus.NORMAL)
				.role(MemberRole.ROLE_MEMBER)
				.loginType(LoginType.FORM)
				.image(Image.createDefaultImage(fileDir, defaultImage.getMemberImage()))
				.createdAt(LocalDateTime.now())
				.modifiedAt(LocalDateTime.now())
				.modifiedBy("feedWriter@naver.com")
				.build();

		MemberDto commentWriter = MemberDto.builder()
				.id(4L)
				.username("commentWriter@naver.com")
				.nickname("commentWriter")
				.password(passwordEncoder.encode("1234"))
				.phone("010-2222-2222")
				.birthDate(LocalDate.now())
				.status(MemberStatus.NORMAL)
				.role(MemberRole.ROLE_MEMBER)
				.loginType(LoginType.FORM)
				.image(Image.createDefaultImage(fileDir, defaultImage.getMemberImage()))
				.createdAt(LocalDateTime.now())
				.modifiedAt(LocalDateTime.now())
				.modifiedBy("commentWriter@naver.com")
				.build();

		FeedDto feedDto = FeedDto.builder()
				.id(1L)
				.title("testTitle")
				.content("testContent")
				.hits(1)
				.commentCount(1)
				.status(FeedStatus.NORMAL)
				.memberDto(feedWriter)
				.createdAt(LocalDateTime.now())
				.modifiedAt(LocalDateTime.now())
				.modifiedBy(feedWriter.getUsername())
				.build();

		CommentDto commentDto = CommentDto.builder()
				.id(1L)
				.member(commentWriter)
				.feed(feedDto)
				.groupId(1L)
				.parentId(1L)
				.content("testContent")
				.status(CommentStatus.ACCUSE)
				.createdAt(LocalDateTime.now())
				.modifiedAt(LocalDateTime.now())
				.modifiedBy(commentWriter.getUsername())
				.build();

		CommentAccuseDto commentAccuseDto = CommentAccuseDto.builder()
				.id(1L)
				.comment(commentDto)
				.member(memberPrincipalDetails.getMemberDto())
				.content(commentAccuseForm.getContent())
				.createdAt(LocalDateTime.now())
				.modifiedAt(LocalDateTime.now())
				.modifiedBy(memberPrincipalDetails.getMemberDto().getUsername())
				.build();

		given(commentService.accuseComment(any(Long.class), any(CommentAccuseForm.class), any()))
				.willReturn(
						commentAccuseDto
				);
		// when

		// then
		mockMvc.perform(
						RestDocumentationRequestBuilders.put("/comments/{commentId}/accuse", 1)
								.with(user(memberPrincipalDetails))
								.content(objectMapper.writeValueAsString(commentAccuseForm))
								.contentType(APPLICATION_JSON)
				)
				.andDo(print())
				.andExpect(status().isOk())
				.andDo(document("accuse-comment",
						preprocessRequest(prettyPrint()),
						preprocessResponse(prettyPrint()),
						pathParameters(
								parameterWithName("commentId").description("댓글 번호")
						),
						requestFields(
								fieldWithPath("content").description("신고 내용")
						),
						responseFields(
								fieldWithPath("id").description("댓글 신고 번호"),
								fieldWithPath("content").description("신고 내용"),
								fieldWithPath("createdAt").description("생성 일자"),
								fieldWithPath("modifiedAt").description("수정 일자"),
								fieldWithPath("modifiedBy").description("수정자"),
								// member
								fieldWithPath("member").description("댓글 신고자"),
								fieldWithPath("member.createdAt").description("생성 일자"),
								fieldWithPath("member.modifiedAt").description("수정 일자"),
								fieldWithPath("member.modifiedBy").description("수정자"),
								fieldWithPath("member.id").description("회원 번호"),
								fieldWithPath("member.username").description("아이디"),
								fieldWithPath("member.nickname").description("닉네임"),
								fieldWithPath("member.password").description("암호화 비밀번호"),
								fieldWithPath("member.phone").description("연락처"),
								fieldWithPath("member.birthDate").description("생년월일"),
								fieldWithPath("member.status").description("상태"),
								fieldWithPath("member.role").description("권한"),
								fieldWithPath("member.image").description("회원 이미지"),
								fieldWithPath("member.image.imageOriginalName").description(
										"원본 파일 이름"),
								fieldWithPath("member.image.imageStoreName").description(
										"저장 파일 이름"),
								fieldWithPath("member.image.imagePath").description("파일 경로"),
								fieldWithPath("member.loginType").description("로그인 타입"),
								// comment
								fieldWithPath("comment").description("신고 댓글"),
								fieldWithPath("comment.id").description("댓글 번호"),
								fieldWithPath("comment.groupId").description("그룹 댓글 번호"),
								fieldWithPath("comment.parentId").description("부모 댓글 번호"),
								fieldWithPath("comment.content").description("댓글 내용"),
								fieldWithPath("comment.status").description("상태"),
								fieldWithPath("comment.createdAt").description("생성 일자"),
								fieldWithPath("comment.modifiedAt").description("수정 일자"),
								fieldWithPath("comment.modifiedBy").description("수정자"),
								//// comment.member
								fieldWithPath("comment.member").description("댓글 작성자"),
								fieldWithPath("comment.member.createdAt").description("생성 일자"),
								fieldWithPath("comment.member.modifiedAt").description("수정 일자"),
								fieldWithPath("comment.member.modifiedBy").description("수정자"),
								fieldWithPath("comment.member.id").description("회원 번호"),
								fieldWithPath("comment.member.username").description("아이디"),
								fieldWithPath("comment.member.nickname").description("닉네임"),
								fieldWithPath("comment.member.password").description("암호화 비밀번호"),
								fieldWithPath("comment.member.phone").description("연락처"),
								fieldWithPath("comment.member.birthDate").description("생년월일"),
								fieldWithPath("comment.member.status").description("상태"),
								fieldWithPath("comment.member.role").description("권한"),
								fieldWithPath("comment.member.image").description("회원 이미지"),
								fieldWithPath("comment.member.image.imageOriginalName").description(
										"원본 파일 이름"),
								fieldWithPath("comment.member.image.imageStoreName").description(
										"저장 파일 이름"),
								fieldWithPath("comment.member.image.imagePath").description(
										"파일 경로"),
								fieldWithPath("comment.member.loginType").description("로그인 타입"),
								//// comment.feed
								fieldWithPath("comment.feed").description("댓글 관련 피드"),
								fieldWithPath("comment.feed.createdAt").description("생성 일자"),
								fieldWithPath("comment.feed.modifiedAt").description("수정 일자"),
								fieldWithPath("comment.feed.modifiedBy").description("수정자"),
								fieldWithPath("comment.feed.id").description("피드 번호"),
								fieldWithPath("comment.feed.title").description("피드 제목"),
								fieldWithPath("comment.feed.content").description("피드 내용"),
								fieldWithPath("comment.feed.hits").description("조회수"),
								fieldWithPath("comment.feed.commentCount").description("댓글 개수"),
								fieldWithPath("comment.feed.status").description("상태"),
								////// comment.feed.memberDto
								fieldWithPath("comment.feed.memberDto").description("피드 작성자"),
								fieldWithPath("comment.feed.memberDto.createdAt").description(
										"생성 일자"),
								fieldWithPath("comment.feed.memberDto.modifiedAt").description(
										"수정 일자"),
								fieldWithPath("comment.feed.memberDto.modifiedBy").description(
										"수정자"),
								fieldWithPath("comment.feed.memberDto.id").description("회원 번호"),
								fieldWithPath("comment.feed.memberDto.username").description("아이디"),
								fieldWithPath("comment.feed.memberDto.nickname").description("닉네임"),
								fieldWithPath("comment.feed.memberDto.password").description(
										"암호화 비밀번호"),
								fieldWithPath("comment.feed.memberDto.phone").description("연락처"),
								fieldWithPath("comment.feed.memberDto.birthDate").description(
										"생년월일"),
								fieldWithPath("comment.feed.memberDto.status").description("상태"),
								fieldWithPath("comment.feed.memberDto.role").description("권한"),
								fieldWithPath("comment.feed.memberDto.image").description("회원 이미지"),
								fieldWithPath(
										"comment.feed.memberDto.image.imageOriginalName").description(
										"원본 파일 이름"),
								fieldWithPath(
										"comment.feed.memberDto.image.imageStoreName").description(
										"저장 파일 이름"),
								fieldWithPath("comment.feed.memberDto.image.imagePath").description(
										"파일 경로"),
								fieldWithPath("comment.feed.memberDto.loginType").description(
										"로그인 타입")
						)
				));
	}

	@DisplayName("댓글 삭제 API")
	@Test
	void deleteComment() throws Exception {
		// given
		MemberDto memberDto = memberPrincipalDetails.getMemberDto();

		BasicComment basicComment = BasicComment.builder()
				.id(1L)
				.feedId(1L)
				.groupId(1L)
				.parentId(1L)
				.parentMember("parentCommentWriter")
				.member(memberDto)
				.content("testContent")
				.status(CommentStatus.DELETE)
				.createdAt(LocalDateTime.now())
				.modifiedAt(LocalDateTime.now())
				.modifiedBy(memberDto.getUsername())
				.build();

		given(commentService.deleteComment(any(Long.class), any()))
				.willReturn(
						basicComment
				);
		// when

		// then
		mockMvc.perform(
						RestDocumentationRequestBuilders.delete("/comments/{commentId}", 1)
								.with(user(memberPrincipalDetails))
				)
				.andDo(print())
				.andExpect(status().isOk())
				.andDo(document("delete-comment",
						preprocessRequest(prettyPrint()),
						preprocessResponse(prettyPrint()),
						pathParameters(
								parameterWithName("commentId").description("댓글 번호")
						),
						responseFields(
								fieldWithPath("createdAt").description("작성 일자"),
								fieldWithPath("modifiedAt").description("수정 일자"),
								fieldWithPath("modifiedBy").description("수정자"),
								fieldWithPath("createdAt").description("작성 일자"),
								fieldWithPath("id").description("댓글 번호"),
								fieldWithPath("feedId").description("피드 번호"),
								fieldWithPath("groupId").description("그룹 댓글 번호"),
								fieldWithPath("parentId").description("부모 댓글 번호"),
								fieldWithPath("parentMember").description("부모 댓글 작성자 닉네임"),
								fieldWithPath("content").description("댓글 내용"),
								fieldWithPath("status").description("댓글 상태"),
								fieldWithPath("member").description("댓글 작성자"),
								fieldWithPath("member.createdAt").description("생성 일자"),
								fieldWithPath("member.modifiedAt").description("수정 일자"),
								fieldWithPath("member.modifiedBy").description("수정자"),
								fieldWithPath("member.id").description("회원 번호"),
								fieldWithPath("member.username").description("아이디"),
								fieldWithPath("member.nickname").description("닉네임"),
								fieldWithPath("member.password").description("암호화 비밀번호"),
								fieldWithPath("member.phone").description("연락처"),
								fieldWithPath("member.birthDate").description("생년월일"),
								fieldWithPath("member.status").description("상태"),
								fieldWithPath("member.role").description("권한"),
								fieldWithPath("member.image").description("회원 이미지"),
								fieldWithPath("member.image.imageOriginalName").description(
										"원본 파일 이름"),
								fieldWithPath("member.image.imageStoreName").description(
										"저장 파일 이름"),
								fieldWithPath("member.image.imagePath").description("파일 경로"),
								fieldWithPath("member.loginType").description("로그인 타입")
						)
				));
	}
}

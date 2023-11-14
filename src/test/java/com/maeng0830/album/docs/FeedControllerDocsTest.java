package com.maeng0830.album.docs;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.springframework.http.MediaType.APPLICATION_JSON;
import static org.springframework.restdocs.mockmvc.MockMvcRestDocumentation.document;
import static org.springframework.restdocs.operation.preprocess.Preprocessors.preprocessRequest;
import static org.springframework.restdocs.operation.preprocess.Preprocessors.preprocessResponse;
import static org.springframework.restdocs.operation.preprocess.Preprocessors.prettyPrint;
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
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.multipart;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.maeng0830.album.common.filedir.FileDir;
import com.maeng0830.album.common.model.image.Image;
import com.maeng0830.album.feed.domain.Feed;
import com.maeng0830.album.feed.domain.FeedImage;
import com.maeng0830.album.feed.domain.FeedStatus;
import com.maeng0830.album.feed.dto.FeedAccuseDto;
import com.maeng0830.album.feed.dto.FeedDto;
import com.maeng0830.album.feed.dto.FeedResponse;
import com.maeng0830.album.feed.dto.request.FeedAccuseRequestForm;
import com.maeng0830.album.feed.dto.request.FeedModifiedForm;
import com.maeng0830.album.feed.dto.request.FeedPostForm;
import com.maeng0830.album.member.domain.Member;
import com.maeng0830.album.member.domain.MemberRole;
import com.maeng0830.album.member.domain.MemberStatus;
import com.maeng0830.album.member.dto.MemberDto;
import com.maeng0830.album.member.dto.response.MemberSimpleResponse;
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
import java.util.UUID;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.domain.Sort.Direction;
import org.springframework.http.HttpMethod;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders;

public class FeedControllerDocsTest extends DocsTestSupport {

	@DisplayName("정상 및 신고 상태인 피드(로그인 상태인 경우, 팔로워 및 팔로잉이 작성한 피드) 조회 API")
	@Test
	void getFeedsForMain() throws Exception {
		// given
		// 작성자 세팅
		Member member1 = Member.builder()
				.id(2L)
				.username("username1@naver.com")
				.nickname("nickname1")
				.password(passwordEncoder.encode("123"))
				.phone("010-1111-1111")
				.birthDate(LocalDate.now())
				.role(MemberRole.ROLE_MEMBER)
				.status(MemberStatus.NORMAL)
				.loginType(LoginType.FORM)
				.image(Image.createDefaultImage(fileDir, defaultImage.getMemberImage()))
				.createdAt(LocalDateTime.now())
				.modifiedAt(LocalDateTime.now())
				.modifiedBy("username1@naver.com")
				.build();

		Member member2 = Member.builder()
				.id(3L)
				.username("username2@naver.com")
				.nickname("nickname2")
				.password(passwordEncoder.encode("123"))
				.phone("010-2222-2222")
				.birthDate(LocalDate.now())
				.role(MemberRole.ROLE_MEMBER)
				.status(MemberStatus.NORMAL)
				.loginType(LoginType.FORM)
				.image(Image.createDefaultImage(fileDir, defaultImage.getMemberImage()))
				.createdAt(LocalDateTime.now())
				.modifiedAt(LocalDateTime.now())
				.modifiedBy("username2@naver.com")
				.build();

		// 피드 이미지 세팅
		FeedImage feedImage1 = FeedImage.builder()
				.id(1L)
				.feed(null)
				.image(Image.createDefaultImage(fileDir, defaultImage.getFeedImage()))
				.createdAt(LocalDateTime.now())
				.modifiedAt(LocalDateTime.now())
				.modifiedBy(member1.getUsername())
				.build();

		FeedImage feedImage2 = FeedImage.builder()
				.id(2L)
				.feed(null)
				.image(Image.createDefaultImage(fileDir, defaultImage.getFeedImage()))
				.createdAt(LocalDateTime.now())
				.modifiedAt(LocalDateTime.now())
				.modifiedBy(member2.getUsername())
				.build();

		// 피드 세팅
		Feed feed1 = Feed.builder()
				.id(1L)
				.member(member1)
				.title("title1")
				.content("content1")
				.status(FeedStatus.NORMAL)
				.hits(1)
				.commentCount(1)
				.feedImages(new ArrayList<>())
				.createdAt(LocalDateTime.now())
				.modifiedAt(LocalDateTime.now())
				.modifiedBy(member1.getUsername())
				.build();
		feed1.addFeedImage(feedImage1);
		FeedResponse feedResponse1 = FeedResponse.createFeedResponse(feed1, feed1.getFeedImages());

		Feed feed2 = Feed.builder()
				.id(2L)
				.member(member1)
				.title("title2")
				.content("content2")
				.status(FeedStatus.ACCUSE)
				.hits(1)
				.commentCount(1)
				.feedImages(new ArrayList<>())
				.createdAt(LocalDateTime.now())
				.modifiedAt(LocalDateTime.now())
				.modifiedBy(member2.getUsername())
				.build();
		feed2.addFeedImage(feedImage2);
		FeedResponse feedResponse2 = FeedResponse.createFeedResponse(feed2, feed2.getFeedImages());

		// 응답 데이터 세팅
		List<FeedResponse> content = List.of(feedResponse1, feedResponse2);

		PageRequest pageRequest = PageRequest.of(0, 20, Sort.by(Direction.DESC, "hits"));

		Page<FeedResponse> feedResponses = new PageImpl<>(content, pageRequest, 2);

		given(feedService.getFeedsForMainWithSearchText(any(String.class), any(Pageable.class)))
				.willReturn(
						feedResponses
				);

		// when

		// then
		mockMvc.perform(
						get("/feeds")
								.queryParam("searchText", "nickname")
								.queryParam("page", "0")
								.queryParam("size", "20")
				)
				.andDo(print())
				.andExpect(status().isOk())
				.andDo(document("get-feeds-main",
						preprocessRequest(prettyPrint()),
						preprocessResponse(prettyPrint()),
						requestParameters(
								parameterWithName("searchText").description("검색어(닉네임, 전방 일치)"),
								parameterWithName("page").description("페이지 번호"),
								parameterWithName("size").description("페이지 당 데이터 개수")
						),
						responseFields(
								fieldWithPath("content.[].id").description("피드 번호"),
								fieldWithPath("content.[].title").description("피드 제목"),
								fieldWithPath("content.[].content").description("피드 내용"),
								fieldWithPath("content.[].hits").description("조회수"),
								fieldWithPath("content.[].commentCount").description("댓글 개수"),
								fieldWithPath("content.[].status").description("피드 상태"),
								fieldWithPath("content.[].createdAt").description("작성 일자"),
								fieldWithPath("content.[].modifiedAt").description("수정 일자"),
								fieldWithPath("content.[].modifiedBy").description("수정자"),
								fieldWithPath("content.[].member").description("피드 작성자"),
								fieldWithPath("content.[].member.id").description("회원 번호"),
								fieldWithPath("content.[].member.username").description("아이디"),
								fieldWithPath("content.[].member.nickname").description("닉네임"),
								fieldWithPath("content.[].member.image").description("이미지"),
								fieldWithPath("content.[].member.image.imageOriginalName")
										.description("원본 이름"),
								fieldWithPath("content.[].member.image.imageStoreName")
										.description("저장 이름"),
								fieldWithPath("content.[].member.image.imagePath")
										.description("경로"),
								fieldWithPath("content.[].feedImages.[].imageOriginalName")
										.description("파일 원본 이름"),
								fieldWithPath("content.[].feedImages.[].imageStoreName")
										.description("파일 저장 이름"),
								fieldWithPath("content.[].feedImages.[].imagePath")
										.description("파일 경로"),

								fieldWithPath("pageable.sort.sorted").description("정렬"),
								fieldWithPath("pageable.sort.empty").description("데이터 비어있는지 여부"),
								fieldWithPath("pageable.sort.unsorted").description("비정렬"),

								fieldWithPath("pageable.offset").description("첫 데이터 인덱스"),
								fieldWithPath("pageable.pageNumber").description("현재 페이지 번호"),
								fieldWithPath("pageable.pageSize").description("페이지 당 데이터 개수"),
								fieldWithPath("pageable.paged").description("페이징 정보 포함"),
								fieldWithPath("pageable.unpaged").description("페이징 정보 비포함"),

								fieldWithPath("last").description("마지막 페이지 여부"),
								fieldWithPath("totalPages").description("전체 페이지 개수"),
								fieldWithPath("totalElements").description("전체 데이터 개수"),
								fieldWithPath("first").description("첫번째 페이지 여부"),
								fieldWithPath("numberOfElements").description(
										"현재 페이지에서 조회된 데이터 개수"),
								fieldWithPath("number").description("현재 페이지 번호"),
								fieldWithPath("size").description("페이지 당 데이터 개수"),

								fieldWithPath("sort.sorted").description("정렬"),
								fieldWithPath("sort.unsorted").description("비정렬"),
								fieldWithPath("sort.empty").description("데이터 비어있는지 여부"),

								fieldWithPath("empty").description("데이터 비어있는지 여부")
						)
				));
	}

	@DisplayName("특정 피드 조회 API")
	@Test
	void getFeed() throws Exception {
		//given
		// 작성자 세팅
		Member member1 = Member.builder()
				.id(2L)
				.username("username1@naver.com")
				.nickname("nickname1")
				.password(passwordEncoder.encode("123"))
				.phone("010-1111-1111")
				.birthDate(LocalDate.now())
				.role(MemberRole.ROLE_MEMBER)
				.status(MemberStatus.NORMAL)
				.loginType(LoginType.FORM)
				.image(Image.createDefaultImage(fileDir, defaultImage.getMemberImage()))
				.createdAt(LocalDateTime.now())
				.modifiedAt(LocalDateTime.now())
				.modifiedBy("username1@naver.com")
				.build();

		// 피드 이미지 세팅
		FeedImage feedImage1 = FeedImage.builder()
				.id(1L)
				.feed(null)
				.image(Image.createDefaultImage(fileDir, defaultImage.getFeedImage()))
				.createdAt(LocalDateTime.now())
				.modifiedAt(LocalDateTime.now())
				.modifiedBy(member1.getUsername())
				.build();

		// 피드 세팅
		Feed feed1 = Feed.builder()
				.id(1L)
				.member(member1)
				.title("title1")
				.content("content1")
				.status(FeedStatus.NORMAL)
				.hits(1)
				.commentCount(1)
				.feedImages(new ArrayList<>())
				.createdAt(LocalDateTime.now())
				.modifiedAt(LocalDateTime.now())
				.modifiedBy(member1.getUsername())
				.build();
		feed1.addFeedImage(feedImage1);
		FeedResponse feedResponse1 = FeedResponse.createFeedResponse(feed1, feed1.getFeedImages());

		given(feedService.getFeed(any(Long.class)))
				.willReturn(
						feedResponse1
				);
		// when

		// then
		mockMvc.perform(
						RestDocumentationRequestBuilders.get("/feeds/{feedId}", 1)
				)
				.andDo(print())
				.andExpect(status().isOk())
				.andDo(document("get-feed",
						preprocessRequest(prettyPrint()),
						preprocessResponse(prettyPrint()),
						pathParameters(
								parameterWithName("feedId").description("피드 아이디")
						),
						responseFields(
								fieldWithPath("id").description("피드 번호"),
								fieldWithPath("title").description("피드 제목"),
								fieldWithPath("content").description("피드 내용"),
								fieldWithPath("hits").description("조회수"),
								fieldWithPath("commentCount").description("댓글 개수"),
								fieldWithPath("status").description("피드 상태"),
								fieldWithPath("createdAt").description("작성 일자"),
								fieldWithPath("modifiedAt").description("수정 일자"),
								fieldWithPath("modifiedBy").description("수정자"),
								fieldWithPath("member.id").description("회원 번호"),
								fieldWithPath("member.username").description("아이디"),
								fieldWithPath("member.nickname").description("닉네임"),
								fieldWithPath("member.image").description("이미지"),
								fieldWithPath("member.image.imageOriginalName")
										.description("원본 이름"),
								fieldWithPath("member.image.imageStoreName").description("저장 이름"),
								fieldWithPath("member.image.imagePath").description("경로"),
								fieldWithPath("feedImages.[].imageOriginalName")
										.description("파일 원본 이름"),
								fieldWithPath("feedImages.[].imageStoreName")
										.description("파일 저장 이름"),
								fieldWithPath("feedImages.[].imagePath").description("파일 경로")
						)
				));

	}

	@DisplayName("피드 작성 API")
	@Test
	void feed() throws Exception {
		// given
		// Request 데이터 세팅
		FeedPostForm feedPostForm = FeedPostForm.builder()
				.title("testTitle")
				.content("testContent")
				.build();

		String content = objectMapper.writeValueAsString(feedPostForm);

		MockMultipartFile json = new MockMultipartFile(
				"feedPostForm", "jsondata",
				"application/json", content.getBytes(StandardCharsets.UTF_8));

		List<MockMultipartFile> imageFiles = createImageFiles("imageFiles", "testImage.PNG",
				"multipart/mixed", fileDir, 2);

		// Response 데이터 세팅
		UUID uuid1 = UUID.randomUUID();
		UUID uuid2 = UUID.randomUUID();

		Image image1 = Image.builder()
				.imageOriginalName("testImage.PNG")
				.imageStoreName(uuid1 + ".PNG")
				.imagePath(fileDir.getDir() + uuid1 + ".PNG")
				.build();

		Image image2 = Image.builder()
				.imageOriginalName("testImage.PNG")
				.imageStoreName(uuid2 + ".PNG")
				.imagePath(fileDir.getDir() + uuid2 + ".PNG")
				.build();

		MemberDto memberDto = memberPrincipalDetails.getMemberDto();
		MemberSimpleResponse memberSimpleResponse = MemberSimpleResponse.builder()
				.id(memberDto.getId())
				.username(memberDto.getUsername())
				.nickname(memberDto.getNickname())
				.image(memberDto.getImage())
				.build();

		FeedResponse feedResponse = FeedResponse.builder()
				.id(1L)
				.title(feedPostForm.getTitle())
				.content(feedPostForm.getContent())
				.hits(0)
				.commentCount(0)
				.status(FeedStatus.NORMAL)
				.member(memberSimpleResponse)
				.feedImages(List.of(image1, image2))
				.createdAt(LocalDateTime.now())
				.modifiedAt(LocalDateTime.now())
				.modifiedBy(memberPrincipalDetails.getMemberDto().getUsername())
				.build();

		given(feedService.feed(any(FeedPostForm.class), any(List.class), any()))
				.willReturn(
						feedResponse
				);

		// when

		// then
		mockMvc.perform(
						multipart(HttpMethod.POST, "/feeds")
								.file(imageFiles.get(0))
								.file(imageFiles.get(1))
								.file(json)
								.with(user(memberPrincipalDetails))
				)
				.andDo(print())
				.andExpect(status().isOk())
				.andDo(document("feed",
						preprocessRequest(prettyPrint()),
						preprocessResponse(prettyPrint()),
						requestParts(
								partWithName("imageFiles").description("이미지 파일"),
								partWithName("feedPostForm").description("등록 내용")
						),
						requestPartFields("feedPostForm",
								fieldWithPath("title").description("피드 제목"),
								fieldWithPath("content").description("피드 내용")
						),
						responseFields(
								fieldWithPath("id").description("피드 번호"),
								fieldWithPath("title").description("피드 제목"),
								fieldWithPath("content").description("피드 내용"),
								fieldWithPath("hits").description("조회수"),
								fieldWithPath("commentCount").description("댓글 개수"),
								fieldWithPath("status").description("피드 상태"),
								fieldWithPath("createdAt").description("작성 일자"),
								fieldWithPath("modifiedAt").description("수정 일자"),
								fieldWithPath("modifiedBy").description("수정자"),
								fieldWithPath("member").description("피드 작성자"),
								fieldWithPath("member.id").description("회원 번호"),
								fieldWithPath("member.username").description("아이디"),
								fieldWithPath("member.nickname").description("닉네임"),
								fieldWithPath("member.image").description("이미지"),
								fieldWithPath("member.image.imageOriginalName")
										.description("원본 이름"),
								fieldWithPath("member.image.imageStoreName").description("저장 이름"),
								fieldWithPath("member.image.imagePath").description("경로"),
								fieldWithPath("feedImages.[].imageOriginalName")
										.description("파일 원본 이름"),
								fieldWithPath("feedImages.[].imageStoreName")
										.description("파일 저장 이름"),
								fieldWithPath("feedImages.[].imagePath").description("파일 경로")
						)
				));
	}

	@DisplayName("피드 삭제 API")
	@Test
	void deleteFeed() throws Exception {
		// given
		FeedDto feedDto = FeedDto.builder()
				.id(1L)
				.title("testTitle")
				.content("content")
				.hits(1)
				.commentCount(1)
				.status(FeedStatus.DELETE)
				.memberDto(memberPrincipalDetails.getMemberDto())
				.build();

		given(feedService.deleteFeed(any(Long.class), any()))
				.willReturn(
						feedDto
				);

		// when

		// then
		mockMvc.perform(
						RestDocumentationRequestBuilders.delete("/feeds/{feedId}", 1)
								.with(user(memberPrincipalDetails))
				)
				.andDo(print())
				.andExpect(status().isOk())
				.andDo(document("delete-feed",
						preprocessRequest(prettyPrint()),
						preprocessResponse(prettyPrint()),
						pathParameters(
								parameterWithName("feedId").description("피드 번호")
						),
						responseFields(
								fieldWithPath("id").description("피드 번호"),
								fieldWithPath("title").description("피드 제목"),
								fieldWithPath("content").description("피드 내용"),
								fieldWithPath("hits").description("조회수"),
								fieldWithPath("commentCount").description("댓글 개수"),
								fieldWithPath("status").description("피드 상태"),
								fieldWithPath("createdAt").description("작성 일자"),
								fieldWithPath("modifiedAt").description("수정 일자"),
								fieldWithPath("modifiedBy").description("수정자"),
								fieldWithPath("memberDto.id").description("회원 번호"),
								fieldWithPath("memberDto.username").description("아이디"),
								fieldWithPath("memberDto.nickname").description("닉네임"),
								fieldWithPath("memberDto.password").description("암호화 비밀번호"),
								fieldWithPath("memberDto.phone").description("연락처"),
								fieldWithPath("memberDto.birthDate").description("생년월일"),
								fieldWithPath("memberDto.status").description("상태"),
								fieldWithPath("memberDto.role").description("권한"),
								fieldWithPath("memberDto.image").description("이미지"),
								fieldWithPath("memberDto.image.imageOriginalName").description(
										"원본 이름"),
								fieldWithPath("memberDto.image.imageStoreName").description(
										"저장 이름"),
								fieldWithPath("memberDto.image.imagePath").description("경로"),
								fieldWithPath("memberDto.loginType").description("로그인 타입"),
								fieldWithPath("memberDto.createdAt").description("가입 일자"),
								fieldWithPath("memberDto.modifiedAt").description("수정 일자"),
								fieldWithPath("memberDto.modifiedBy").description("수정자")
						)
				));
	}

	@DisplayName("피드 수정 API")
	@Test
	void modifiedFeed() throws Exception {
		// given
		// Request 데이터 세팅
		FeedModifiedForm feedModifiedForm = FeedModifiedForm.builder()
				.id(1L)
				.title("testTitle")
				.content("testContent")
				.build();

		String content = objectMapper.writeValueAsString(feedModifiedForm);

		MockMultipartFile json = new MockMultipartFile(
				"feedModifiedForm", "jsondata",
				"application/json", content.getBytes(StandardCharsets.UTF_8));

		List<MockMultipartFile> imageFiles = createImageFiles("imageFiles", "testImage.PNG",
				"multipart/mixed", fileDir, 2);

		// Response 데이터 세팅
		UUID uuid1 = UUID.randomUUID();
		UUID uuid2 = UUID.randomUUID();

		Image image1 = Image.builder()
				.imageOriginalName("testImage.PNG")
				.imageStoreName(uuid1 + ".PNG")
				.imagePath(fileDir.getDir() + uuid1 + ".PNG")
				.build();

		Image image2 = Image.builder()
				.imageOriginalName("testImage.PNG")
				.imageStoreName(uuid2 + ".PNG")
				.imagePath(fileDir.getDir() + uuid2 + ".PNG")
				.build();

		MemberDto memberDto = memberPrincipalDetails.getMemberDto();
		MemberSimpleResponse memberSimpleResponse = MemberSimpleResponse.builder()
				.id(memberDto.getId())
				.username(memberDto.getUsername())
				.nickname(memberDto.getNickname())
				.image(memberDto.getImage())
				.build();

		FeedResponse feedResponse = FeedResponse.builder()
				.id(1L)
				.title(feedModifiedForm.getTitle())
				.content(feedModifiedForm.getContent())
				.hits(0)
				.commentCount(0)
				.status(FeedStatus.NORMAL)
				.member(memberSimpleResponse)
				.feedImages(List.of(image1, image2))
				.createdAt(LocalDateTime.now())
				.modifiedAt(LocalDateTime.now())
				.modifiedBy(memberPrincipalDetails.getMemberDto().getUsername())
				.build();

		given(feedService.modifiedFeed(any(FeedModifiedForm.class), any(List.class), any()))
				.willReturn(
						feedResponse
				);

		// when

		// then
		mockMvc.perform(
						multipart(HttpMethod.PUT, "/feeds")
								.file(imageFiles.get(0))
								.file(imageFiles.get(1))
								.file(json)
								.with(user(memberPrincipalDetails))
				)
				.andDo(print())
				.andExpect(status().isOk())
				.andDo(document("modified-feed",
						preprocessRequest(prettyPrint()),
						preprocessResponse(prettyPrint()),
						requestParts(
								partWithName("imageFiles").description("이미지 파일"),
								partWithName("feedModifiedForm").description("등록 내용")
						),
						requestPartFields("feedModifiedForm",
								fieldWithPath("id").description("피드 번호"),
								fieldWithPath("title").description("피드 제목"),
								fieldWithPath("content").description("피드 내용")
						),
						responseFields(
								fieldWithPath("id").description("피드 번호"),
								fieldWithPath("title").description("피드 제목"),
								fieldWithPath("content").description("피드 내용"),
								fieldWithPath("hits").description("조회수"),
								fieldWithPath("commentCount").description("댓글 개수"),
								fieldWithPath("status").description("피드 상태"),
								fieldWithPath("createdAt").description("작성 일자"),
								fieldWithPath("modifiedAt").description("수정 일자"),
								fieldWithPath("modifiedBy").description("수정자"),
								fieldWithPath("member").description("피드 작성자"),
								fieldWithPath("member.id").description("회원 번호"),
								fieldWithPath("member.username").description("아이디"),
								fieldWithPath("member.nickname").description("닉네임"),
								fieldWithPath("member.image").description("이미지"),
								fieldWithPath("member.image.imageOriginalName")
										.description("원본 이름"),
								fieldWithPath("member.image.imageStoreName").description("저장 이름"),
								fieldWithPath("member.image.imagePath").description("경로"),
								fieldWithPath("feedImages.[].imageOriginalName")
										.description("파일 원본 이름"),
								fieldWithPath("feedImages.[].imageStoreName")
										.description("파일 저장 이름"),
								fieldWithPath("feedImages.[].imagePath").description("파일 경로")
						)
				));
	}

	@DisplayName("피드 신고 API")
	@Test
	void accuseFeed() throws Exception {
		// given
		// 작성자 세팅
		Member member1 = Member.builder()
				.id(2L)
				.username("username1@naver.com")
				.nickname("nickname1")
				.password(passwordEncoder.encode("123"))
				.phone("010-1111-1111")
				.birthDate(LocalDate.now())
				.role(MemberRole.ROLE_MEMBER)
				.status(MemberStatus.NORMAL)
				.loginType(LoginType.FORM)
				.image(Image.createDefaultImage(fileDir, defaultImage.getMemberImage()))
				.createdAt(LocalDateTime.now())
				.modifiedAt(LocalDateTime.now())
				.modifiedBy("username1@naver.com")
				.build();

		// 피드 이미지 세팅
		FeedImage feedImage1 = FeedImage.builder()
				.id(1L)
				.feed(null)
				.image(Image.createDefaultImage(fileDir, defaultImage.getFeedImage()))
				.createdAt(LocalDateTime.now())
				.modifiedAt(LocalDateTime.now())
				.modifiedBy(member1.getUsername())
				.build();

		// 피드 세팅
		Feed feed1 = Feed.builder()
				.id(1L)
				.member(member1)
				.title("title1")
				.content("content1")
				.status(FeedStatus.ACCUSE)
				.hits(1)
				.commentCount(1)
				.feedImages(new ArrayList<>())
				.createdAt(LocalDateTime.now())
				.modifiedAt(LocalDateTime.now())
				.modifiedBy(member1.getUsername())
				.build();
		feed1.addFeedImage(feedImage1);

		FeedResponse feedResponse1 = FeedResponse.createFeedResponse(feed1, feed1.getFeedImages());

		FeedAccuseRequestForm feedAccuseRequestForm = FeedAccuseRequestForm.builder()
				.id(feed1.getId())
				.content("testContent")
				.build();

		given(feedService.accuseFeed(any(FeedAccuseRequestForm.class), any()))
				.willReturn(
						feedResponse1
				);
		// when

		// then
		mockMvc.perform(
						RestDocumentationRequestBuilders.put("/feeds/accuse")
								.with(user(memberPrincipalDetails))
								.content(objectMapper.writeValueAsString(feedAccuseRequestForm))
								.contentType(APPLICATION_JSON)
				)
				.andDo(print())
				.andExpect(status().isOk())
				.andDo(document("accuse-feed",
						preprocessRequest(prettyPrint()),
						preprocessResponse(prettyPrint()),
						requestFields(
								fieldWithPath("id").description("피드 번호"),
								fieldWithPath("content").description("신고 내용")
						),
						responseFields(
								fieldWithPath("id").description("피드 번호"),
								fieldWithPath("title").description("피드 제목"),
								fieldWithPath("content").description("피드 내용"),
								fieldWithPath("hits").description("조회수"),
								fieldWithPath("commentCount").description("댓글 개수"),
								fieldWithPath("status").description("피드 상태"),
								fieldWithPath("createdAt").description("작성 일자"),
								fieldWithPath("modifiedAt").description("수정 일자"),
								fieldWithPath("modifiedBy").description("수정자"),
								fieldWithPath("member").description("피드 작성자"),
								fieldWithPath("member.id").description("회원 번호"),
								fieldWithPath("member.username").description("아이디"),
								fieldWithPath("member.nickname").description("닉네임"),
								fieldWithPath("member.image").description("이미지"),
								fieldWithPath("member.image.imageOriginalName")
										.description("원본 이름"),
								fieldWithPath("member.image.imageStoreName").description("저장 이름"),
								fieldWithPath("member.image.imagePath").description("경로"),
								fieldWithPath("feedImages.[].imageOriginalName")
										.description("파일 원본 이름"),
								fieldWithPath("feedImages.[].imageStoreName")
										.description("파일 저장 이름"),
								fieldWithPath("feedImages.[].imagePath").description("파일 경로")
						)
				));
	}

	@DisplayName("나의 피드 조회 API")
	@Test
	void getMyFeeds() throws Exception {
		// given
		MemberDto memberDto = memberPrincipalDetails.getMemberDto();
		MemberSimpleResponse memberSimpleResponse = MemberSimpleResponse.builder()
				.id(memberDto.getId())
				.username(memberDto.getUsername())
				.nickname(memberDto.getNickname())
				.image(memberDto.getImage())
				.build();

		FeedResponse feedResponse1 = FeedResponse.builder()
				.id(1L)
				.title("testTitle1")
				.content("testContent1")
				.status(FeedStatus.NORMAL)
				.hits(1)
				.commentCount(1)
				.feedImages(List.of(Image.createDefaultImage(fileDir, defaultImage.getFeedImage())))
				.member(memberSimpleResponse)
				.createdAt(LocalDateTime.now())
				.modifiedAt(LocalDateTime.now())
				.modifiedBy(memberPrincipalDetails.getMemberDto().getUsername())
				.build();

		FeedResponse feedResponse2 = FeedResponse.builder()
				.id(1L)
				.title("testTitle2")
				.content("testContent2")
				.status(FeedStatus.ACCUSE)
				.hits(1)
				.commentCount(1)
				.feedImages(List.of(Image.createDefaultImage(fileDir, defaultImage.getFeedImage())))
				.member(memberSimpleResponse)
				.createdAt(LocalDateTime.now())
				.modifiedAt(LocalDateTime.now())
				.modifiedBy(memberPrincipalDetails.getMemberDto().getUsername())
				.build();

		// 응답 데이터 세팅
		List<FeedResponse> content = List.of(feedResponse1, feedResponse2);

		PageRequest pageRequest = PageRequest.of(0, 20);

		Page<FeedResponse> feedResponses = new PageImpl<>(content, pageRequest, 2);

		given(feedService.getMyFeeds(any(Long.class), any(), any(Pageable.class)))
				.willReturn(
						feedResponses
				);

		// when

		// then
		mockMvc.perform(
						RestDocumentationRequestBuilders.get("/feeds/members/{memberId}", 1)
								.with(user(memberPrincipalDetails))
								.queryParam("page", "0")
								.queryParam("size", "20")
				)
				.andDo(print())
				.andExpect(status().isOk())
				.andDo(document("get-my-feeds",
						preprocessRequest(prettyPrint()),
						preprocessResponse(prettyPrint()),
						pathParameters(
								parameterWithName("memberId").description("회원 번호")
						),
						requestParameters(
								parameterWithName("page").description("페이지 번호"),
								parameterWithName("size").description("페이지 당 데이터 개수")
						),
						responseFields(
								fieldWithPath("content.[].id").description("피드 번호"),
								fieldWithPath("content.[].title").description("피드 제목"),
								fieldWithPath("content.[].content").description("피드 내용"),
								fieldWithPath("content.[].hits").description("조회수"),
								fieldWithPath("content.[].commentCount").description("댓글 개수"),
								fieldWithPath("content.[].status").description("피드 상태"),
								fieldWithPath("content.[].createdAt").description("작성 일자"),
								fieldWithPath("content.[].modifiedAt").description("수정 일자"),
								fieldWithPath("content.[].modifiedBy").description("수정자"),
								fieldWithPath("content.[].member").description("피드 작성자"),
								fieldWithPath("content.[].member.id").description("회원 번호"),
								fieldWithPath("content.[].member.username").description("아이디"),
								fieldWithPath("content.[].member.nickname").description("닉네임"),
								fieldWithPath("content.[].member.image").description("이미지"),
								fieldWithPath("content.[].member.image.imageOriginalName")
										.description("원본 이름"),
								fieldWithPath("content.[].member.image.imageStoreName")
										.description("저장 이름"),
								fieldWithPath("content.[].member.image.imagePath")
										.description("경로"),
								fieldWithPath("content.[].feedImages.[].imageOriginalName")
										.description("파일 원본 이름"),
								fieldWithPath("content.[].feedImages.[].imageStoreName")
										.description("파일 저장 이름"),
								fieldWithPath("content.[].feedImages.[].imagePath")
										.description("파일 경로"),

								fieldWithPath("pageable.sort.sorted").description("정렬"),
								fieldWithPath("pageable.sort.empty").description("데이터 비어있는지 여부"),
								fieldWithPath("pageable.sort.unsorted").description("비정렬"),

								fieldWithPath("pageable.offset").description("첫 데이터 인덱스"),
								fieldWithPath("pageable.pageNumber").description("현재 페이지 번호"),
								fieldWithPath("pageable.pageSize").description("페이지 당 데이터 개수"),
								fieldWithPath("pageable.paged").description("페이징 정보 포함"),
								fieldWithPath("pageable.unpaged").description("페이징 정보 비포함"),

								fieldWithPath("last").description("마지막 페이지 여부"),
								fieldWithPath("totalPages").description("전체 페이지 개수"),
								fieldWithPath("totalElements").description("전체 데이터 개수"),
								fieldWithPath("first").description("첫번째 페이지 여부"),
								fieldWithPath("numberOfElements")
										.description("현재 페이지에서 조회된 데이터 개수"),
								fieldWithPath("number").description("현재 페이지 번호"),
								fieldWithPath("size").description("페이지 당 데이터 개수"),

								fieldWithPath("sort.sorted").description("정렬"),
								fieldWithPath("sort.unsorted").description("비정렬"),
								fieldWithPath("sort.empty").description("데이터 비어있는지 여부"),

								fieldWithPath("empty").description("데이터 비어있는지 여부")
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

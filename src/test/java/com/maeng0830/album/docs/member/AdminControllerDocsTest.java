package com.maeng0830.album.docs.member;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.springframework.restdocs.mockmvc.MockMvcRestDocumentation.document;
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
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.maeng0830.album.comment.domain.CommentStatus;
import com.maeng0830.album.comment.dto.CommentAccuseDto;
import com.maeng0830.album.comment.dto.CommentDto;
import com.maeng0830.album.comment.dto.response.BasicComment;
import com.maeng0830.album.common.model.image.Image;
import com.maeng0830.album.feed.domain.FeedStatus;
import com.maeng0830.album.feed.dto.FeedAccuseDto;
import com.maeng0830.album.feed.dto.FeedDto;
import com.maeng0830.album.feed.dto.FeedResponse;
import com.maeng0830.album.member.domain.MemberRole;
import com.maeng0830.album.member.domain.MemberStatus;
import com.maeng0830.album.member.dto.MemberDto;
import com.maeng0830.album.security.dto.LoginType;
import com.maeng0830.album.support.DocsTestSupport;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.domain.Sort.Order;
import org.springframework.http.MediaType;
import org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders;

public class AdminControllerDocsTest extends DocsTestSupport {

	@DisplayName("전체 피드 목록 조회 API")
	@Test
	void getFeedsForAdmin() throws Exception {
		// given
		MemberDto writer1 = MemberDto.builder()
				.id(3L)
				.username("writer1@naver.com")
				.nickname("writer1")
				.password(passwordEncoder.encode("123"))
				.phone("010-2222-2222")
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
				.id(4L)
				.username("writer2@naver.com")
				.nickname("writer2")
				.password(passwordEncoder.encode("123"))
				.phone("010-3333-3333")
				.birthDate(LocalDate.now())
				.status(MemberStatus.NORMAL)
				.role(MemberRole.ROLE_MEMBER)
				.loginType(LoginType.FORM)
				.image(Image.createDefaultImage(fileDir, defaultImage.getMemberImage()))
				.createdAt(LocalDateTime.now())
				.modifiedAt(LocalDateTime.now())
				.modifiedBy("writer2@naver.com")
				.build();

		MemberDto writer3 = MemberDto.builder()
				.id(5L)
				.username("writer3@naver.com")
				.nickname("writer3")
				.password(passwordEncoder.encode("123"))
				.phone("010-4444-4444")
				.birthDate(LocalDate.now())
				.status(MemberStatus.NORMAL)
				.role(MemberRole.ROLE_MEMBER)
				.loginType(LoginType.FORM)
				.image(Image.createDefaultImage(fileDir, defaultImage.getMemberImage()))
				.createdAt(LocalDateTime.now())
				.modifiedAt(LocalDateTime.now())
				.modifiedBy("writer3@naver.com")
				.build();

		FeedResponse feedResponse1 = FeedResponse.builder()
				.id(1L)
				.title("testTitle")
				.content("testContent")
				.hits(1)
				.commentCount(1)
				.status(FeedStatus.NORMAL)
				.member(writer1)
				.feedImages(List.of(Image.createDefaultImage(fileDir, defaultImage.getFeedImage())))
				.createdAt(LocalDateTime.now())
				.modifiedAt(LocalDateTime.now())
				.modifiedBy(writer1.getUsername())
				.build();

		FeedResponse feedResponse2 = FeedResponse.builder()
				.id(2L)
				.title("testTitle")
				.content("testContent")
				.hits(1)
				.commentCount(1)
				.status(FeedStatus.ACCUSE)
				.member(writer2)
				.feedImages(List.of(Image.createDefaultImage(fileDir, defaultImage.getFeedImage())))
				.createdAt(LocalDateTime.now())
				.modifiedAt(LocalDateTime.now())
				.modifiedBy(writer2.getUsername())
				.build();

		FeedResponse feedResponse3 = FeedResponse.builder()
				.id(3L)
				.title("testTitle")
				.content("testContent")
				.hits(1)
				.commentCount(1)
				.status(FeedStatus.DELETE)
				.member(writer3)
				.feedImages(List.of(Image.createDefaultImage(fileDir, defaultImage.getFeedImage())))
				.createdAt(LocalDateTime.now())
				.modifiedAt(LocalDateTime.now())
				.modifiedBy(writer3.getUsername())
				.build();

		List<FeedResponse> feedResponses = List.of(feedResponse2, feedResponse1, feedResponse3);

		PageRequest pageRequest = PageRequest.of(0, 20,
				Sort.by(Order.asc("status"), Order.desc("createdAt")));

		Page<FeedResponse> feedResponsePage = new PageImpl<>(feedResponses, pageRequest, 3);

		given(feedService.getFeedsForAdmin(any(), any(String.class), any(Pageable.class)))
				.willReturn(
						feedResponsePage
				);
		// when

		// then
		mockMvc.perform(
						get("/admin/feeds")
								.with(user(adminPrincipalDetails))
								.queryParam("searchText", "writer")
								.queryParam("page", "0")
								.queryParam("size", "20")
				)
				.andDo(print())
				.andExpect(status().isOk())
				.andDo(document("get-feeds-for-admin",
						preprocessRequest(prettyPrint()),
						preprocessResponse(prettyPrint()),
						requestParameters(
								parameterWithName("searchText").description("아이디 또는 닉네임(전방 일치)"),
								parameterWithName("page").description("페이지 번호"),
								parameterWithName("size").description("페이지 당 데이터 개수")
						),
						responseFields(
								// content
								fieldWithPath("content.[].id").description("피드 번호"),
								fieldWithPath("content.[].title").description("피드 제목"),
								fieldWithPath("content.[].content").description("피드 내용"),
								fieldWithPath("content.[].hits").description("조회수"),
								fieldWithPath("content.[].commentCount").description("댓글 개수"),
								fieldWithPath("content.[].status").description("피드 상태"),
								fieldWithPath("content.[].createdAt").description("작성 일자"),
								fieldWithPath("content.[].modifiedAt").description("수정 일자"),
								fieldWithPath("content.[].modifiedBy").description("수정자"),
								//// content.member
								fieldWithPath("content.[].member.id").description("회원 번호"),
								fieldWithPath("content.[].member.username").description("아이디"),
								fieldWithPath("content.[].member.nickname").description("닉네임"),
								fieldWithPath("content.[].member.password").description("암호화 비밀번호"),
								fieldWithPath("content.[].member.phone").description("연락처"),
								fieldWithPath("content.[].member.birthDate").description("생년월일"),
								fieldWithPath("content.[].member.status").description("상태"),
								fieldWithPath("content.[].member.role").description("권한"),
								fieldWithPath("content.[].member.image").description("이미지"),
								fieldWithPath(
										"content.[].member.image.imageOriginalName").description(
										"원본 이름"),
								fieldWithPath("content.[].member.image.imageStoreName").description(
										"저장 이름"),
								fieldWithPath("content.[].member.image.imagePath").description(
										"경로"),
								fieldWithPath("content.[].member.loginType").description("로그인 타입"),
								fieldWithPath("content.[].member.createdAt").description("가입 일자"),
								fieldWithPath("content.[].member.modifiedAt").description("수정 일자"),
								fieldWithPath("content.[].member.modifiedBy").description("수정자"),
								//// content.feedImages
								fieldWithPath(
										"content.[].feedImages.[].imageOriginalName").description(
										"파일 원본 이름"),
								fieldWithPath(
										"content.[].feedImages.[].imageStoreName").description(
										"파일 저장 이름"),
								fieldWithPath("content.[].feedImages.[].imagePath").description(
										"파일 경로"),
								// pageable
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

	@DisplayName("특정 피드 신고 내역 조회 API")
	@Test
	void getFeedAccuses() throws Exception {
		// given
		MemberDto feedWriter = MemberDto.builder()
				.id(3L)
				.username("feedWriter@naver.com")
				.nickname("feedWriter")
				.password(passwordEncoder.encode("123"))
				.phone("010-3333-3333")
				.birthDate(LocalDate.now())
				.status(MemberStatus.NORMAL)
				.role(MemberRole.ROLE_MEMBER)
				.loginType(LoginType.FORM)
				.image(Image.createDefaultImage(fileDir, defaultImage.getMemberImage()))
				.createdAt(LocalDateTime.now())
				.modifiedAt(LocalDateTime.now())
				.modifiedBy("feedWriter@naver.com")
				.build();

		MemberDto feedAccuseWriter = MemberDto.builder()
				.id(4L)
				.username("feedAccuseWriter@naver.com")
				.nickname("feedAccuseWriter")
				.password(passwordEncoder.encode("123"))
				.phone("010-4444-4444")
				.birthDate(LocalDate.now())
				.status(MemberStatus.NORMAL)
				.role(MemberRole.ROLE_MEMBER)
				.loginType(LoginType.FORM)
				.image(Image.createDefaultImage(fileDir, defaultImage.getMemberImage()))
				.createdAt(LocalDateTime.now())
				.modifiedAt(LocalDateTime.now())
				.modifiedBy("feedAccuseWriter@naver.com")
				.build();

		FeedDto feedDto = FeedDto.builder()
				.id(1L)
				.title("testTitle")
				.content("content")
				.hits(1)
				.commentCount(1)
				.status(FeedStatus.ACCUSE)
				.memberDto(feedWriter)
				.createdAt(LocalDateTime.now())
				.modifiedAt(LocalDateTime.now())
				.modifiedBy(feedWriter.getUsername())
				.build();

		FeedAccuseDto feedAccuseDto = FeedAccuseDto.builder()
				.id(1L)
				.content("testContent")
				.feedDto(feedDto)
				.memberDto(feedAccuseWriter)
				.createdAt(LocalDateTime.now())
				.modifiedAt(LocalDateTime.now())
				.modifiedBy(feedAccuseWriter.getUsername())
				.build();

		List<FeedAccuseDto> feedAccuseDtos = List.of(feedAccuseDto);

		given(feedService.getFeedAccuses(any(), any(Long.class)))
				.willReturn(
						feedAccuseDtos
				);
		// when

		// then
		mockMvc.perform(
						RestDocumentationRequestBuilders.get("/admin/feeds/{feedId}/accuses", 1)
								.with(user(adminPrincipalDetails))
				)
				.andDo(print())
				.andExpect(status().isOk())
				.andDo(document("get-feeds-accuses-for-admin",
						preprocessRequest(prettyPrint()),
						preprocessResponse(prettyPrint()),
						pathParameters(
								parameterWithName("feedId").description("피드 번호")
						),
						responseFields(
								fieldWithPath("[].id").description("피드 신고 번호"),
								fieldWithPath("[].content").description("신고 내용"),
								fieldWithPath("[].createdAt").description("신고 일자"),
								fieldWithPath("[].modifiedAt").description("수정 일자"),
								fieldWithPath("[].modifiedBy").description("수정자"),
								// MemberDto
								fieldWithPath("[].memberDto").description("신고자"),
								fieldWithPath("[].memberDto.id").description("회원 번호"),
								fieldWithPath("[].memberDto.username").description("아이디"),
								fieldWithPath("[].memberDto.nickname").description("닉네임"),
								fieldWithPath("[].memberDto.password").description("암호화 비밀번호"),
								fieldWithPath("[].memberDto.phone").description("연락처"),
								fieldWithPath("[].memberDto.birthDate").description("생년월일"),
								fieldWithPath("[].memberDto.status").description("상태"),
								fieldWithPath("[].memberDto.role").description("권한"),
								fieldWithPath("[].memberDto.image").description("이미지"),
								fieldWithPath("[].memberDto.image.imageOriginalName").description(
										"원본 이름"),
								fieldWithPath("[].memberDto.image.imageStoreName").description(
										"저장 이름"),
								fieldWithPath("[].memberDto.image.imagePath").description("경로"),
								fieldWithPath("[].memberDto.loginType").description("로그인 타입"),
								fieldWithPath("[].memberDto.createdAt").description("가입 일자"),
								fieldWithPath("[].memberDto.modifiedAt").description("수정 일자"),
								fieldWithPath("[].memberDto.modifiedBy").description("수정자"),

								// FeedDto
								fieldWithPath("[].feedDto.id").description("피드 번호"),
								fieldWithPath("[].feedDto.title").description("피드 제목"),
								fieldWithPath("[].feedDto.content").description("피드 내용"),
								fieldWithPath("[].feedDto.hits").description("조회수"),
								fieldWithPath("[].feedDto.commentCount").description("댓글 개수"),
								fieldWithPath("[].feedDto.status").description("피드 상태"),
								fieldWithPath("[].feedDto.createdAt").description("작성 일자"),
								fieldWithPath("[].feedDto.modifiedAt").description("수정 일자"),
								fieldWithPath("[].feedDto.modifiedBy").description("수정자"),
								fieldWithPath("[].feedDto.memberDto").description("피드 작성자"),
								fieldWithPath("[].feedDto.memberDto.id").description("회원 번호"),
								fieldWithPath("[].feedDto.memberDto.username").description("아이디"),
								fieldWithPath("[].feedDto.memberDto.nickname").description("닉네임"),
								fieldWithPath("[].feedDto.memberDto.password").description(
										"암호화 비밀번호"),
								fieldWithPath("[].feedDto.memberDto.phone").description("연락처"),
								fieldWithPath("[].feedDto.memberDto.birthDate").description("생년월일"),
								fieldWithPath("[].feedDto.memberDto.status").description("상태"),
								fieldWithPath("[].feedDto.memberDto.role").description("권한"),
								fieldWithPath("[].feedDto.memberDto.image").description("이미지"),
								fieldWithPath(
										"[].feedDto.memberDto.image.imageOriginalName").description(
										"원본 이름"),
								fieldWithPath(
										"[].feedDto.memberDto.image.imageStoreName").description(
										"저장 이름"),
								fieldWithPath("[].feedDto.memberDto.image.imagePath").description(
										"경로"),
								fieldWithPath("[].feedDto.memberDto.loginType").description(
										"로그인 타입"),
								fieldWithPath("[].feedDto.memberDto.createdAt").description(
										"가입 일자"),
								fieldWithPath("[].feedDto.memberDto.modifiedAt").description(
										"수정 일자"),
								fieldWithPath("[].feedDto.memberDto.modifiedBy").description("수정자")
						)
				));
	}

	@DisplayName("피드 상태 변경 API")
	@Test
	void changeFeedStatus() throws Exception {
		// given
		Map<String, String> map = new HashMap<>();
		map.put("feedStatus", "DELETE");

		MemberDto feedWriter = MemberDto.builder()
				.id(3L)
				.username("feedWriter@naver.com")
				.nickname("feedWriter")
				.password(passwordEncoder.encode("123"))
				.phone("010-3333-3333")
				.birthDate(LocalDate.now())
				.status(MemberStatus.NORMAL)
				.role(MemberRole.ROLE_MEMBER)
				.loginType(LoginType.FORM)
				.image(Image.createDefaultImage(fileDir, defaultImage.getMemberImage()))
				.createdAt(LocalDateTime.now())
				.modifiedAt(LocalDateTime.now())
				.modifiedBy("feedWriter@naver.com")
				.build();

		FeedDto feedDto = FeedDto.builder()
				.id(1L)
				.title("testTitle")
				.content("content")
				.hits(1)
				.commentCount(1)
				.status(FeedStatus.DELETE)
				.memberDto(feedWriter)
				.createdAt(LocalDateTime.now())
				.modifiedAt(LocalDateTime.now())
				.modifiedBy(feedWriter.getUsername())
				.build();

		given(feedService.changeFeedStatus(any(Long.class), any(FeedStatus.class)))
				.willReturn(
						feedDto
				);
		// when

		// then
		mockMvc.perform(
						RestDocumentationRequestBuilders.put("/admin/feeds/{feedId}/status", 1)
								.with(user(adminPrincipalDetails))
								.content(objectMapper.writeValueAsString(map))
								.contentType(MediaType.APPLICATION_JSON)
				)
				.andDo(print())
				.andExpect(status().isOk())
				.andDo(document("change-feed-status-for-admin",
						preprocessRequest(prettyPrint()),
						preprocessResponse(prettyPrint()),
						pathParameters(
								parameterWithName("feedId").description("피드 번호")
						),
						requestFields(
								fieldWithPath("feedStatus").description("변경할 상태")
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
								fieldWithPath("memberDto").description("피드 작성자"),
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

	@DisplayName("전체 회원 목록 조회 API")
	@Test
	void getMembersForAdmin() throws Exception {
		// given
		List<MemberDto> memberDtos = new ArrayList<>();

		for (int i = 0; i < 4; i++) {
			MemberStatus memberStatus = null;

			if (i == 0) {
				memberStatus = MemberStatus.NORMAL;
			} else if (i == 1) {
				memberStatus = MemberStatus.LOCKED;
			} else if (i == 2) {
				memberStatus = MemberStatus.WITHDRAW;
			} else {
				memberStatus = MemberStatus.FIRST;
			}

			MemberDto memberDto = MemberDto.builder()
					.id((long) (3 + i))
					.username("member" + i + "@naver.com")
					.nickname("member" + i)
					.password(passwordEncoder.encode("***"))
					.phone("010-****-****")
					.birthDate(LocalDate.now())
					.status(memberStatus)
					.role(MemberRole.ROLE_MEMBER)
					.loginType(LoginType.FORM)
					.image(Image.createDefaultImage(fileDir, defaultImage.getMemberImage()))
					.createdAt(LocalDateTime.now())
					.modifiedAt(LocalDateTime.now())
					.modifiedBy("member" + i + "@naver.com")
					.build();

			memberDtos.add(memberDto);
		}

		PageRequest pageRequest = PageRequest.of(0, 20,
				Sort.by(Order.asc("status"), Order.desc("createdAt")));

		Page<MemberDto> memberDtoPage = new PageImpl<>(memberDtos, pageRequest, 4);

		given(memberService.getMembersForAdmin(any(), any(String.class), any(Pageable.class)))
				.willReturn(
						memberDtoPage
				);
		// when

		// then
		mockMvc.perform(
						get("/admin/members")
								.with(user(adminPrincipalDetails))
								.queryParam("searchText", "member")
								.queryParam("page", "0")
								.queryParam("size", "20")
				)
				.andDo(print())
				.andExpect(status().isOk())
				.andDo(document("get-members-for-admin",
						preprocessRequest(prettyPrint()),
						preprocessResponse(prettyPrint()),
						requestParameters(
								parameterWithName("searchText").description("아이디 또는 닉네임(전방 일치)"),
								parameterWithName("page").description("페이지 번호"),
								parameterWithName("size").description("페이지 당 데이터 개수")
						),
						responseFields(
								fieldWithPath("content.[].id").description("회원 번호"),
								fieldWithPath("content.[].username").description("아이디"),
								fieldWithPath("content.[].nickname").description("닉네임"),
								fieldWithPath("content.[].password").description("암호화 비밀번호"),
								fieldWithPath("content.[].phone").description("연락처"),
								fieldWithPath("content.[].birthDate").description("생년월일"),
								fieldWithPath("content.[].status").description("상태"),
								fieldWithPath("content.[].role").description("권한"),
								fieldWithPath("content.[].image").description("이미지"),
								fieldWithPath("content.[].image.imageOriginalName").description(
										"원본 이름"),
								fieldWithPath("content.[].image.imageStoreName").description(
										"저장 이름"),
								fieldWithPath("content.[].image.imagePath").description("경로"),
								fieldWithPath("content.[].loginType").description("로그인 타입"),
								fieldWithPath("content.[].createdAt").description("가입 일자"),
								fieldWithPath("content.[].modifiedAt").description("수정 일자"),
								fieldWithPath("content.[].modifiedBy").description("수정자"),
								// pageable
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

	@DisplayName("특정 회원 상태 변경 API")
	@Test
	void changeMemberStatus() throws Exception {
		// given
		Map<String, String> map = new HashMap<>();
		map.put("memberStatus", "LOCKED");

		MemberDto memberDto = MemberDto.builder()
				.id(3L)
				.username("member@naver.com")
				.nickname("member")
				.password(passwordEncoder.encode("***"))
				.phone("010-****-****")
				.birthDate(LocalDate.now())
				.status(MemberStatus.LOCKED)
				.role(MemberRole.ROLE_MEMBER)
				.loginType(LoginType.FORM)
				.image(Image.createDefaultImage(fileDir, defaultImage.getMemberImage()))
				.createdAt(LocalDateTime.now())
				.modifiedAt(LocalDateTime.now())
				.modifiedBy("member@naver.com")
				.build();

		given(memberService.changeMemberStatus(any(Long.class), any(MemberStatus.class)))
				.willReturn(
						memberDto
				);
		// when

		// then
		mockMvc.perform(
						RestDocumentationRequestBuilders.put("/admin/members/{memberId}/status", 3)
								.with(user(adminPrincipalDetails))
								.content(objectMapper.writeValueAsString(map))
								.contentType(MediaType.APPLICATION_JSON)
				)
				.andDo(print())
				.andExpect(status().isOk())
				.andDo(document("change-member-status-for-admin",
						preprocessRequest(prettyPrint()),
						preprocessResponse(prettyPrint()),
						pathParameters(
								parameterWithName("memberId").description("회원 번호")
						),
						requestFields(
								fieldWithPath("memberStatus").description("변경할 상태")
						),
						responseFields(
								fieldWithPath("id").description("회원 번호"),
								fieldWithPath("username").description("아이디"),
								fieldWithPath("nickname").description("닉네임"),
								fieldWithPath("password").description("암호화 비밀번호"),
								fieldWithPath("phone").description("연락처"),
								fieldWithPath("birthDate").description("생년월일"),
								fieldWithPath("status").description("상태"),
								fieldWithPath("role").description("권한"),
								fieldWithPath("image").description("이미지"),
								fieldWithPath("image.imageOriginalName").description("원본 이름"),
								fieldWithPath("image.imageStoreName").description("저장 이름"),
								fieldWithPath("image.imagePath").description("경로"),
								fieldWithPath("loginType").description("로그인 타입"),
								fieldWithPath("createdAt").description("가입 일자"),
								fieldWithPath("modifiedAt").description("수정 일자"),
								fieldWithPath("modifiedBy").description("수정자")
						)
				));
	}

	@DisplayName("전체 댓글 조회 API")
	@Test
	void getCommentsForAdmin() throws Exception {
		// given
		MemberDto writer1 = MemberDto.builder()
				.id(3L)
				.username("writer1@naver.com")
				.nickname("writer1")
				.password(passwordEncoder.encode("123"))
				.phone("010-2222-2222")
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
				.id(4L)
				.username("writer2@naver.com")
				.nickname("writer2")
				.password(passwordEncoder.encode("123"))
				.phone("010-3333-3333")
				.birthDate(LocalDate.now())
				.status(MemberStatus.NORMAL)
				.role(MemberRole.ROLE_MEMBER)
				.loginType(LoginType.FORM)
				.image(Image.createDefaultImage(fileDir, defaultImage.getMemberImage()))
				.createdAt(LocalDateTime.now())
				.modifiedAt(LocalDateTime.now())
				.modifiedBy("writer2@naver.com")
				.build();

		MemberDto writer3 = MemberDto.builder()
				.id(5L)
				.username("writer3@naver.com")
				.nickname("writer3")
				.password(passwordEncoder.encode("123"))
				.phone("010-4444-4444")
				.birthDate(LocalDate.now())
				.status(MemberStatus.NORMAL)
				.role(MemberRole.ROLE_MEMBER)
				.loginType(LoginType.FORM)
				.image(Image.createDefaultImage(fileDir, defaultImage.getMemberImage()))
				.createdAt(LocalDateTime.now())
				.modifiedAt(LocalDateTime.now())
				.modifiedBy("writer3@naver.com")
				.build();

		BasicComment basicComment1 = BasicComment.builder()
				.id(1L)
				.feedId(1L)
				.groupId(1L)
				.parentId(1L)
				.parentMember(writer1.getNickname())
				.member(writer1)
				.content("testContent")
				.status(CommentStatus.ACCUSE)
				.createdAt(LocalDateTime.now())
				.modifiedAt(LocalDateTime.now())
				.modifiedBy(writer1.getUsername())
				.build();
		BasicComment basicComment2 = BasicComment.builder()
				.id(2L)
				.feedId(1L)
				.groupId(2L)
				.parentId(2L)
				.parentMember(writer2.getNickname())
				.member(writer2)
				.content("testContent")
				.status(CommentStatus.NORMAL)
				.createdAt(LocalDateTime.now())
				.modifiedAt(LocalDateTime.now())
				.modifiedBy(writer2.getUsername())
				.build();
		BasicComment basicComment3 = BasicComment.builder()
				.id(3L)
				.feedId(1L)
				.groupId(3L)
				.parentId(3L)
				.parentMember(writer3.getNickname())
				.member(writer3)
				.content("testContent")
				.status(CommentStatus.DELETE)
				.createdAt(LocalDateTime.now())
				.modifiedAt(LocalDateTime.now())
				.modifiedBy(writer3.getUsername())
				.build();

		List<BasicComment> basicComments = List.of(basicComment1, basicComment2, basicComment3);

		PageRequest pageRequest = PageRequest.of(0, 20,
				Sort.by(Order.asc("status"), Order.desc("createdAt")));

		Page<BasicComment> basicCommentPage = new PageImpl<>(basicComments, pageRequest, 3);

		given(commentService.getCommentsForAdmin(any(), any(String.class), any(Pageable.class)))
				.willReturn(
						basicCommentPage
				);
		// when

		// then
		mockMvc.perform(
						get("/admin/comments")
								.with(user(adminPrincipalDetails))
								.queryParam("searchText", "writer")
								.queryParam("page", "0")
								.queryParam("size", "20")
				)
				.andDo(print())
				.andExpect(status().isOk())
				.andDo(document("get-comments-for-admin",
						preprocessRequest(prettyPrint()),
						preprocessResponse(prettyPrint()),
						requestParameters(
								parameterWithName("searchText").description("아이디 또는 닉네임(전방 일치)"),
								parameterWithName("page").description("페이지 번호"),
								parameterWithName("size").description("페이지 당 데이터 개수")
						),
						responseFields(
								// content
								fieldWithPath("content.[].id").description("댓글 번호"),
								fieldWithPath("content.[].feedId").description("댓글 관련 피드 번호"),
								fieldWithPath("content.[].groupId").description("그룹 댓글 번호"),
								fieldWithPath("content.[].parentId").description("부모 댓글 번호"),
								fieldWithPath("content.[].parentMember").description("부모 댓글 작성자"),
								fieldWithPath("content.[].content").description("댓글 내용"),
								fieldWithPath("content.[].status").description("댓글 상태"),
								fieldWithPath("content.[].createdAt").description("가입 일자"),
								fieldWithPath("content.[].modifiedAt").description("수정 일자"),
								fieldWithPath("content.[].modifiedBy").description("수정자"),
								//// content.member
								fieldWithPath("content.[].member").description("댓글 작성자"),
								fieldWithPath("content.[].member.id").description("회원 번호"),
								fieldWithPath("content.[].member.username").description("아이디"),
								fieldWithPath("content.[].member.nickname").description("닉네임"),
								fieldWithPath("content.[].member.password").description("암호화 비밀번호"),
								fieldWithPath("content.[].member.phone").description("연락처"),
								fieldWithPath("content.[].member.birthDate").description("생년월일"),
								fieldWithPath("content.[].member.status").description("상태"),
								fieldWithPath("content.[].member.role").description("권한"),
								fieldWithPath("content.[].member.image").description("이미지"),
								fieldWithPath(
										"content.[].member.image.imageOriginalName").description(
										"원본 이름"),
								fieldWithPath("content.[].member.image.imageStoreName").description(
										"저장 이름"),
								fieldWithPath("content.[].member.image.imagePath").description(
										"경로"),
								fieldWithPath("content.[].member.loginType").description("로그인 타입"),
								fieldWithPath("content.[].member.createdAt").description("가입 일자"),
								fieldWithPath("content.[].member.modifiedAt").description("수정 일자"),
								fieldWithPath("content.[].member.modifiedBy").description("수정자"),
								// pageable
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

	@DisplayName("특정 댓글 신고 내역 조회 API")
	@Test
	void getCommentAccuses() throws Exception {
		// given
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

		MemberDto commentAccuser = MemberDto.builder()
				.id(5L)
				.username("commentAccuser@naver.com")
				.nickname("commentAccuser")
				.password(passwordEncoder.encode("1234"))
				.phone("010-3333-3333")
				.birthDate(LocalDate.now())
				.status(MemberStatus.NORMAL)
				.role(MemberRole.ROLE_MEMBER)
				.loginType(LoginType.FORM)
				.image(Image.createDefaultImage(fileDir, defaultImage.getMemberImage()))
				.createdAt(LocalDateTime.now())
				.modifiedAt(LocalDateTime.now())
				.modifiedBy("commentAccuser@naver.com")
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
				.member(commentAccuser)
				.content("testContent")
				.createdAt(LocalDateTime.now())
				.modifiedAt(LocalDateTime.now())
				.modifiedBy(commentAccuser.getUsername())
				.build();

		List<CommentAccuseDto> commentAccuseDtos = List.of(commentAccuseDto);

		given(commentService.getCommentAccuses(any(), any(Long.class)))
				.willReturn(
						commentAccuseDtos
				);
		// when

		// then
		mockMvc.perform(
						RestDocumentationRequestBuilders.get("/admin/comments/{commentId}/accuses", 1)
								.with(user(adminPrincipalDetails))
				)
				.andDo(print())
				.andExpect(status().isOk())
				.andDo(document("get-comment-accuses-for-admin",
						preprocessRequest(prettyPrint()),
						preprocessResponse(prettyPrint()),
						pathParameters(
								parameterWithName("commentId").description("댓글 번호")
						),
						responseFields(
								fieldWithPath("[].id").description("댓글 신고 번호"),
								fieldWithPath("[].content").description("신고 내용"),
								fieldWithPath("[].createdAt").description("생성 일자"),
								fieldWithPath("[].modifiedAt").description("수정 일자"),
								fieldWithPath("[].modifiedBy").description("수정자"),
								// member
								fieldWithPath("[].member").description("댓글 신고자"),
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
								// comment
								fieldWithPath("[].comment").description("신고 댓글"),
								fieldWithPath("[].comment.id").description("댓글 번호"),
								fieldWithPath("[].comment.groupId").description("그룹 댓글 번호"),
								fieldWithPath("[].comment.parentId").description("부모 댓글 번호"),
								fieldWithPath("[].comment.content").description("댓글 내용"),
								fieldWithPath("[].comment.status").description("상태"),
								fieldWithPath("[].comment.createdAt").description("생성 일자"),
								fieldWithPath("[].comment.modifiedAt").description("수정 일자"),
								fieldWithPath("[].comment.modifiedBy").description("수정자"),
								//// comment.member
								fieldWithPath("[].comment.member").description("댓글 작성자"),
								fieldWithPath("[].comment.member.createdAt").description("생성 일자"),
								fieldWithPath("[].comment.member.modifiedAt").description("수정 일자"),
								fieldWithPath("[].comment.member.modifiedBy").description("수정자"),
								fieldWithPath("[].comment.member.id").description("회원 번호"),
								fieldWithPath("[].comment.member.username").description("아이디"),
								fieldWithPath("[].comment.member.nickname").description("닉네임"),
								fieldWithPath("[].comment.member.password").description("암호화 비밀번호"),
								fieldWithPath("[].comment.member.phone").description("연락처"),
								fieldWithPath("[].comment.member.birthDate").description("생년월일"),
								fieldWithPath("[].comment.member.status").description("상태"),
								fieldWithPath("[].comment.member.role").description("권한"),
								fieldWithPath("[].comment.member.image").description("회원 이미지"),
								fieldWithPath(
										"[].comment.member.image.imageOriginalName").description(
										"원본 파일 이름"),
								fieldWithPath("[].comment.member.image.imageStoreName").description(
										"저장 파일 이름"),
								fieldWithPath("[].comment.member.image.imagePath").description(
										"파일 경로"),
								fieldWithPath("[].comment.member.loginType").description("로그인 타입"),
								//// comment.feed
								fieldWithPath("[].comment.feed").description("댓글 관련 피드"),
								fieldWithPath("[].comment.feed.createdAt").description("생성 일자"),
								fieldWithPath("[].comment.feed.modifiedAt").description("수정 일자"),
								fieldWithPath("[].comment.feed.modifiedBy").description("수정자"),
								fieldWithPath("[].comment.feed.id").description("피드 번호"),
								fieldWithPath("[].comment.feed.title").description("피드 제목"),
								fieldWithPath("[].comment.feed.content").description("피드 내용"),
								fieldWithPath("[].comment.feed.hits").description("조회수"),
								fieldWithPath("[].comment.feed.commentCount").description("댓글 개수"),
								fieldWithPath("[].comment.feed.status").description("상태"),
								////// comment.feed.memberDto
								fieldWithPath("[].comment.feed.memberDto").description("피드 작성자"),
								fieldWithPath("[].comment.feed.memberDto.createdAt").description(
										"생성 일자"),
								fieldWithPath("[].comment.feed.memberDto.modifiedAt").description(
										"수정 일자"),
								fieldWithPath("[].comment.feed.memberDto.modifiedBy").description(
										"수정자"),
								fieldWithPath("[].comment.feed.memberDto.id").description("회원 번호"),
								fieldWithPath("[].comment.feed.memberDto.username").description(
										"아이디"),
								fieldWithPath("[].comment.feed.memberDto.nickname").description(
										"닉네임"),
								fieldWithPath("[].comment.feed.memberDto.password").description(
										"암호화 비밀번호"),
								fieldWithPath("[].comment.feed.memberDto.phone").description("연락처"),
								fieldWithPath("[].comment.feed.memberDto.birthDate").description(
										"생년월일"),
								fieldWithPath("[].comment.feed.memberDto.status").description("상태"),
								fieldWithPath("[].comment.feed.memberDto.role").description("권한"),
								fieldWithPath("[].comment.feed.memberDto.image").description(
										"회원 이미지"),
								fieldWithPath(
										"[].comment.feed.memberDto.image.imageOriginalName").description(
										"원본 파일 이름"),
								fieldWithPath(
										"[].comment.feed.memberDto.image.imageStoreName").description(
										"저장 파일 이름"),
								fieldWithPath(
										"[].comment.feed.memberDto.image.imagePath").description(
										"파일 경로"),
								fieldWithPath("[].comment.feed.memberDto.loginType").description(
										"로그인 타입")
						)
				));
	}

	@DisplayName("특정 댓글 상태 변경 API")
	@Test
	void changeCommentStatus() throws Exception {
		// given
		Map<String, String> map = new HashMap<>();
		map.put("commentStatus", "DELETE");

		MemberDto writer = MemberDto.builder()
				.id(3L)
				.username("writer@naver.com")
				.nickname("writer")
				.password(passwordEncoder.encode("123"))
				.phone("010-2222-2222")
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
				.status(CommentStatus.DELETE)
				.createdAt(LocalDateTime.now())
				.modifiedAt(LocalDateTime.now())
				.modifiedBy(writer.getUsername())
				.build();

		given(commentService.changeCommentStatus(any(Long.class), any(CommentStatus.class)))
				.willReturn(
						basicComment
				);
		// when

		// then
		mockMvc.perform(
						RestDocumentationRequestBuilders.put("/admin/comments/{commentId}/status", 1)
								.with(user(adminPrincipalDetails))
								.content(objectMapper.writeValueAsString(map))
								.contentType(MediaType.APPLICATION_JSON)
				)
				.andDo(print())
				.andExpect(status().isOk())
				.andDo(document("change-comment-status-for-admin",
						preprocessRequest(prettyPrint()),
						preprocessResponse(prettyPrint()),
						pathParameters(
								parameterWithName("commentId").description("댓글 번호")
						),
						requestFields(
								fieldWithPath("commentStatus").description("변경할 상태")
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

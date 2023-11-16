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

import com.maeng0830.album.comment.domain.CommentStatus;
import com.maeng0830.album.comment.dto.request.CommentAccuseForm;
import com.maeng0830.album.comment.dto.request.CommentModifiedForm;
import com.maeng0830.album.comment.dto.request.CommentPostForm;
import com.maeng0830.album.comment.dto.response.BasicComment;
import com.maeng0830.album.comment.dto.response.GroupComment;
import com.maeng0830.album.common.model.image.Image;
import com.maeng0830.album.member.dto.MemberDto;
import com.maeng0830.album.member.dto.response.MemberSimpleResponse;
import com.maeng0830.album.support.DocsTestSupport;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.data.domain.Pageable;
import org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders;

public class CommentControllerDocsTest extends DocsTestSupport {

	@DisplayName("특정 피드 댓글 조회 API")
	@Test
	void getFeedComments() throws Exception {
		// given
		// 댓글 작성자 세팅
		MemberSimpleResponse writer1 = MemberSimpleResponse.builder()
				.id(3L)
				.username("writer1@naver.com")
				.nickname("writer1")
				.image(Image.createDefaultImage(fileDir, defaultImage.getMemberImage()))
				.build();

		MemberSimpleResponse writer2 = MemberSimpleResponse.builder()
				.id(4L)
				.username("writer2@naver.com")
				.nickname("writer2")
				.image(Image.createDefaultImage(fileDir, defaultImage.getMemberImage()))
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
								fieldWithPath("[].member.id").description("회원 번호"),
								fieldWithPath("[].member.username").description("아이디"),
								fieldWithPath("[].member.nickname").description("닉네임"),
								fieldWithPath("[].member.image").description("회원 이미지"),
								fieldWithPath("[].member.image.imageOriginalName")
										.description("원본 파일 이름"),
								fieldWithPath("[].member.image.imageStoreName")
										.description("저장 파일 이름"),
								fieldWithPath("[].member.image.imagePath").description("파일 경로"),
								// basicComments
								fieldWithPath("[].basicComments").description("자식 댓글 목록"),
								fieldWithPath("[].basicComments.[].createdAt").description("작성 일자"),
								fieldWithPath("[].basicComments.[].modifiedAt")
										.description("수정 일자"),
								fieldWithPath("[].basicComments.[].modifiedBy").description("수정자"),
								fieldWithPath("[].basicComments.[].createdAt").description("작성 일자"),
								fieldWithPath("[].basicComments.[].id").description("댓글 번호"),
								fieldWithPath("[].basicComments.[].feedId").description("피드 번호"),
								fieldWithPath("[].basicComments.[].groupId")
										.description("그룹 댓글 번호"),
								fieldWithPath("[].basicComments.[].parentId")
										.description("부모 댓글 번호"),
								fieldWithPath("[].basicComments.[].parentMember")
										.description("부모 댓글 작성자 닉네임"),
								fieldWithPath("[].basicComments.[].content").description("댓글 내용"),
								fieldWithPath("[].basicComments.[].status").description("댓글 상태"),
								fieldWithPath("[].basicComments.[].member").description("댓글 작성자"),
								fieldWithPath("[].basicComments.[].member.id").description("회원 번호"),
								fieldWithPath("[].basicComments.[].member.username").description("아이디"),
								fieldWithPath("[].basicComments.[].member.nickname").description("닉네임"),
								fieldWithPath("[].basicComments.[].member.image").description("회원 이미지"),
								fieldWithPath("[].basicComments.[].member.image.imageOriginalName")
										.description("원본 파일 이름"),
								fieldWithPath("[].basicComments.[].member.image.imageStoreName")
										.description("저장 파일 이름"),
								fieldWithPath("[].basicComments.[].member.image.imagePath")
										.description("파일 경로")
						)
				));
	}

	@DisplayName("특정 댓글 조회 API")
	@Test
	void getComment() throws Exception {
		// given
		MemberSimpleResponse writer = MemberSimpleResponse.builder()
				.id(3L)
				.username("writer@naver.com")
				.nickname("writer")
				.image(Image.createDefaultImage(fileDir, defaultImage.getMemberImage()))
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
								fieldWithPath("id").description("댓글 번호"),
								fieldWithPath("feedId").description("피드 번호"),
								fieldWithPath("groupId").description("그룹 댓글 번호"),
								fieldWithPath("parentId").description("부모 댓글 번호"),
								fieldWithPath("parentMember").description("부모 댓글 작성자 닉네임"),
								fieldWithPath("member").description("댓글 작성자"),
								fieldWithPath("member.id").description("회원 번호"),
								fieldWithPath("member.username").description("아이디"),
								fieldWithPath("member.nickname").description("닉네임"),
								fieldWithPath("member.image").description("회원 이미지"),
								fieldWithPath("member.image.imageOriginalName")
										.description("원본 파일 이름"),
								fieldWithPath("member.image.imageStoreName")
										.description("저장 파일 이름"),
								fieldWithPath("member.image.imagePath").description("파일 경로"),
								fieldWithPath("content").description("댓글 내용"),
								fieldWithPath("status").description("댓글 상태"),
								fieldWithPath("createdAt").description("작성 일자"),
								fieldWithPath("modifiedAt").description("수정 일자"),
								fieldWithPath("modifiedBy").description("수정자")
						)
				));
	}

	@DisplayName("댓글 작성 API")
	@Test
	void comment() throws Exception {
		// given
		MemberDto memberDto = memberPrincipalDetails.getMemberDto();
		MemberSimpleResponse memberSimpleResponse = createMemberSimpleResponse(memberDto);

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
				.member(memberSimpleResponse)
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
								fieldWithPath("id").description("댓글 번호"),
								fieldWithPath("feedId").description("피드 번호"),
								fieldWithPath("groupId").description("그룹 댓글 번호"),
								fieldWithPath("parentId").description("부모 댓글 번호"),
								fieldWithPath("parentMember").description("부모 댓글 작성자 닉네임"),
								fieldWithPath("member").description("댓글 작성자"),
								fieldWithPath("member.id").description("회원 번호"),
								fieldWithPath("member.username").description("아이디"),
								fieldWithPath("member.nickname").description("닉네임"),
								fieldWithPath("member.image").description("회원 이미지"),
								fieldWithPath("member.image.imageOriginalName")
										.description("원본 파일 이름"),
								fieldWithPath("member.image.imageStoreName")
										.description("저장 파일 이름"),
								fieldWithPath("member.image.imagePath").description("파일 경로"),
								fieldWithPath("content").description("댓글 내용"),
								fieldWithPath("status").description("댓글 상태"),
								fieldWithPath("createdAt").description("작성 일자"),
								fieldWithPath("modifiedAt").description("수정 일자"),
								fieldWithPath("modifiedBy").description("수정자")
						)
				));
	}

	@DisplayName("댓글 수정 API")
	@Test
	void modifiedComment() throws Exception {
		// given
		MemberSimpleResponse memberSimpleResponse = createMemberSimpleResponse(
				memberPrincipalDetails.getMemberDto());

		CommentModifiedForm commentModifiedForm = CommentModifiedForm.builder()
				.id(1L)
				.feedId(1L)
				.content("modifiedContent")
				.build();

		BasicComment basicComment = BasicComment.builder()
				.id(commentModifiedForm.getId())
				.feedId(1L)
				.groupId(1L)
				.parentId(1L)
				.parentMember("parentCommentWriter")
				.member(memberSimpleResponse)
				.content(commentModifiedForm.getContent())
				.status(CommentStatus.NORMAL)
				.createdAt(LocalDateTime.now())
				.modifiedAt(LocalDateTime.now())
				.modifiedBy(memberSimpleResponse.getUsername())
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
								fieldWithPath("feedId").description("피드 번호"),
								fieldWithPath("content").description("수정 댓글 내용")
						),
						responseFields(
								fieldWithPath("id").description("댓글 번호"),
								fieldWithPath("feedId").description("피드 번호"),
								fieldWithPath("groupId").description("그룹 댓글 번호"),
								fieldWithPath("parentId").description("부모 댓글 번호"),
								fieldWithPath("parentMember").description("부모 댓글 작성자 닉네임"),
								fieldWithPath("member").description("댓글 작성자"),
								fieldWithPath("member.id").description("회원 번호"),
								fieldWithPath("member.username").description("아이디"),
								fieldWithPath("member.nickname").description("닉네임"),
								fieldWithPath("member.image").description("회원 이미지"),
								fieldWithPath("member.image.imageOriginalName")
										.description("원본 파일 이름"),
								fieldWithPath("member.image.imageStoreName")
										.description("저장 파일 이름"),
								fieldWithPath("member.image.imagePath").description("파일 경로"),
								fieldWithPath("content").description("댓글 내용"),
								fieldWithPath("status").description("댓글 상태"),
								fieldWithPath("createdAt").description("작성 일자"),
								fieldWithPath("modifiedAt").description("수정 일자"),
								fieldWithPath("modifiedBy").description("수정자")
						)
				));
	}

	@DisplayName("댓글 신고 API")
	@Test
	void accuseComment() throws Exception {
		// given
		CommentAccuseForm commentAccuseForm = CommentAccuseForm.builder()
				.id(1L)
				.content("accuseContent")
				.build();

		MemberSimpleResponse commentWriter = MemberSimpleResponse.builder()
				.id(3L)
				.username("commentWriter@naver.com")
				.nickname("commentWriter")
				.image(Image.createDefaultImage(fileDir, defaultImage.getMemberImage()))
				.build();

		BasicComment basicComment = BasicComment.builder()
				.id(1L)
				.feedId(1L)
				.groupId(1L)
				.parentId(1L)
				.content("testContent")
				.member(commentWriter)
				.parentMember("commentWriter")
				.createdAt(LocalDateTime.now())
				.modifiedAt(LocalDateTime.now())
				.modifiedBy("commentWriter")
				.status(CommentStatus.ACCUSE)
				.build();

		given(commentService.accuseComment(any(CommentAccuseForm.class), any()))
				.willReturn(
						basicComment
				);
		// when

		// then
		mockMvc.perform(
						RestDocumentationRequestBuilders.put("/comments/accuse")
								.with(user(memberPrincipalDetails))
								.content(objectMapper.writeValueAsString(commentAccuseForm))
								.contentType(APPLICATION_JSON)
				)
				.andDo(print())
				.andExpect(status().isOk())
				.andDo(document("accuse-comment",
						preprocessRequest(prettyPrint()),
						preprocessResponse(prettyPrint()),
						requestFields(
								fieldWithPath("id").description("댓글 번호"),
								fieldWithPath("content").description("신고 내용")
						),
						responseFields(
								fieldWithPath("id").description("댓글 번호"),
								fieldWithPath("feedId").description("피드 번호"),
								fieldWithPath("groupId").description("그룹 댓글 번호"),
								fieldWithPath("parentId").description("부모 댓글 번호"),
								fieldWithPath("parentMember").description("부모 댓글 작성자 닉네임"),
								fieldWithPath("member").description("댓글 작성자"),
								fieldWithPath("member.id").description("회원 번호"),
								fieldWithPath("member.username").description("아이디"),
								fieldWithPath("member.nickname").description("닉네임"),
								fieldWithPath("member.image").description("회원 이미지"),
								fieldWithPath("member.image.imageOriginalName")
										.description("원본 파일 이름"),
								fieldWithPath("member.image.imageStoreName")
										.description("저장 파일 이름"),
								fieldWithPath("member.image.imagePath").description("파일 경로"),
								fieldWithPath("content").description("댓글 내용"),
								fieldWithPath("status").description("댓글 상태"),
								fieldWithPath("createdAt").description("작성 일자"),
								fieldWithPath("modifiedAt").description("수정 일자"),
								fieldWithPath("modifiedBy").description("수정자")
						)
				));
	}

	@DisplayName("댓글 삭제 API")
	@Test
	void deleteComment() throws Exception {
		// given
		MemberSimpleResponse memberSimpleResponse = createMemberSimpleResponse(
				memberPrincipalDetails.getMemberDto());

		BasicComment basicComment = BasicComment.builder()
				.id(1L)
				.feedId(1L)
				.groupId(1L)
				.parentId(1L)
				.parentMember("parentCommentWriter")
				.member(memberSimpleResponse)
				.content("testContent")
				.status(CommentStatus.DELETE)
				.createdAt(LocalDateTime.now())
				.modifiedAt(LocalDateTime.now())
				.modifiedBy(memberSimpleResponse.getUsername())
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
								fieldWithPath("id").description("댓글 번호"),
								fieldWithPath("feedId").description("피드 번호"),
								fieldWithPath("groupId").description("그룹 댓글 번호"),
								fieldWithPath("parentId").description("부모 댓글 번호"),
								fieldWithPath("parentMember").description("부모 댓글 작성자 닉네임"),
								fieldWithPath("member").description("댓글 작성자"),
								fieldWithPath("member.id").description("회원 번호"),
								fieldWithPath("member.username").description("아이디"),
								fieldWithPath("member.nickname").description("닉네임"),
								fieldWithPath("member.image").description("회원 이미지"),
								fieldWithPath("member.image.imageOriginalName")
										.description("원본 파일 이름"),
								fieldWithPath("member.image.imageStoreName")
										.description("저장 파일 이름"),
								fieldWithPath("member.image.imagePath").description("파일 경로"),
								fieldWithPath("content").description("댓글 내용"),
								fieldWithPath("status").description("댓글 상태"),
								fieldWithPath("createdAt").description("작성 일자"),
								fieldWithPath("modifiedAt").description("수정 일자"),
								fieldWithPath("modifiedBy").description("수정자")
						)
				));
	}

	private static MemberSimpleResponse createMemberSimpleResponse(MemberDto memberDto) {
		return MemberSimpleResponse.builder()
				.id(memberDto.getId())
				.username(memberDto.getUsername())
				.nickname(memberDto.getNickname())
				.image(memberDto.getImage())
				.build();
	}
}

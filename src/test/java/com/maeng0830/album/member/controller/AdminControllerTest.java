package com.maeng0830.album.member.controller;

import static org.springframework.http.MediaType.APPLICATION_JSON;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.user;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.maeng0830.album.comment.domain.CommentStatus;
import com.maeng0830.album.comment.dto.request.CommentChangeStatusForm;
import com.maeng0830.album.feed.domain.FeedStatus;
import com.maeng0830.album.feed.dto.request.FeedChangeStatusForm;
import com.maeng0830.album.member.domain.MemberStatus;
import com.maeng0830.album.member.dto.request.MemberChangeStatusForm;
import com.maeng0830.album.support.ControllerTestSupport;
import java.util.HashMap;
import java.util.Map;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

class AdminControllerTest extends ControllerTestSupport {

	@Test
	void getFeedsForAdmin() throws Exception {
		// given

		// when

		// then
		mockMvc.perform(
						get("/api/admin/feeds?searchText=nickname&page=0&size=20")
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
						get("/api/admin/feeds/1/accuses")
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
		FeedChangeStatusForm feedChangeStatusForm = FeedChangeStatusForm.builder()
				.id(1L)
				.feedStatus(FeedStatus.ACCUSE)
				.build();

		// when

		// then
		mockMvc.perform(
						put("/api/admin/feeds/status")
								.with(csrf())
								.with(user(adminPrincipalDetails))
								.content(objectMapper.writeValueAsString(feedChangeStatusForm))
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
						get("/api/admin/comments?searchText=nickname&page=0&size=20")
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
						get("/api/admin/comments/1/accuses")
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
		CommentChangeStatusForm commentChangeStatusForm = CommentChangeStatusForm.builder()
				.id(1L)
				.feedId(1L)
				.commentStatus(CommentStatus.DELETE)
				.build();

		// when

		// then
		mockMvc.perform(
						put("/api/admin/comments/status")
								.with(csrf())
								.with(user(adminPrincipalDetails))
								.content(objectMapper.writeValueAsString(commentChangeStatusForm))
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
						get("/api/admin/members?searchText=nickname&page=0&size=20")
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
		MemberChangeStatusForm memberChangeStatusForm = MemberChangeStatusForm.builder()
				.id(1L)
				.memberStatus(MemberStatus.LOCKED)
				.build();

		// when

		// then
		mockMvc.perform(
						put("/api/admin/members/status")
								.with(csrf())
								.with(user(adminPrincipalDetails))
								.content(objectMapper.writeValueAsString(memberChangeStatusForm))
								.contentType(APPLICATION_JSON)
				)
				.andDo(print())
				.andExpect(status().isOk());
	}
}
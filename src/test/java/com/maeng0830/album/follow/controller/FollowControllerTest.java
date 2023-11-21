package com.maeng0830.album.follow.controller;

import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.user;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.maeng0830.album.support.ControllerTestSupport;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

class FollowControllerTest extends ControllerTestSupport {

	@DisplayName("로그인한 경우, 특정 회원을 팔로우 할 수 있다.")
	@Test
	void follow() throws Exception {
		// given

		// when

		// then
		mockMvc.perform(
						post("/api/follows/1")
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
						delete("/api/follows/1")
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
						get("/api/follows/following/1?searchText=nickname&page=0&size=20")
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
						get("/api/follows/follower/1?searchText=nickname&page=0&size=20")
								.with(csrf())
								.with(user(memberPrincipalDetails))
				)
				.andDo(print())
				.andExpect(status().isOk());
	}
}
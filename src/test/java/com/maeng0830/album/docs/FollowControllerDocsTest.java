package com.maeng0830.album.docs;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.springframework.restdocs.mockmvc.MockMvcRestDocumentation.document;
import static org.springframework.restdocs.operation.preprocess.Preprocessors.preprocessRequest;
import static org.springframework.restdocs.operation.preprocess.Preprocessors.preprocessResponse;
import static org.springframework.restdocs.operation.preprocess.Preprocessors.prettyPrint;
import static org.springframework.restdocs.payload.JsonFieldType.BOOLEAN;
import static org.springframework.restdocs.payload.JsonFieldType.NUMBER;
import static org.springframework.restdocs.payload.JsonFieldType.OBJECT;
import static org.springframework.restdocs.payload.JsonFieldType.STRING;
import static org.springframework.restdocs.payload.PayloadDocumentation.fieldWithPath;
import static org.springframework.restdocs.payload.PayloadDocumentation.responseFields;
import static org.springframework.restdocs.request.RequestDocumentation.parameterWithName;
import static org.springframework.restdocs.request.RequestDocumentation.pathParameters;
import static org.springframework.restdocs.request.RequestDocumentation.requestParameters;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.user;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.maeng0830.album.common.model.image.Image;
import com.maeng0830.album.follow.dto.FollowDto;
import com.maeng0830.album.follow.dto.response.FollowerResponse;
import com.maeng0830.album.follow.dto.response.FollowingResponse;
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
import org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders;

public class FollowControllerDocsTest extends DocsTestSupport {

	@DisplayName("팔로우 API")
	@Test
	void follow() throws Exception {
		// given
		MemberDto follower = memberPrincipalDetails.getMemberDto();

		MemberDto following = MemberDto.builder()
				.id(2L)
				.username("followingUsername")
				.nickname("followingNickname")
				.phone("010-2222-2222")
				.birthDate(LocalDate.now())
				.password(passwordEncoder.encode("1234"))
				.status(MemberStatus.NORMAL)
				.role(MemberRole.ROLE_MEMBER)
				.image(Image.createDefaultImage(fileDir, defaultImage.getMemberImage()))
				.loginType(LoginType.FORM)
				.createdAt(LocalDateTime.now())
				.modifiedAt(LocalDateTime.now())
				.modifiedBy("followingUsername")
				.build();

		Map<String, String> map = new HashMap<>();
		String value = String.format("%s님이 %s님을 팔로우 합니다.", follower.getUsername(), following.getUsername());
		map.put("message", value);

		given(followService.follow(any(Long.class), any()))
				.willReturn(
						map
				);

		// when

		// then
		mockMvc.perform(
						RestDocumentationRequestBuilders.post("/api/follows/{followingId}", 2)
								.with(user(memberPrincipalDetails))
								.with(csrf())
				)
				.andDo(print())
				.andExpect(status().isOk())
				.andDo(document("follow",
						preprocessRequest(prettyPrint()),
						preprocessResponse(prettyPrint()),
						pathParameters(
								parameterWithName("followingId").description("팔로우 대상 번호")
						),
						responseFields(
								fieldWithPath("message").description("응답 메세지")
						)
				));
	}

	@DisplayName("팔로우 취소 API")
	@Test
	void cancelFollow() throws Exception {
		// given
		MemberDto follower = memberPrincipalDetails.getMemberDto();

		MemberDto following = MemberDto.builder()
				.id(2L)
				.username("followingUsername")
				.nickname("followingNickname")
				.phone("010-2222-2222")
				.birthDate(LocalDate.now())
				.password(passwordEncoder.encode("1234"))
				.status(MemberStatus.NORMAL)
				.role(MemberRole.ROLE_MEMBER)
				.image(Image.createDefaultImage(fileDir, defaultImage.getMemberImage()))
				.loginType(LoginType.FORM)
				.createdAt(LocalDateTime.now())
				.modifiedAt(LocalDateTime.now())
				.modifiedBy("followingUsername")
				.build();

		Map<String, String> map = new HashMap<>();
		String value = String.format("%s님이 %s님에 대한 팔로우를 취소하였습니다.", follower.getUsername(),
				following.getUsername());
		map.put("message", value);

		given(followService.cancelFollow(any(Long.class), any()))
				.willReturn(
						map
				);

		// when

		// then
		mockMvc.perform(
						RestDocumentationRequestBuilders.delete("/api/follows/{followingId}", 2)
								.with(user(memberPrincipalDetails))
								.with(csrf())
				)
				.andDo(print())
				.andExpect(status().isOk())
				.andDo(document("cancel-follow",
						preprocessRequest(prettyPrint()),
						preprocessResponse(prettyPrint()),
						pathParameters(
								parameterWithName("followingId").description("팔로우 취소 대상 번호")
						),
						responseFields(
								fieldWithPath("message").description("응답 메세지")
						)
				));
	}

	@DisplayName("팔로잉 목록 API")
	@Test
	void getFollowings() throws Exception {
		// given
		List<FollowingResponse> followingResponses = new ArrayList<>();
		for (int i = 0; i < 2; i++) {
			FollowingResponse followingResponse = FollowingResponse.builder()
					.followingId((long) i + 2)
					.followingNickname("nickname" + (i + 2))
					.build();

			followingResponses.add(followingResponse);
		}

		PageRequest pageRequest = PageRequest.of(0, 20);

		Page<FollowingResponse> followingResponsePage = new PageImpl<>(followingResponses, pageRequest, 2);

		given(followService.getFollowings(any(Long.class), any(String.class),
				any(Pageable.class)))
				.willReturn(
						followingResponsePage
				);

		// when

		// then
		mockMvc.perform(
						RestDocumentationRequestBuilders
								.get("/api/follows/following/{followerId}", 1)
								.queryParam("searchText", "nickname")
								.queryParam("page", "0")
								.queryParam("size", "20")
								.with(user(memberPrincipalDetails))
				)
				.andDo(print())
				.andExpect(status().isOk())
				.andDo(document("get-followings",
						preprocessRequest(prettyPrint()),
						preprocessResponse(prettyPrint()),
						pathParameters(
								parameterWithName("followerId").description("팔로워 번호")
						),
						requestParameters(
								parameterWithName("searchText").description("검색어(닉네임, 전방 일치)"),
								parameterWithName("page").description("페이지 번호"),
								parameterWithName("size").description("페이지 당 데이터 개수")
						),
						responseFields(
								fieldWithPath("content.[].followingId").description("팔로잉 아이디"),
								fieldWithPath("content.[].followingNickname").description("팔로잉 닉네임"),

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
								fieldWithPath("numberOfElements").description("현재 페이지에서 조회된 데이터 개수"),
								fieldWithPath("number").description("현재 페이지 번호"),
								fieldWithPath("size").description("페이지 당 데이터 개수"),

								fieldWithPath("sort.sorted").description("정렬"),
								fieldWithPath("sort.unsorted").description("비정렬"),
								fieldWithPath("sort.empty").description("데이터 비어있는지 여부"),

								fieldWithPath("empty").description("데이터 비어있는지 여부")
						)
				));
	}

	@DisplayName("팔로워 목록 API")
	@Test
	void getFollowers() throws Exception {
		// given
		MemberDto following = memberPrincipalDetails.getMemberDto();

		List<FollowerResponse> followerResponses = new ArrayList<>();
		for (int i = 0; i < 2; i++) {
			FollowerResponse followerResponse = FollowerResponse.builder()
					.followerId((long) i + 2)
					.followerNickname("nickname" + (i + 2))
					.build();

			followerResponses.add(followerResponse);
		}

		PageRequest pageRequest = PageRequest.of(0, 20);

		Page<FollowerResponse> followerResponsePage = new PageImpl<>(followerResponses, pageRequest, 2);

		given(followService.getFollowers(any(Long.class), any(String.class),
				any(Pageable.class)))
				.willReturn(
						followerResponsePage
				);

		// when

		// then
		mockMvc.perform(
						RestDocumentationRequestBuilders
								.get("/api/follows/follower/{followingId}", 1)
								.queryParam("searchText", "nickname")
								.queryParam("page", "0")
								.queryParam("size", "20")
								.with(user(memberPrincipalDetails))
				)
				.andDo(print())
				.andExpect(status().isOk())
				.andDo(document("get-followers",
						preprocessRequest(prettyPrint()),
						preprocessResponse(prettyPrint()),
						pathParameters(
								parameterWithName("followingId").description("팔로잉 번호")
						),
						requestParameters(
								parameterWithName("searchText").description("검색어(닉네임, 전방 일치)"),
								parameterWithName("page").description("페이지 번호"),
								parameterWithName("size").description("페이지 당 데이터 개수")
						),
						responseFields(
								fieldWithPath("content.[].followerId").description("팔로워 아이디"),
								fieldWithPath("content.[].followerNickname").description("팔로워 닉네임"),

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
								fieldWithPath("numberOfElements").description("현재 페이지에서 조회된 데이터 개수"),
								fieldWithPath("number").description("현재 페이지 번호"),
								fieldWithPath("size").description("페이지 당 데이터 개수"),

								fieldWithPath("sort.sorted").description("정렬"),
								fieldWithPath("sort.unsorted").description("비정렬"),
								fieldWithPath("sort.empty").description("데이터 비어있는지 여부"),

								fieldWithPath("empty").description("데이터 비어있는지 여부")
						)
				));
	}
}

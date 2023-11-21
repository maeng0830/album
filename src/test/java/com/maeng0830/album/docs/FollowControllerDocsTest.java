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

		FollowDto followDto = FollowDto.builder()
				.follower(follower)
				.following(following)
				.createdAt(LocalDateTime.now())
				.modifiedAt(LocalDateTime.now())
				.modifiedBy(follower.getUsername())
				.build();

		given(followService.follow(any(Long.class), any()))
				.willReturn(
						followDto
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
								fieldWithPath("createdAt").type(STRING)
										.description("팔로우 일자"),
								fieldWithPath("modifiedAt").type(STRING)
										.description("팔로우 수정일자"),
								fieldWithPath("modifiedBy").type(STRING)
										.description("팔로우 신청자"),
								fieldWithPath("follower.id").type(NUMBER)
										.description("팔로워 회원 번호"),
								fieldWithPath("follower.username").type(STRING)
										.description("팔로워 아이디"),
								fieldWithPath("follower.nickname").type(STRING)
										.description("팔로워 닉네임"),
								fieldWithPath("follower.password").type(STRING)
										.description("팔로워 암호화 비밀번호"),
								fieldWithPath("follower.phone").type(STRING)
										.description("팔로워 연락처"),
								fieldWithPath("follower.birthDate").type(STRING)
										.description("팔로워 생년월일"),
								fieldWithPath("follower.status").type(STRING)
										.description("팔로워 상태"),
								fieldWithPath("follower.role").type(STRING)
										.description("팔로워 권한"),
								fieldWithPath("follower.image").type(OBJECT)
										.description("팔로워 이미지"),
								fieldWithPath("follower.image.imageOriginalName").type(STRING)
										.description("팔로워 원본 이름"),
								fieldWithPath("follower.image.imageStoreName").type(STRING)
										.description("팔로워 저장 이름"),
								fieldWithPath("follower.image.imagePath").type(STRING)
										.description("팔로워 경로"),
								fieldWithPath("follower.loginType").type(STRING)
										.description("팔로워 로그인 타입"),
								fieldWithPath("follower.createdAt").type(STRING)
										.description("팔로워 가입 일자"),
								fieldWithPath("follower.modifiedAt").type(STRING)
										.description("팔로워 수정 일자"),
								fieldWithPath("follower.modifiedBy").type(STRING)
										.description("팔로워 수정자"),
								fieldWithPath("following.id").type(NUMBER)
										.description("팔로잉 회원 번호"),
								fieldWithPath("following.username").type(STRING)
										.description("팔로잉 아이디"),
								fieldWithPath("following.nickname").type(STRING)
										.description("팔로잉 닉네임"),
								fieldWithPath("following.password").type(STRING)
										.description("팔로잉 암호화 비밀번호"),
								fieldWithPath("following.phone").type(STRING)
										.description("팔로잉 연락처"),
								fieldWithPath("following.birthDate").type(STRING)
										.description("팔로잉 생년월일"),
								fieldWithPath("following.status").type(STRING)
										.description("팔로잉 상태"),
								fieldWithPath("following.role").type(STRING)
										.description("팔로잉 권한"),
								fieldWithPath("following.image").type(OBJECT)
										.description("팔로잉 이미지"),
								fieldWithPath("following.image.imageOriginalName").type(STRING)
										.description("팔로잉 원본 이름"),
								fieldWithPath("following.image.imageStoreName").type(STRING)
										.description("팔로잉 저장 이름"),
								fieldWithPath("following.image.imagePath").type(STRING)
										.description("팔로잉 경로"),
								fieldWithPath("following.loginType").type(STRING)
										.description("팔로잉 로그인 타입"),
								fieldWithPath("following.createdAt").type(STRING)
										.description("팔로잉 가입 일자"),
								fieldWithPath("following.modifiedAt").type(STRING)
										.description("팔로잉 수정 일자"),
								fieldWithPath("following.modifiedBy").type(STRING)
										.description("팔로잉 수정자")
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
		MemberDto follower = memberPrincipalDetails.getMemberDto();

		List<FollowDto> followDtos = new ArrayList<>();
		for (int i = 0; i < 2; i++) {
			MemberDto following = MemberDto.builder()
					.id((long) i + 2)
					.username("username")
					.nickname("nickname" + (i + 2))
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

			FollowDto followDto = FollowDto.builder()
					.follower(follower)
					.following(following)
					.createdAt(LocalDateTime.now())
					.modifiedAt(LocalDateTime.now())
					.modifiedBy(follower.getUsername())
					.build();

			followDtos.add(followDto);
		}

		PageRequest pageRequest = PageRequest.of(0, 20);

		Page<FollowDto> followDtoPage = new PageImpl<>(followDtos, pageRequest, 2);

		given(followService.getFollowings(any(Long.class), any(), any(String.class),
				any(Pageable.class)))
				.willReturn(
						followDtoPage
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
								fieldWithPath("content.[].createdAt").type(STRING)
										.description("팔로우 일자"),
								fieldWithPath("content.[].modifiedAt").type(STRING)
										.description("팔로우 수정일자"),
								fieldWithPath("content.[].modifiedBy").type(STRING)
										.description("팔로우 신청자"),
								fieldWithPath("content.[].follower.id").type(NUMBER)
										.description("팔로워 회원 번호"),
								fieldWithPath("content.[].follower.username").type(STRING)
										.description("팔로워 아이디"),
								fieldWithPath("content.[].follower.nickname").type(STRING)
										.description("팔로워 닉네임"),
								fieldWithPath("content.[].follower.password").type(STRING)
										.description("팔로워 암호화 비밀번호"),
								fieldWithPath("content.[].follower.phone").type(STRING)
										.description("팔로워 연락처"),
								fieldWithPath("content.[].follower.birthDate").type(STRING)
										.description("팔로워 생년월일"),
								fieldWithPath("content.[].follower.status").type(STRING)
										.description("팔로워 상태"),
								fieldWithPath("content.[].follower.role").type(STRING)
										.description("팔로워 권한"),
								fieldWithPath("content.[].follower.image").type(OBJECT)
										.description("팔로워 이미지"),
								fieldWithPath("content.[].follower.image.imageOriginalName").type(
												STRING)
										.description("팔로워 원본 이름"),
								fieldWithPath("content.[].follower.image.imageStoreName").type(
												STRING)
										.description("팔로워 저장 이름"),
								fieldWithPath("content.[].follower.image.imagePath").type(STRING)
										.description("팔로워 경로"),
								fieldWithPath("content.[].follower.loginType").type(STRING)
										.description("팔로워 로그인 타입"),
								fieldWithPath("content.[].follower.createdAt").type(STRING)
										.description("팔로워 가입 일자"),
								fieldWithPath("content.[].follower.modifiedAt").type(STRING)
										.description("팔로워 수정 일자"),
								fieldWithPath("content.[].follower.modifiedBy").type(STRING)
										.description("팔로워 수정자"),
								fieldWithPath("content.[].following.id").type(NUMBER)
										.description("팔로잉 회원 번호"),
								fieldWithPath("content.[].following.username").type(STRING)
										.description("팔로잉 아이디"),
								fieldWithPath("content.[].following.nickname").type(STRING)
										.description("팔로잉 닉네임"),
								fieldWithPath("content.[].following.password").type(STRING)
										.description("팔로잉 암호화 비밀번호"),
								fieldWithPath("content.[].following.phone").type(STRING)
										.description("팔로잉 연락처"),
								fieldWithPath("content.[].following.birthDate").type(STRING)
										.description("팔로잉 생년월일"),
								fieldWithPath("content.[].following.status").type(STRING)
										.description("팔로잉 상태"),
								fieldWithPath("content.[].following.role").type(STRING)
										.description("팔로잉 권한"),
								fieldWithPath("content.[].following.image").type(OBJECT)
										.description("팔로잉 이미지"),
								fieldWithPath("content.[].following.image.imageOriginalName").type(
												STRING)
										.description("팔로잉 원본 이름"),
								fieldWithPath("content.[].following.image.imageStoreName").type(
												STRING)
										.description("팔로잉 저장 이름"),
								fieldWithPath("content.[].following.image.imagePath").type(STRING)
										.description("팔로잉 경로"),
								fieldWithPath("content.[].following.loginType").type(STRING)
										.description("팔로잉 로그인 타입"),
								fieldWithPath("content.[].following.createdAt").type(STRING)
										.description("팔로잉 가입 일자"),
								fieldWithPath("content.[].following.modifiedAt").type(STRING)
										.description("팔로잉 수정 일자"),
								fieldWithPath("content.[].following.modifiedBy").type(STRING)
										.description("팔로잉 수정자"),

								fieldWithPath("pageable.sort.sorted").type(BOOLEAN)
										.description("정렬"),
								fieldWithPath("pageable.sort.empty").type(BOOLEAN)
										.description("데이터 비어있는지 여부"),
								fieldWithPath("pageable.sort.unsorted").type(BOOLEAN)
										.description("비정렬"),

								fieldWithPath("pageable.offset").type(NUMBER)
										.description("첫 데이터 인덱스"),
								fieldWithPath("pageable.pageNumber").type(NUMBER)
										.description("현재 페이지 번호"),
								fieldWithPath("pageable.pageSize").type(NUMBER)
										.description("페이지 당 데이터 개수"),
								fieldWithPath("pageable.paged").type(BOOLEAN)
										.description("페이징 정보 포함"),
								fieldWithPath("pageable.unpaged").type(BOOLEAN)
										.description("페이징 정보 비포함"),

								fieldWithPath("last").type(BOOLEAN).description("마지막 페이지 여부"),
								fieldWithPath("totalPages").type(NUMBER).description("전체 페이지 개수"),
								fieldWithPath("totalElements").type(NUMBER)
										.description("전체 데이터 개수"),
								fieldWithPath("first").type(BOOLEAN).description("첫번째 페이지 여부"),
								fieldWithPath("numberOfElements").type(NUMBER).description(
										"현재 페이지에서 조회된 데이터 개수"),
								fieldWithPath("number").type(NUMBER).description("현재 페이지 번호"),
								fieldWithPath("size").type(NUMBER).description("페이지 당 데이터 개수"),

								fieldWithPath("sort.sorted").type(BOOLEAN).description("정렬"),
								fieldWithPath("sort.unsorted").type(BOOLEAN).description("비정렬"),
								fieldWithPath("sort.empty").type(BOOLEAN)
										.description("데이터 비어있는지 여부"),

								fieldWithPath("empty").type(BOOLEAN).description("데이터 비어있는지 여부")
						)
				));
	}

	@DisplayName("팔로워 목록 API")
	@Test
	void getFollowers() throws Exception {
		// given
		MemberDto following = memberPrincipalDetails.getMemberDto();

		List<FollowDto> followDtos = new ArrayList<>();
		for (int i = 0; i < 2; i++) {
			MemberDto follower = MemberDto.builder()
					.id((long) i + 2)
					.username("username")
					.nickname("nickname" + (i + 2))
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

			FollowDto followDto = FollowDto.builder()
					.follower(follower)
					.following(following)
					.createdAt(LocalDateTime.now())
					.modifiedAt(LocalDateTime.now())
					.modifiedBy(follower.getUsername())
					.build();

			followDtos.add(followDto);
		}

		PageRequest pageRequest = PageRequest.of(0, 20);

		Page<FollowDto> followDtoPage = new PageImpl<>(followDtos, pageRequest, 2);

		given(followService.getFollowers(any(Long.class), any(), any(String.class),
				any(Pageable.class)))
				.willReturn(
						followDtoPage
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
								fieldWithPath("content.[].createdAt").type(STRING)
										.description("팔로우 일자"),
								fieldWithPath("content.[].modifiedAt").type(STRING)
										.description("팔로우 수정일자"),
								fieldWithPath("content.[].modifiedBy").type(STRING)
										.description("팔로우 신청자"),
								fieldWithPath("content.[].follower.id").type(NUMBER)
										.description("팔로워 회원 번호"),
								fieldWithPath("content.[].follower.username").type(STRING)
										.description("팔로워 아이디"),
								fieldWithPath("content.[].follower.nickname").type(STRING)
										.description("팔로워 닉네임"),
								fieldWithPath("content.[].follower.password").type(STRING)
										.description("팔로워 암호화 비밀번호"),
								fieldWithPath("content.[].follower.phone").type(STRING)
										.description("팔로워 연락처"),
								fieldWithPath("content.[].follower.birthDate").type(STRING)
										.description("팔로워 생년월일"),
								fieldWithPath("content.[].follower.status").type(STRING)
										.description("팔로워 상태"),
								fieldWithPath("content.[].follower.role").type(STRING)
										.description("팔로워 권한"),
								fieldWithPath("content.[].follower.image").type(OBJECT)
										.description("팔로워 이미지"),
								fieldWithPath("content.[].follower.image.imageOriginalName").type(
												STRING)
										.description("팔로워 원본 이름"),
								fieldWithPath("content.[].follower.image.imageStoreName").type(
												STRING)
										.description("팔로워 저장 이름"),
								fieldWithPath("content.[].follower.image.imagePath").type(STRING)
										.description("팔로워 경로"),
								fieldWithPath("content.[].follower.loginType").type(STRING)
										.description("팔로워 로그인 타입"),
								fieldWithPath("content.[].follower.createdAt").type(STRING)
										.description("팔로워 가입 일자"),
								fieldWithPath("content.[].follower.modifiedAt").type(STRING)
										.description("팔로워 수정 일자"),
								fieldWithPath("content.[].follower.modifiedBy").type(STRING)
										.description("팔로워 수정자"),
								fieldWithPath("content.[].following.id").type(NUMBER)
										.description("팔로잉 회원 번호"),
								fieldWithPath("content.[].following.username").type(STRING)
										.description("팔로잉 아이디"),
								fieldWithPath("content.[].following.nickname").type(STRING)
										.description("팔로잉 닉네임"),
								fieldWithPath("content.[].following.password").type(STRING)
										.description("팔로잉 암호화 비밀번호"),
								fieldWithPath("content.[].following.phone").type(STRING)
										.description("팔로잉 연락처"),
								fieldWithPath("content.[].following.birthDate").type(STRING)
										.description("팔로잉 생년월일"),
								fieldWithPath("content.[].following.status").type(STRING)
										.description("팔로잉 상태"),
								fieldWithPath("content.[].following.role").type(STRING)
										.description("팔로잉 권한"),
								fieldWithPath("content.[].following.image").type(OBJECT)
										.description("팔로잉 이미지"),
								fieldWithPath("content.[].following.image.imageOriginalName").type(
												STRING)
										.description("팔로잉 원본 이름"),
								fieldWithPath("content.[].following.image.imageStoreName").type(
												STRING)
										.description("팔로잉 저장 이름"),
								fieldWithPath("content.[].following.image.imagePath").type(STRING)
										.description("팔로잉 경로"),
								fieldWithPath("content.[].following.loginType").type(STRING)
										.description("팔로잉 로그인 타입"),
								fieldWithPath("content.[].following.createdAt").type(STRING)
										.description("팔로잉 가입 일자"),
								fieldWithPath("content.[].following.modifiedAt").type(STRING)
										.description("팔로잉 수정 일자"),
								fieldWithPath("content.[].following.modifiedBy").type(STRING)
										.description("팔로잉 수정자"),

								fieldWithPath("pageable.sort.sorted").type(BOOLEAN)
										.description("정렬"),
								fieldWithPath("pageable.sort.empty").type(BOOLEAN)
										.description("데이터 비어있는지 여부"),
								fieldWithPath("pageable.sort.unsorted").type(BOOLEAN)
										.description("비정렬"),

								fieldWithPath("pageable.offset").type(NUMBER)
										.description("첫 데이터 인덱스"),
								fieldWithPath("pageable.pageNumber").type(NUMBER)
										.description("현재 페이지 번호"),
								fieldWithPath("pageable.pageSize").type(NUMBER)
										.description("페이지 당 데이터 개수"),
								fieldWithPath("pageable.paged").type(BOOLEAN)
										.description("페이징 정보 포함"),
								fieldWithPath("pageable.unpaged").type(BOOLEAN)
										.description("페이징 정보 비포함"),

								fieldWithPath("last").type(BOOLEAN).description("마지막 페이지 여부"),
								fieldWithPath("totalPages").type(NUMBER).description("전체 페이지 개수"),
								fieldWithPath("totalElements").type(NUMBER)
										.description("전체 데이터 개수"),
								fieldWithPath("first").type(BOOLEAN).description("첫번째 페이지 여부"),
								fieldWithPath("numberOfElements").type(NUMBER).description(
										"현재 페이지에서 조회된 데이터 개수"),
								fieldWithPath("number").type(NUMBER).description("현재 페이지 번호"),
								fieldWithPath("size").type(NUMBER).description("페이지 당 데이터 개수"),

								fieldWithPath("sort.sorted").type(BOOLEAN).description("정렬"),
								fieldWithPath("sort.unsorted").type(BOOLEAN).description("비정렬"),
								fieldWithPath("sort.empty").type(BOOLEAN)
										.description("데이터 비어있는지 여부"),

								fieldWithPath("empty").type(BOOLEAN).description("데이터 비어있는지 여부")
						)
				));
	}
}

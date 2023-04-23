package com.maeng0830.album.follow.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.BDDMockito.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.verify;

import com.maeng0830.album.follow.domain.Follow;
import com.maeng0830.album.follow.dto.FollowDto;
import com.maeng0830.album.follow.repository.FollowRepository;
import com.maeng0830.album.member.domain.Member;
import com.maeng0830.album.member.dto.MemberDto;
import com.maeng0830.album.member.repository.MemberRepository;
import com.maeng0830.album.security.formlogin.PrincipalDetails;
import java.util.Optional;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.context.ActiveProfiles;

@ActiveProfiles({"test"})
@ExtendWith(MockitoExtension.class)
class FollowServiceTest {

	@InjectMocks
	private FollowService followService;

	@Mock
	private FollowRepository followRepository;

	@Mock
	private MemberRepository memberRepository;

	@DisplayName("팔로우-성공")
	@Test
	public void follow() {
		//given
		long followeeId = 1L;

		PrincipalDetails principalDetails = new PrincipalDetails(MemberDto.builder()
				.id(2L)
				.build());

		Member follower = Member.builder()
				.id(principalDetails.getMemberDto().getId())
				.build();

		Member followee = Member.builder()
				.id(followeeId)
				.build();

		Follow follow = Follow.builder()
				.follower(follower)
				.followee(followee)
				.build();

		given(memberRepository.findById(principalDetails.getMemberDto().getId()))
				.willReturn(Optional.of(follower));

		given(memberRepository.findById(followeeId))
				.willReturn(Optional.of(followee));

		given(followRepository.save(any())).willReturn(follow);

		//when
		FollowDto result = followService.follow(followeeId, principalDetails);

		//then
		assertThat(result.getFollower()).usingRecursiveComparison().isEqualTo(MemberDto.from(follow.getFollower()));
		assertThat(result.getFollowee()).usingRecursiveComparison().isEqualTo(MemberDto.from(follow.getFollowee()));
	}

	@DisplayName("팔로우 취소-성공")
	@Test
	public void cancelFollow() {
		//given
		long followeeId = 1L;

		PrincipalDetails principalDetails = new PrincipalDetails(MemberDto.builder()
				.id(2L)
				.build());

		Member follower = Member.builder()
				.id(principalDetails.getMemberDto().getId())
				.build();

		Member followee = Member.builder()
				.id(followeeId)
				.build();

		given(memberRepository.findById(principalDetails.getMemberDto().getId()))
				.willReturn(Optional.of(follower));

		given(memberRepository.findById(followeeId))
				.willReturn(Optional.of(followee));

		String result = followService.cancelFollow(followeeId, principalDetails);

		verify(followRepository).deleteByFollowerAndFollowee(follower, followee);

		assertThat(result).isEqualTo(String.format("%s님이 %s님에 대한 팔로우를 취소하였습니다.", follower.getUsername(), followee.getUsername()));
	}
}
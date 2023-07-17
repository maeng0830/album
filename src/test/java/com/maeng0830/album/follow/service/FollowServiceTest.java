package com.maeng0830.album.follow.service;

import static com.maeng0830.album.follow.exception.FollowExceptionCode.NOT_EXIST_FOLLOW;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import com.maeng0830.album.common.exception.AlbumException;
import com.maeng0830.album.follow.domain.Follow;
import com.maeng0830.album.follow.dto.FollowDto;
import com.maeng0830.album.follow.repository.FollowRepository;
import com.maeng0830.album.member.domain.Member;
import com.maeng0830.album.member.dto.MemberDto;
import com.maeng0830.album.member.repository.MemberRepository;
import java.util.List;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.annotation.Transactional;

@ActiveProfiles("test")
@Transactional
@SpringBootTest
class FollowServiceTest {

	@Autowired
	private FollowService followService;

	@Autowired
	private FollowRepository followRepository;

	@Autowired
	private MemberRepository memberRepository;

	@DisplayName("로그인 상태인 경우, 주어진 ID의 회원에게 팔로우할 수 있다.")
	@Test
	public void follow() {
		//given
		Member follower = Member.builder()
				.build();
		Member followee = Member.builder()
				.build();
		memberRepository.saveAll(List.of(follower, followee));

		MemberDto followerDto = MemberDto.from(follower);

		//when
		FollowDto result = followService.follow(followee.getId(), followerDto);

		//then
		assertThat(result.getFollower()).usingRecursiveComparison()
				.isEqualTo(MemberDto.from(follower));
		assertThat(result.getFollowing()).usingRecursiveComparison()
				.isEqualTo(MemberDto.from(followee));
	}

	@DisplayName("로그인 상태인 경우, 주어진 ID의 회원에 대한 팔로우를 취소할 수 있다.")
	@Test
	public void cancelFollow() {
		//given
		Member follower = Member.builder()
				.build();
		Member followee = Member.builder()
				.build();
		memberRepository.saveAll(List.of(follower, followee));

		followRepository.save(Follow.builder()
				.follower(follower)
				.following(followee)
				.build());

		MemberDto followerDto = MemberDto.from(follower);

		//when
		String result = followService.cancelFollow(followee.getId(), followerDto);

		//then
		assertThat(result).isEqualTo(
				String.format("%s님이 %s님에 대한 팔로우를 취소하였습니다.", follower.getUsername(), followee.getUsername()));
	}

	@DisplayName("주어진 ID의 회원에 대한 팔로우를 취소할 때, 팔로우가 존재하지 않는다면 예외가 발생한다.")
	@Test
	public void cancelFollow_notExistFollow() {
		//given
		Member follower = Member.builder()
				.build();
		Member followee = Member.builder()
				.build();
		memberRepository.saveAll(List.of(follower, followee));

		MemberDto followerDto = MemberDto.from(follower);

		//when

		//then
		assertThatThrownBy(() -> followService.cancelFollow(followee.getId(), followerDto))
				.isInstanceOf(AlbumException.class)
				.hasMessage(NOT_EXIST_FOLLOW.getMessage());
	}
}
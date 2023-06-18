package com.maeng0830.album.follow.repository;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.tuple;

import com.maeng0830.album.follow.domain.Follow;
import com.maeng0830.album.member.domain.Member;
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
class FollowRepositoryTest {

	@Autowired
	private FollowRepository followRepository;

	@Autowired
	private MemberRepository memberRepository;

	@DisplayName("주어진 팔로워 및 팔로이에 해당하는 팔로우를 삭제한다.")
	@Test
	void deleteByFollowerAndFollowee() {
	    // given
		Member follower1 = Member.builder()
				.build();
		Member followee1 = Member.builder()
				.build();
		Member follower2 = Member.builder()
				.build();
		Member followee2 = Member.builder()
				.build();
		memberRepository.saveAll(List.of(follower1, followee1, follower2, followee2));

		Follow follow1 = Follow.builder()
				.follower(follower1)
				.followee(followee1)
				.build();
		Follow follow2 = Follow.builder()
				.follower(follower2)
				.followee(followee2)
				.build();
		followRepository.saveAll(List.of(follow1, follow2));

	    // when
		followRepository.deleteByFollowerAndFollowee(follower1, followee1);
		List<Follow> afterResult = followRepository.findAll();

		// then
		assertThat(afterResult).hasSize(1)
				.extracting("follower", "followee")
				.containsExactlyInAnyOrder(tuple(follower2, followee2));
	}

	@DisplayName("주어진 팔로워에 해당하는 팔로우를 조회한다.")
	@Test
	void findByFollower() {
	    // given
		Member follower1 = Member.builder()
				.build();
		Member followee1 = Member.builder()
				.build();
		Member follower2 = Member.builder()
				.build();
		Member followee2 = Member.builder()
				.build();
		memberRepository.saveAll(List.of(follower1, followee1, follower2, followee2));

		Follow follow1 = Follow.builder()
				.follower(follower1)
				.followee(followee1)
				.build();
		Follow follow2 = Follow.builder()
				.follower(follower2)
				.followee(followee2)
				.build();
		followRepository.saveAll(List.of(follow1, follow2));

	    // when
		List<Follow> byFollower1 = followRepository.findByFollower(follower1);
		List<Follow> byFollower2 = followRepository.findByFollower(follower2);

		// then
		assertThat(byFollower1).hasSize(1)
				.usingRecursiveComparison().isEqualTo(List.of(follow1));
		assertThat(byFollower2).hasSize(1)
				.usingRecursiveComparison().isEqualTo(List.of(follow2));
	}

	@DisplayName("주어진 팔로이에 해당하는 팔로우를 조회한다.")
	@Test
	void findByFollowee() {
		// given
		Member follower1 = Member.builder()
				.build();
		Member followee1 = Member.builder()
				.build();
		Member follower2 = Member.builder()
				.build();
		Member followee2 = Member.builder()
				.build();
		memberRepository.saveAll(List.of(follower1, followee1, follower2, followee2));

		Follow follow1 = Follow.builder()
				.follower(follower1)
				.followee(followee1)
				.build();
		Follow follow2 = Follow.builder()
				.follower(follower2)
				.followee(followee2)
				.build();
		followRepository.saveAll(List.of(follow1, follow2));

		// when
		List<Follow> byFollowee1 = followRepository.findByFollowee(followee1);
		List<Follow> byFollowee2 = followRepository.findByFollowee(followee2);

		// then
		assertThat(byFollowee1).hasSize(1)
				.usingRecursiveComparison().isEqualTo(List.of(follow1));
		assertThat(byFollowee2).hasSize(1)
				.usingRecursiveComparison().isEqualTo(List.of(follow2));
	}
}
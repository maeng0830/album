package com.maeng0830.album.follow.repository;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.tuple;

import com.maeng0830.album.follow.domain.Follow;
import com.maeng0830.album.member.domain.Member;
import com.maeng0830.album.member.repository.MemberRepository;
import com.maeng0830.album.support.RepositoryTestSupport;
import java.util.ArrayList;
import java.util.List;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;

class FollowRepositoryTest extends RepositoryTestSupport {

	@Autowired
	private FollowRepository followRepository;

	@Autowired
	private MemberRepository memberRepository;

	@DisplayName("주어진 팔로워 및 팔로잉에 해당하는 팔로우를 삭제한다.")
	@Test
	void deleteByFollowerAndFollowee() {
		// given
		Member follower1 = Member.builder()
				.build();
		Member following1 = Member.builder()
				.build();
		Member follower2 = Member.builder()
				.build();
		Member following2 = Member.builder()
				.build();
		memberRepository.saveAll(List.of(follower1, following1, follower2, following2));

		Follow follow1 = Follow.builder()
				.follower(follower1)
				.following(following1)
				.build();
		Follow follow2 = Follow.builder()
				.follower(follower2)
				.following(following2)
				.build();
		followRepository.saveAll(List.of(follow1, follow2));

		// when
		followRepository.deleteByFollowerAndFollowing(follower1, following1);
		List<Follow> afterResult = followRepository.findAll();

		// then
		assertThat(afterResult).hasSize(1)
				.extracting("follower", "following")
				.containsExactlyInAnyOrder(tuple(follower2, following2));
	}

	@DisplayName("주어진 팔로워를 가진 팔로우를 조회한다.")
	@Test
	void searchByFollowerOrFollowee_byFollower() {
		// given
		Member follower1 = Member.builder()
				.build();
		Member following1 = Member.builder()
				.build();
		Member follower2 = Member.builder()
				.build();
		Member following2 = Member.builder()
				.build();
		memberRepository.saveAll(List.of(follower1, following1, follower2, following2));

		Follow follow1 = Follow.builder()
				.follower(follower1)
				.following(following1)
				.build();
		Follow follow2 = Follow.builder()
				.follower(follower2)
				.following(following2)
				.build();
		followRepository.saveAll(List.of(follow1, follow2));

		// when
		List<Follow> byFollower1 = followRepository.searchByFollowerOrFollowee(follower1, null);
		List<Follow> byFollower2 = followRepository.searchByFollowerOrFollowee(follower2, null);

		// then
		assertThat(byFollower1).hasSize(1)
				.usingRecursiveComparison().isEqualTo(List.of(follow1));
		assertThat(byFollower2).hasSize(1)
				.usingRecursiveComparison().isEqualTo(List.of(follow2));
	}

	@DisplayName("주어진 팔로잉을 가진 팔로우를 조회한다.")
	@Test
	void searchByFollowerOrFollowee_byFollowee() {
		// given
		Member follower1 = Member.builder()
				.build();
		Member following1 = Member.builder()
				.build();
		Member follower2 = Member.builder()
				.build();
		Member following2 = Member.builder()
				.build();
		memberRepository.saveAll(List.of(follower1, following1, follower2, following2));

		Follow follow1 = Follow.builder()
				.follower(follower1)
				.following(following1)
				.build();
		Follow follow2 = Follow.builder()
				.follower(follower2)
				.following(following2)
				.build();
		followRepository.saveAll(List.of(follow1, follow2));

		// when
		List<Follow> byFollowee1 = followRepository.searchByFollowerOrFollowee(null, following1);
		List<Follow> byFollowee2 = followRepository.searchByFollowerOrFollowee(null, following2);

		// then
		assertThat(byFollowee1).hasSize(1)
				.usingRecursiveComparison().isEqualTo(List.of(follow1));
		assertThat(byFollowee2).hasSize(1)
				.usingRecursiveComparison().isEqualTo(List.of(follow2));
	}

	@DisplayName("주어진 팔로워 또는 팔로잉을 가진 팔로우를 조회한다.")
	@Test
	void searchByFollowerOrFollowee_byFollowerAndFollowee() {
		// given
		Member follower1 = Member.builder()
				.build();
		Member following1 = Member.builder()
				.build();
		Member follower2 = Member.builder()
				.build();
		Member following2 = Member.builder()
				.build();
		memberRepository.saveAll(List.of(follower1, following1, follower2, following2));

		Follow follow1 = Follow.builder()
				.follower(follower1)
				.following(following1)
				.build();
		Follow follow2 = Follow.builder()
				.follower(follower2)
				.following(following2)
				.build();
		followRepository.saveAll(List.of(follow1, follow2));

		// when
		List<Follow> byFollower1AndFollowee2 = followRepository.searchByFollowerOrFollowee(
				follower1,
				following2);
		List<Follow> byFollower2AndFollowee1 = followRepository.searchByFollowerOrFollowee(
				follower2,
				following1);

		// then
		assertThat(byFollower1AndFollowee2).hasSize(2)
				.extracting("follower", "following")
				.containsExactlyInAnyOrder(tuple(follower1, following1),
						tuple(follower2, following2));
		assertThat(byFollower2AndFollowee1).hasSize(2)
				.extracting("follower", "following")
				.containsExactlyInAnyOrder(tuple(follower1, following1),
						tuple(follower2, following2));
	}

	@DisplayName("주어진 팔로워아이디와 일치하는 팔로우 목록을 조회한다."
			+ "팔로잉닉네임이 null인 경우, 일치하는 모든 팔로우 목록을 조회한다."
			+ "null이 아닌 경우, 팔로잉닉네임과 전방 일치하는 팔로우 목록을 조회한다.")
	@CsvSource(value = {", 15", "aaa, 5", "bbb, 10"})
	@ParameterizedTest
	void searchForMyFollowings(String followingNickname, int size) {
		// given
		List<Member> members = new ArrayList<>();
		List<Follow> follows = new ArrayList<>();

		// 팔로워 세팅
		Member follower = Member.builder()
				.build();
		members.add(follower);

		// 팔로잉 세팅
		for (int i = 0; i < 15; i++) {
			if (i <= 4) {
				Member following = Member.builder()
						.nickname("aaa" + i)
						.build();
				members.add(following);

				Follow follow = Follow.builder()
						.follower(follower)
						.following(following)
						.build();
				follows.add(follow);
			} else {
				Member following = Member.builder()
						.nickname("bbb" + i)
						.build();
				members.add(following);

				Follow follow = Follow.builder()
						.follower(follower)
						.following(following)
						.build();
				follows.add(follow);
			}
		}

		memberRepository.saveAll(members);
		followRepository.saveAll(follows);

		PageRequest pageRequest = PageRequest.of(0, 20);

		// when
		Page<Follow> findFollows = followRepository.searchForMyFollowings(follower.getId(),
				followingNickname, pageRequest);

		// then
		assertThat(findFollows.getContent()).hasSize(size);
	}

	@DisplayName("주어진 팔로잉아이디와 일치하는 팔로우 목록을 조회한다."
			+ "팔로워닉네임이 null인 경우, 일치하는 모든 팔로우 목록을 조회한다."
			+ "null이 아닌 경우, 팔로워닉네임과 전방 일치하는 팔로우 목록을 조회한다.")
	@CsvSource(value = {", 15", "aaa, 5", "bbb, 10"})
	@ParameterizedTest
	void searchForMyFollowers(String followerNickname, int size) {
		// given
		List<Member> members = new ArrayList<>();
		List<Follow> follows = new ArrayList<>();

		// 팔로잉 세팅
		Member following = Member.builder()
				.build();
		members.add(following);

		// 팔로워 세팅
		for (int i = 0; i < 15; i++) {
			if (i <= 4) {
				Member follower = Member.builder()
						.nickname("aaa" + i)
						.build();
				members.add(follower);

				Follow follow = Follow.builder()
						.follower(follower)
						.following(following)
						.build();
				follows.add(follow);
			} else {
				Member follower = Member.builder()
						.nickname("bbb" + i)
						.build();
				members.add(follower);

				Follow follow = Follow.builder()
						.follower(follower)
						.following(following)
						.build();
				follows.add(follow);
			}
		}

		memberRepository.saveAll(members);
		followRepository.saveAll(follows);

		PageRequest pageRequest = PageRequest.of(0, 20);

		// when
		Page<Follow> findFollows = followRepository.searchForMyFollowers(following.getId(),
				followerNickname, pageRequest);

		// then
		assertThat(findFollows.getContent()).hasSize(size);
	}
}
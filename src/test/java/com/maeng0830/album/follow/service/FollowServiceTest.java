package com.maeng0830.album.follow.service;

import static com.maeng0830.album.follow.exception.FollowExceptionCode.ALREADY_EXIST_FOLLOW;
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
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
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
		Member following = Member.builder()
				.build();
		memberRepository.saveAll(List.of(follower, following));

		MemberDto followerDto = MemberDto.from(follower);

		//when
		FollowDto result = followService.follow(following.getId(), followerDto);

		//then
		assertThat(result.getFollower()).usingRecursiveComparison()
				.isEqualTo(MemberDto.from(follower));
		assertThat(result.getFollowing()).usingRecursiveComparison()
				.isEqualTo(MemberDto.from(following));
	}

	@DisplayName("이미 팔로우 관계가 존재하는 경우, 주어진 ID의 회원에게 팔로우를 시도할 때 예외가 발생한다.")
	@Test
	public void follow_alreadyExistFollow() {
		//given
		// 멤버 세팅
		Member follower = Member.builder()
				.build();
		Member following = Member.builder()
				.build();
		memberRepository.saveAll(List.of(follower, following));

		MemberDto followerDto = MemberDto.from(follower);

		// 팔로우 세팅
		Follow follow = Follow.builder()
				.follower(follower)
				.following(following)
				.build();

		followRepository.save(follow);

		//when
		assertThatThrownBy(() -> followService.follow(following.getId(), followerDto))
				.isInstanceOf(AlbumException.class)
				.hasMessage(ALREADY_EXIST_FOLLOW.getMessage());

		//then
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
		Map<String, String> result = followService.cancelFollow(followee.getId(), followerDto);

		//then
		assertThat(result.get("message")).isEqualTo(
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

	@DisplayName("로그인 상태인 경우, 나의 팔로잉 목록(내가 팔로우 하는 회원들)을 조회할 수 있다."
			+ "searchText가 null인 경우, 모든 팔로잉 목록을 조회한다."
			+ "null이 아닌 경우, searchText와 닉네임이 전방 일치하는 팔로잉 목록을 조회한다.")
	@CsvSource(value = {", 15", "aaa, 5", "bbb, 10"})
	@ParameterizedTest
	void getFollowings(String searchText, int size) {
	    // given
		List<Member> members = new ArrayList<>();
		List<Follow> follows = new ArrayList<>();

		// 팔로워 세팅
		Member follower = Member.builder()
				.build();
		members.add(follower);

		// 팔로잉 및 팔로우 세팅
		for (int i = 0; i < 15; i++) {
			if (i <= 4) {
				Member following = Member.builder()
						.nickname("aaa" + i)
						.build();

				Follow follow = Follow.builder()
						.follower(follower)
						.following(following)
						.build();

				members.add(following);
				follows.add(follow);
			} else {
				Member following = Member.builder()
						.nickname("bbb" + i)
						.build();

				Follow follow = Follow.builder()
						.follower(follower)
						.following(following)
						.build();

				members.add(following);
				follows.add(follow);
			}
		}

		memberRepository.saveAll(members);
		followRepository.saveAll(follows);

		PageRequest pageRequest = PageRequest.of(0, 20);

		// when
		Page<FollowDto> findFollows = followService.getFollowings(follower.getId(),
				MemberDto.from(follower), searchText, pageRequest);

		// then
		assertThat(findFollows.getContent()).hasSize(size);
	}

	@DisplayName("로그인 상태인 경우, 나의 팔로워 목록(나를 팔로우 하는 회원들)을 조회할 수 있다."
			+ "searchText가 null인 경우, 모든 팔로워 목록을 조회한다."
			+ "null이 아닌 경우, searchText와 닉네임이 전방 일치하는 팔로워 목록을 조회한다.")
	@CsvSource(value = {", 15", "aaa, 5", "bbb, 10"})
	@ParameterizedTest
	void getFollowers(String searchText, int size) {
		// given
		List<Member> members = new ArrayList<>();
		List<Follow> follows = new ArrayList<>();

		// 팔로워 세팅
		Member following = Member.builder()
				.build();
		members.add(following);

		// 팔로잉 및 팔로우 세팅
		for (int i = 0; i < 15; i++) {
			if (i <= 4) {
				Member follower = Member.builder()
						.nickname("aaa" + i)
						.build();

				Follow follow = Follow.builder()
						.follower(follower)
						.following(following)
						.build();

				members.add(follower);
				follows.add(follow);
			} else {
				Member follower = Member.builder()
						.nickname("bbb" + i)
						.build();

				Follow follow = Follow.builder()
						.follower(follower)
						.following(following)
						.build();

				members.add(follower);
				follows.add(follow);
			}
		}

		memberRepository.saveAll(members);
		followRepository.saveAll(follows);

		PageRequest pageRequest = PageRequest.of(0, 20);

		// when
		Page<FollowDto> findFollows = followService.getFollowers(following.getId(),
				MemberDto.from(following), searchText, pageRequest);

		// then
		assertThat(findFollows.getContent()).hasSize(size);
	}
}
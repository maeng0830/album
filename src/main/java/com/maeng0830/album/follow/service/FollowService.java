package com.maeng0830.album.follow.service;

import static com.maeng0830.album.follow.exception.FollowExceptionCode.*;
import static com.maeng0830.album.member.exception.MemberExceptionCode.NOT_EXIST_MEMBER;
import static com.maeng0830.album.member.exception.MemberExceptionCode.REQUIRED_LOGIN;

import com.maeng0830.album.common.exception.AlbumException;
import com.maeng0830.album.follow.domain.Follow;
import com.maeng0830.album.follow.dto.FollowDto;
import com.maeng0830.album.follow.exception.FollowExceptionCode;
import com.maeng0830.album.follow.repository.FollowRepository;
import com.maeng0830.album.member.domain.Member;
import com.maeng0830.album.member.dto.MemberDto;
import com.maeng0830.album.member.repository.MemberRepository;
import com.maeng0830.album.security.formlogin.PrincipalDetails;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.hibernate.exception.ConstraintViolationException;
import org.springframework.dao.DataAccessException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@Service
@RequiredArgsConstructor
public class FollowService {

	private final FollowRepository followRepository;
	private final MemberRepository memberRepository;

	@Transactional
	public FollowDto follow(Long followingId, MemberDto memberDto) {
		// 로그인 여부 확인
		if (memberDto == null) {
			throw new AlbumException(REQUIRED_LOGIN);
		}

		// 본인한테 신청하는지 확인
		if (memberDto.getId().equals(followingId)) {
			throw new AlbumException(NOT_ALLOW_FOLLOW_YOURSELF);
		}

		log.info("본인 조회");
		Member follower = memberRepository.findById(memberDto.getId())
				.orElseThrow(() -> new AlbumException(
						NOT_EXIST_MEMBER));

		log.info("타인 조회");
		Member following = memberRepository.findById(followingId)
				.orElseThrow(() -> new AlbumException(NOT_EXIST_MEMBER));

		log.info("팔로우 저장");
		// 이미 팔로우 관계가 존재하는 지 확인
		followRepository
				.findByFollower_IdAndFollowing_Id(follower.getId(), following.getId())
				.ifPresent(f -> {throw new AlbumException(ALREADY_EXIST_FOLLOW);});

		Follow follow = followRepository.save(Follow.builder()
				.follower(follower)
				.following(following)
				.build());

		return FollowDto.from(follow);
	}

	public Map<String, String> cancelFollow(Long followingId, MemberDto memberDto) {

		log.info("본인 조회");
		Member follower = memberRepository.findById(memberDto.getId())
				.orElseThrow(() -> new AlbumException(NOT_EXIST_MEMBER));

		log.info("타인 조회");
		Member following = memberRepository.findById(followingId)
				.orElseThrow(() -> new AlbumException(NOT_EXIST_MEMBER));

		log.info("팔로우 삭제");
		int count = followRepository.deleteByFollowerAndFollowing(follower, following);
		System.out.println("count = " + count);

		if (count == 0) {
			throw new AlbumException(NOT_EXIST_FOLLOW);
		} else {
			Map<String, String> map = new HashMap<>();

			String value = String.format("%s님이 %s님에 대한 팔로우를 취소하였습니다.", follower.getUsername(),
					following.getUsername());
			map.put("message", value);

			return map;
		}
	}

	public Page<FollowDto> getFollowings(Long followerId, String searchText, Pageable pageable) {
		Page<Follow> followings = followRepository.searchForMyFollowings(followerId, searchText,
				pageable);

		return followings.map(FollowDto::from);
	}

	public Page<FollowDto> getFollowers(Long followingId, String searchText, Pageable pageable) {
		Page<Follow> followers = followRepository.searchForMyFollowers(followingId, searchText,
				pageable);

		return followers.map(FollowDto::from);
	}
}

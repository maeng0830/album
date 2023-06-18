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
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@Service
@RequiredArgsConstructor
public class FollowService {

	private final FollowRepository followRepository;
	private final MemberRepository memberRepository;

	@Transactional
	public FollowDto follow(Long followeeId, MemberDto memberDto) {

		if (memberDto == null) {
			throw new AlbumException(REQUIRED_LOGIN);
		}

		log.info("follower 조회");
		Member follower = memberRepository.findById(memberDto.getId())
				.orElseThrow(() -> new AlbumException(
						NOT_EXIST_MEMBER));

		log.info("followee 조회");
		Member followee = memberRepository.findById(followeeId)
				.orElseThrow(() -> new AlbumException(NOT_EXIST_MEMBER));

		log.info("팔로우 저장");
		Follow follow = followRepository.save(Follow.builder()
				.follower(follower)
				.followee(followee)
				.build());

		return FollowDto.from(follow);
	}

	public String cancelFollow(Long followeeId, MemberDto memberDto) {

		log.info("follower 조회");
		Member follower = memberRepository.findById(memberDto.getId())
				.orElseThrow(() -> new AlbumException(NOT_EXIST_MEMBER));

		log.info("followee 조회");
		Member followee = memberRepository.findById(followeeId)
				.orElseThrow(() -> new AlbumException(NOT_EXIST_MEMBER));

		log.info("팔로우 삭제");
		int count = followRepository.deleteByFollowerAndFollowee(follower, followee);
		System.out.println("count = " + count);

		if (count == 0) {
			throw new AlbumException(NOT_EXIST_FOLLOW);
		} else {
			return String.format("%s님이 %s님에 대한 팔로우를 취소하였습니다.", follower.getUsername(), followee.getUsername());
		}
	}
}

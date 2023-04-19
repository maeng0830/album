package com.maeng0830.album.follow.service;

import static com.maeng0830.album.member.exception.MemberExceptionCode.NOT_EXIST_MEMBER;

import com.maeng0830.album.common.exception.AlbumException;
import com.maeng0830.album.follow.domain.Follow;
import com.maeng0830.album.follow.dto.FollowDto;
import com.maeng0830.album.follow.repository.FollowRepository;
import com.maeng0830.album.member.domain.Member;
import com.maeng0830.album.member.dto.MemberDto;
import com.maeng0830.album.member.repository.MemberRepository;
import com.maeng0830.album.security.formlogin.PrincipalDetails;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class FollowService {

	private final FollowRepository followRepository;
	private final MemberRepository memberRepository;

	public FollowDto follow(Long followeeId, PrincipalDetails principalDetails) {

		MemberDto loginMemberDto = principalDetails.getMemberDto();

		// follower(현재 로그인 회원), followee(팔로우 대상 회원)
		Member follower = memberRepository.findById(loginMemberDto.getId())
				.orElseThrow(() -> new AlbumException(
						NOT_EXIST_MEMBER));
		Member followee = memberRepository.findById(followeeId)
				.orElseThrow(() -> new AlbumException(NOT_EXIST_MEMBER));

		Follow follow = followRepository.save(Follow.builder()
				.follower(follower)
				.followee(followee)
				.build());

		return FollowDto.from(follow);
	}

	public String cancelFollow(Long followeeId, PrincipalDetails principalDetails) {

		Member follower = memberRepository.findById(principalDetails.getMemberDto().getId())
				.orElseThrow(() -> new AlbumException(NOT_EXIST_MEMBER));

		Member followee = memberRepository.findById(followeeId)
				.orElseThrow(() -> new AlbumException(NOT_EXIST_MEMBER));

		followRepository.deleteByFollowerAndFollowee(follower, followee);
		return String.format("%s님이 %s님에 대한 팔로우를 취소하였습니다.", follower.getUsername(), followee.getUsername());
	}
}

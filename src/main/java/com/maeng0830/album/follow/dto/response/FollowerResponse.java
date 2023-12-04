package com.maeng0830.album.follow.dto.response;

import com.maeng0830.album.follow.domain.Follow;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@NoArgsConstructor
@Getter
public class FollowerResponse {

	private Long followerId;
	private String followerNickname;

	@Builder
	public FollowerResponse(Long followerId, String followerNickname) {
		this.followerId = followerId;
		this.followerNickname = followerNickname;
	}

	public static FollowerResponse from(Follow follow) {
		return FollowerResponse.builder()
				.followerId(follow.getFollower().getId())
				.followerNickname(follow.getFollower().getNickname())
				.build();
	}
}

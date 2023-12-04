package com.maeng0830.album.follow.dto.response;

import com.maeng0830.album.follow.domain.Follow;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@NoArgsConstructor
@Getter
public class FollowingResponse {

	private Long followingId;
	private String followingNickname;

	@Builder
	public FollowingResponse(Long followingId, String followingNickname) {
		this.followingId = followingId;
		this.followingNickname = followingNickname;
	}

	public static FollowingResponse from(Follow follow) {
		return FollowingResponse.builder()
				.followingId(follow.getFollowing().getId())
				.followingNickname(follow.getFollowing().getNickname())
				.build();
	}
}

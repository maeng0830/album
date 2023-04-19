package com.maeng0830.album.follow.dto;

import com.maeng0830.album.common.TimeStamp;
import com.maeng0830.album.follow.domain.Follow;
import com.maeng0830.album.member.domain.Member;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.SuperBuilder;

@Getter
@Setter
@SuperBuilder
@NoArgsConstructor
@AllArgsConstructor
public class FollowDto extends TimeStamp {

	private Member follower;
	private Member followee;

	public static FollowDto from(Follow follow) {
		return FollowDto.builder()
				.follower(follow.getFollower())
				.followee(follow.getFollowee())
				.createdAt(follow.getCreatedAt())
				.modifiedAt(follow.getModifiedAt())
				.build();
	}
}

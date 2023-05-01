package com.maeng0830.album.follow.dto;

import com.maeng0830.album.common.model.entity.TimeEntity;
import com.maeng0830.album.follow.domain.Follow;
import com.maeng0830.album.member.dto.MemberDto;
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
public class FollowDto extends TimeEntity {

	private MemberDto follower;
	private MemberDto followee;

	public static FollowDto from(Follow follow) {
		return FollowDto.builder()
				.follower(MemberDto.from(follow.getFollower()))
				.followee(MemberDto.from(follow.getFollowee()))
				.createdAt(follow.getCreatedAt())
				.modifiedAt(follow.getModifiedAt())
				.build();
	}
}

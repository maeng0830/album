package com.maeng0830.album.follow.dto;

import com.maeng0830.album.common.model.entity.BaseEntity;
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
public class FollowDto extends BaseEntity {

	private MemberDto follower;
	private MemberDto following;

	public static FollowDto from(Follow follow) {
		return FollowDto.builder()
				.follower(MemberDto.from(follow.getFollower()))
				.following(MemberDto.from(follow.getFollowing()))
				.createdAt(follow.getCreatedAt())
				.modifiedAt(follow.getModifiedAt())
				.modifiedBy(follow.getModifiedBy())
				.build();
	}
}

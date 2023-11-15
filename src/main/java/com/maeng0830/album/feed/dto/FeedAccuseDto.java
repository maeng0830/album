package com.maeng0830.album.feed.dto;

import com.maeng0830.album.common.model.entity.BaseEntity;
import com.maeng0830.album.feed.domain.FeedAccuse;
import com.maeng0830.album.member.dto.response.MemberSimpleResponse;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.SuperBuilder;

@SuperBuilder
@Getter
@Setter
@NoArgsConstructor
public class FeedAccuseDto extends BaseEntity {

	private Long id;
	private String content;
	private MemberSimpleResponse member;
	private FeedDto feedDto;

	public static FeedAccuseDto from(FeedAccuse feedAccuse) {
		return FeedAccuseDto.builder()
				.id(feedAccuse.getId())
				.content(feedAccuse.getContent())
				.member(MemberSimpleResponse.from(feedAccuse.getMember()))
				.feedDto(FeedDto.from(feedAccuse.getFeed()))
				.createdAt(feedAccuse.getCreatedAt())
				.modifiedAt(feedAccuse.getModifiedAt())
				.modifiedBy(feedAccuse.getModifiedBy())
				.build();
	}
}

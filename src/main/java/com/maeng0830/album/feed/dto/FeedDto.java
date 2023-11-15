package com.maeng0830.album.feed.dto;

import com.maeng0830.album.common.model.entity.BaseEntity;
import com.maeng0830.album.feed.domain.Feed;
import com.maeng0830.album.feed.domain.FeedStatus;
import com.maeng0830.album.member.dto.response.MemberSimpleResponse;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.SuperBuilder;

@SuperBuilder
@Getter
@Setter
@NoArgsConstructor
public class FeedDto extends BaseEntity {
	private Long id;
	private String title;
	private String content;
	private int hits;
	private int commentCount;
	private FeedStatus status;
	private MemberSimpleResponse member;

	public static FeedDto from(Feed feed) {
		return FeedDto.builder()
				.id(feed.getId())
				.title(feed.getTitle())
				.content(feed.getContent())
				.hits(feed.getHits())
				.commentCount(feed.getCommentCount())
				.status(feed.getStatus())
				.createdAt(feed.getCreatedAt())
				.modifiedAt(feed.getModifiedAt())
				.modifiedBy(feed.getModifiedBy())
				.member(MemberSimpleResponse.from(feed.getMember()))
				.build();
	}
}

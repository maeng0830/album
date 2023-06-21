package com.maeng0830.album.comment.dto;

import com.maeng0830.album.comment.domain.CommentStatus;
import com.maeng0830.album.common.model.entity.TimeEntity;
import com.maeng0830.album.feed.dto.FeedDto;
import com.maeng0830.album.member.dto.MemberDto;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.SuperBuilder;

@Getter
@Setter
@SuperBuilder
public class CommentDto extends TimeEntity {

	private Long id;
	private MemberDto member;
	private FeedDto feed;
	private CommentDto group;
	private CommentDto parent;
	private String content;
	private CommentStatus status;
}

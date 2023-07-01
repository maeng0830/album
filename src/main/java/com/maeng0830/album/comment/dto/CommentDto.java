package com.maeng0830.album.comment.dto;

import com.maeng0830.album.comment.domain.Comment;
import com.maeng0830.album.comment.domain.CommentStatus;
import com.maeng0830.album.common.model.entity.BaseEntity;
import com.maeng0830.album.feed.dto.FeedDto;
import com.maeng0830.album.member.dto.MemberDto;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.SuperBuilder;

@Getter
@Setter
@SuperBuilder
public class CommentDto extends BaseEntity {

	private Long id;
	private MemberDto member;
	private FeedDto feed;
	private Long groupId;
	private Long parentId;
	private String content;
	private CommentStatus status;

	public static CommentDto from(Comment comment) {
		return CommentDto.builder()
				.id(comment.getId())
				.member(MemberDto.from(comment.getMember()))
				.feed(FeedDto.from(comment.getFeed()))
				.groupId(comment.getGroup().getId())
				.parentId(comment.getParent().getId())
				.content(comment.getContent())
				.status(comment.getStatus())
				.createdAt(comment.getCreatedAt())
				.modifiedAt(comment.getModifiedAt())
				.modifiedBy(comment.getModifiedBy())
				.build();
	}
}

package com.maeng0830.album.comment.dto.response;

import com.maeng0830.album.comment.domain.Comment;
import com.maeng0830.album.comment.domain.CommentStatus;
import com.maeng0830.album.common.model.entity.TimeEntity;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.SuperBuilder;

@SuperBuilder
@Getter
@Setter
@NoArgsConstructor
public class BasicComment extends TimeEntity {

	private Long id;
	private Long feedId;
	private Long groupId;
	private Long parentId;
	private String parentCreatedBy;
	private String createdBy;
	private String content;
	private CommentStatus status;

	public static BasicComment from(Comment comment) {
		return BasicComment.builder()
				.id(comment.getId())
				.feedId(comment.getFeed().getId())
				.groupId(comment.getGroup().getId())
				.parentId(comment.getParent().getId())
				.parentCreatedBy(comment.getParent().getMember().getUsername())
				.createdBy(comment.getMember().getUsername())
				.content(comment.getContent())
				.status(comment.getStatus())
				.createdAt(comment.getCreatedAt())
				.modifiedAt(comment.getModifiedAt())
				.build();
	}
}

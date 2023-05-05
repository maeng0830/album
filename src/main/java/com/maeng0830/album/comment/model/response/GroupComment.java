package com.maeng0830.album.comment.model.response;

import com.maeng0830.album.comment.domain.Comment;
import com.maeng0830.album.comment.domain.CommentStatus;
import com.maeng0830.album.common.model.entity.TimeEntity;
import java.util.List;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.SuperBuilder;

@SuperBuilder
@Getter
@Setter
public class GroupComment extends TimeEntity {

	private Long id;
	private Long feedId;
	private Long groupId;
	private Long parentId;
	private String createdBy;
	private String content;
	private CommentStatus status;
	private List<BasicComment> basicComments;

	public static GroupComment from(Comment comment) {
		return GroupComment.builder()
				.id(comment.getId())
				.feedId(comment.getFeed().getId())
				.groupId(comment.getGroup().getId())
				.parentId(comment.getParent().getId())
				.createdBy(comment.getMember().getUsername())
				.content(comment.getContent())
				.status(comment.getStatus())
				.createdAt(comment.getCreatedAt())
				.modifiedAt(comment.getModifiedAt())
				.build();
	}

	public void addBasicComments(List<BasicComment> basicComments) {
		this.basicComments.addAll(basicComments);
	}
}


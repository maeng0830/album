package com.maeng0830.album.comment.dto.request;

import com.maeng0830.album.comment.domain.CommentStatus;
import javax.validation.constraints.NotNull;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class CommentChangeStatusForm {

	@NotNull
	private Long id;
	private CommentStatus commentStatus;
	@NotNull
	private Long feedId;

	@Builder
	public CommentChangeStatusForm(Long id, CommentStatus commentStatus, Long feedId) {
		this.id = id;
		this.commentStatus = commentStatus;
		this.feedId = feedId;
	}
}

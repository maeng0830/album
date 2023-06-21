package com.maeng0830.album.comment.dto.request;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class CommentAccuseForm {

	@NotNull
	private Long commentId;
	@NotBlank
	private String content;

	@Builder
	private CommentAccuseForm(Long commentId, String content) {
		this.commentId = commentId;
		this.content = content;
	}
}

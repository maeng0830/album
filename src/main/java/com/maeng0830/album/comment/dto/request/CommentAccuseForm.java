package com.maeng0830.album.comment.dto.request;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class CommentAccuseForm {

	@NotBlank
	private String content;

	@Builder
	private CommentAccuseForm(String content) {
		this.content = content;
	}
}

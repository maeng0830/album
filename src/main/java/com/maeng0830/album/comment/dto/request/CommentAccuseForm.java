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
	private Long id;
	@NotBlank
	private String content;

	@Builder
	public CommentAccuseForm(Long id, String content) {
		this.id = id;
		this.content = content;
	}
}

package com.maeng0830.album.comment.dto.request;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class CommentModifiedForm {

	@NotNull
	private Long id;
	@NotBlank
	private String content;
	@NotNull
	private Long feedId;

	@Builder
	private CommentModifiedForm(Long id, String content, Long feedId) {
		this.id = id;
		this.content = content;
		this.feedId = feedId;
	}
}

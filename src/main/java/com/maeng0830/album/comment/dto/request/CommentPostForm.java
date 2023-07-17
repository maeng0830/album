package com.maeng0830.album.comment.dto.request;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class CommentPostForm {

	@NotNull
	private Long feedId;
	private Long groupId;
	private Long parentId;
	@NotBlank
	private String content;

	@Builder
	public CommentPostForm(Long feedId, Long groupId, Long parentId, String content) {
		this.feedId = feedId;
		this.groupId = groupId;
		this.parentId = parentId;
		this.content = content;
	}
}

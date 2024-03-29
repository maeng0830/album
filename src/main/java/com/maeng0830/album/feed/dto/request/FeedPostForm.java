package com.maeng0830.album.feed.dto.request;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class FeedPostForm {

	@NotBlank
	private String title;
	@NotNull
	private String content;

	@Builder
	private FeedPostForm(String title, String content) {
		this.title = title;
		this.content = content;
	}
}

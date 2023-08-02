package com.maeng0830.album.feed.dto.request;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class FeedModifiedForm {

	@NotNull
	private Long id;
	@NotBlank
	private String title;
	@NotNull
	private String content;

	@Builder
	private FeedModifiedForm(Long id, String title, String content) {
		this.id = id;
		this.title = title;
		this.content = content;
	}
}

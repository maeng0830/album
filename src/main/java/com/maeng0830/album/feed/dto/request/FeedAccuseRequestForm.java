package com.maeng0830.album.feed.dto.request;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class FeedAccuseRequestForm {

	@NotNull
	private Long id;
	@NotBlank
	private String content;

	@Builder
	public FeedAccuseRequestForm(Long id, String content) {
		this.id = id;
		this.content = content;
	}
}

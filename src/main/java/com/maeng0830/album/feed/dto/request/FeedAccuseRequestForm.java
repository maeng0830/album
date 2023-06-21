package com.maeng0830.album.feed.dto.request;

import javax.validation.constraints.NotBlank;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class FeedAccuseRequestForm {

	@NotBlank
	private String content;

	@Builder
	private FeedAccuseRequestForm(String content) {
		this.content = content;
	}
}

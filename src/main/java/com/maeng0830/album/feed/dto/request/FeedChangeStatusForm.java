package com.maeng0830.album.feed.dto.request;

import com.maeng0830.album.feed.domain.FeedStatus;
import com.maeng0830.album.member.domain.MemberStatus;
import javax.validation.constraints.NotNull;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class FeedChangeStatusForm {

	@NotNull
	private Long id;
	private FeedStatus feedStatus;

	@Builder
	public FeedChangeStatusForm(Long id, FeedStatus feedStatus) {
		this.id = id;
		this.feedStatus = feedStatus;
	}
}

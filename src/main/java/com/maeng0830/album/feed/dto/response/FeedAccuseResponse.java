package com.maeng0830.album.feed.dto.response;

import com.maeng0830.album.feed.domain.FeedAccuse;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class FeedAccuseResponse {

	private Long feedId; // 신고 피드 번호
	private String username; // 신고자 username
	private String nickname; // 신고자 nickname
	private String content; // 신고 내용

	@Builder
	public FeedAccuseResponse(Long feedId, String username, String nickname, String content) {
		this.feedId = feedId;
		this.username = username;
		this.nickname = nickname;
		this.content = content;
	}

	public static FeedAccuseResponse from(FeedAccuse feedAccuse) {
		return FeedAccuseResponse.builder()
				.feedId(feedAccuse.getFeed().getId())
				.username(feedAccuse.getMember().getUsername())
				.nickname(feedAccuse.getMember().getNickname())
				.content(feedAccuse.getContent())
				.build();
	}
}

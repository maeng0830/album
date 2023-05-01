package com.maeng0830.album.feed.dto;

import com.maeng0830.album.feed.domain.FeedAccuse;
import com.maeng0830.album.member.dto.MemberDto;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.SuperBuilder;

@SuperBuilder
@Getter
@Setter
@NoArgsConstructor
public class FeedAccuseDto {

	private Long id;
	private String content;
	private MemberDto memberDto;
	private FeedDto feedDto;

	public static FeedAccuseDto from(FeedAccuse feedAccuse) {
		return FeedAccuseDto.builder()
				.id(feedAccuse.getId())
				.content(feedAccuse.getContent())
				.memberDto(MemberDto.from(feedAccuse.getMember()))
				.feedDto(FeedDto.from(feedAccuse.getFeed()))
				.build();
	}
}

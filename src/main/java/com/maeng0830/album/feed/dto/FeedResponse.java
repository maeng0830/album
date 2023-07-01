package com.maeng0830.album.feed.dto;

import com.maeng0830.album.common.model.image.Image;
import com.maeng0830.album.feed.domain.Feed;
import com.maeng0830.album.feed.domain.FeedImage;
import com.maeng0830.album.member.dto.MemberDto;
import java.util.ArrayList;
import java.util.List;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class FeedResponse {

	private String title;
	private String content;
	private int hits;
	private int commentCount;

	private MemberDto member;

	private List<Image> feedImages = new ArrayList<>();

	public FeedResponse(Feed feed, List<FeedImage> feedImages) {
		this.title = feed.getTitle();
		this.content = feed.getContent();
		this.hits = feed.getHits();
		this.commentCount = feed.getCommentCount();
		this.member = MemberDto.from(feed.getMember());

		for (FeedImage feedImage : feedImages) {
			Image image = Image.builder()
					.imageOriginalName(feedImage.getImage().getImageOriginalName())
					.imageStoreName(feedImage.getImage().getImageStoreName())
					.imagePath(feedImage.getImage().getImagePath())
					.build();

			this.feedImages.add(image);
		}
	}
}

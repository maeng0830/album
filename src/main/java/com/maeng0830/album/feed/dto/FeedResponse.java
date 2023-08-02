package com.maeng0830.album.feed.dto;

import com.maeng0830.album.common.model.entity.BaseEntity;
import com.maeng0830.album.common.model.image.Image;
import com.maeng0830.album.feed.domain.Feed;
import com.maeng0830.album.feed.domain.FeedImage;
import com.maeng0830.album.feed.domain.FeedStatus;
import com.maeng0830.album.member.dto.MemberDto;
import java.util.ArrayList;
import java.util.List;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.SuperBuilder;

@SuperBuilder
@Getter
@Setter
@NoArgsConstructor
public class FeedResponse extends BaseEntity {

	private Long id;
	private String title;
	private String content;
	private int hits;
	private int commentCount;
	private FeedStatus status;
	private MemberDto member;
	private List<Image> feedImages = new ArrayList<>();

	public static FeedResponse createFeedResponse(Feed feed, List<FeedImage> feedImages) {
		FeedResponse feedResponse = FeedResponse.builder()
				.id(feed.getId())
				.title(feed.getTitle())
				.content(feed.getContent())
				.hits(feed.getHits())
				.commentCount(feed.getCommentCount())
				.status(feed.getStatus())
				.member(MemberDto.from(feed.getMember()))
				.feedImages(new ArrayList<>())
				.createdAt(feed.getCreatedAt())
				.modifiedAt(feed.getModifiedAt())
				.modifiedBy(feed.getModifiedBy())
				.build();

		for (FeedImage feedImage : feedImages) {
			Image image = Image.builder()
					.imageOriginalName(feedImage.getImage().getImageOriginalName())
					.imageStoreName(feedImage.getImage().getImageStoreName())
					.imagePath(feedImage.getImage().getImagePath())
					.build();

			feedResponse.feedImages.add(image);
		}

		return feedResponse;
	}
}

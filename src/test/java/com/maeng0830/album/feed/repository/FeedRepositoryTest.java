package com.maeng0830.album.feed.repository;

import static com.maeng0830.album.feed.domain.FeedStatus.*;
import static org.assertj.core.api.Assertions.*;

import com.maeng0830.album.common.model.image.Image;
import com.maeng0830.album.feed.domain.Feed;
import com.maeng0830.album.feed.domain.FeedImage;
import java.util.List;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.annotation.Transactional;

@Transactional
@ActiveProfiles("test")
@SpringBootTest
class FeedRepositoryTest {

	@Autowired
	private FeedRepository feedRepository;

	@Autowired
	private FeedImageRepository feedImageRepository;

	@DisplayName("피드 상태 및 피드 작성자와 일치하는 피드와 피드이미지를 함께 조회한다")
	@Test
	void searchByStatusAndCreatedBy() {
		// given
		// Image 세팅
		Image image1 = Image.builder()
				.imageOriginalName("testImageOriginalName1")
				.imageStoreName("testImageStoreName1")
				.imagePath("testImagePath1")
				.build();
		Image image2 = Image.builder()
				.imageOriginalName("testImageOriginalName2")
				.imageStoreName("testImageStoreName2")
				.imagePath("testImagePath2")
				.build();

		// FeedImage 세팅
		FeedImage feedImage1 = FeedImage.builder()
				.image(image1)
				.build();
		FeedImage feedImage2 = FeedImage.builder()
				.image(image2)
				.build();

		// Feed 세팅
		Feed feed1 = Feed.builder()
				.status(NORMAL)
				.createdBy("memberA")
				.build();
		Feed feed2 = Feed.builder()
				.status(ACCUSE)
				.createdBy("memberA")
				.build();
		Feed feed3 = Feed.builder()
				.status(DELETE)
				.createdBy("memberA")
				.build();
		Feed feed4 = Feed.builder()
				.status(NORMAL)
				.createdBy("memberB")
				.build();
		Feed feed5 = Feed.builder()
				.status(ACCUSE)
				.createdBy("memberB")
				.build();
		Feed feed6 = Feed.builder()
				.status(DELETE)
				.createdBy("memberB")
				.build();

		feed1.addFeedImage(feedImage1);
		feed5.addFeedImage(feedImage2);
		feedRepository.saveAll(List.of(feed1, feed2, feed3, feed4, feed5, feed6));
		feedImageRepository.saveAll(List.of(feedImage1, feedImage2));


		// when
		List<Feed> result1 = feedRepository.searchByStatusAndCreatedBy(List.of(NORMAL, ACCUSE),
				List.of("memberA"));
		List<Feed> result2 = feedRepository.searchByStatusAndCreatedBy(List.of(ACCUSE, DELETE),
				List.of("memberA", "memberB"));

		// then
		assertThat(result1).hasSize(2)
				.extracting("status", "createdBy", "feedImages")
				.containsExactlyInAnyOrder(
						tuple(NORMAL, "memberA", feed1.getFeedImages()),
						tuple(ACCUSE, "memberA", feed2.getFeedImages())
				);

		assertThat(result2).hasSize(4)
				.extracting("status", "createdBy", "feedImages")
				.containsExactlyInAnyOrder(
						tuple(ACCUSE, "memberA", feed2.getFeedImages()),
						tuple(DELETE, "memberA", feed3.getFeedImages()),
						tuple(ACCUSE, "memberB", feed5.getFeedImages()),
						tuple(DELETE, "memberB", feed6.getFeedImages())
				);
	}
}
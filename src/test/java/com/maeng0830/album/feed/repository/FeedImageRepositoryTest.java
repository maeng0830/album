package com.maeng0830.album.feed.repository;

import static org.assertj.core.api.Assertions.*;
import static org.junit.jupiter.api.Assertions.*;

import com.maeng0830.album.feed.domain.Feed;
import com.maeng0830.album.feed.domain.FeedImage;
import java.util.List;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.annotation.Transactional;

@Transactional
@ActiveProfiles("test")
@SpringBootTest
class FeedImageRepositoryTest {

	@Autowired
	FeedRepository feedRepository;

	@Autowired
	FeedImageRepository feedImageRepository;

	@DisplayName("피드 아이디를 통해 관련된 피드 이미지를 조회할 수 있다.")
	@Test
	void findByFeed_Id() {
	    // given
		FeedImage feedImage1 = FeedImage.builder()
				.build();
		FeedImage feedImage2 = FeedImage.builder()
				.build();
		FeedImage feedImage3 = FeedImage.builder()
				.build();
		FeedImage feedImage4 = FeedImage.builder()
				.build();

		Feed feed1 = new Feed();
		feed1.addFeedImage(feedImage1);
		feed1.addFeedImage(feedImage2);
		Feed feed2 = new Feed();
		feed2.addFeedImage(feedImage3);
		feed2.addFeedImage(feedImage4);

		feedRepository.saveAll(List.of(feed1, feed2));
		feedImageRepository.saveAll(List.of(feedImage1, feedImage2, feedImage3, feedImage4));

		// when
		List<FeedImage> result1 = feedImageRepository.findByFeed_Id(feed1.getId());
		List<FeedImage> result2 = feedImageRepository.findByFeed_Id(feed2.getId());

		// then
		assertThat(result1).hasSize(2)
				.extracting("feed.id")
				.containsExactlyInAnyOrder(feed1.getId(), feed1.getId());
		assertThat(result2).hasSize(2)
				.extracting("feed.id")
				.containsExactlyInAnyOrder(feed2.getId(), feed2.getId());
	}

	@DisplayName("피드 아이디를 통해 관련된 피드 이미지를 삭제할 수 있다.")
	@Test
	void deleteFeedImageByFeed_Id() {
	    // given
		FeedImage feedImage1 = FeedImage.builder()
				.build();
		FeedImage feedImage2 = FeedImage.builder()
				.build();
		FeedImage feedImage3 = FeedImage.builder()
				.build();
		FeedImage feedImage4 = FeedImage.builder()
				.build();

		Feed feed1 = new Feed();
		feed1.addFeedImage(feedImage1);
		feed1.addFeedImage(feedImage2);
		Feed feed2 = new Feed();
		feed2.addFeedImage(feedImage3);
		feed2.addFeedImage(feedImage4);

		feedRepository.saveAll(List.of(feed1, feed2));
		feedImageRepository.saveAll(List.of(feedImage1, feedImage2, feedImage3, feedImage4));

	    // when
		feedImageRepository.deleteFeedImageByFeed_Id(feed1.getId());
		feedImageRepository.deleteFeedImageByFeed_Id(feed2.getId());

		List<FeedImage> result1 = feedImageRepository.findByFeed_Id(feed1.getId());
		List<FeedImage> result2 = feedImageRepository.findByFeed_Id(feed2.getId());

		for (FeedImage feedImage : result1) {
			System.out.println("feedImage = " + feedImage);
		}

	    // then
		assertThat(result1.isEmpty()).isTrue();
		assertThat(result2.isEmpty()).isTrue();
	}
}
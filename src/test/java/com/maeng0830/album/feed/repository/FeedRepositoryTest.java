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

	@DisplayName("피드 상태 및 피드 작성자와 일치하는 피드를 조회한다")
	@Test
	void findByStatusAndCreatedBy() {
		// given
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

		feedRepository.saveAll(List.of(feed1, feed2, feed3, feed4, feed5, feed6));

		// when
		List<Feed> result1 = feedRepository.findByStatusAndCreatedBy(List.of(NORMAL, ACCUSE),
				List.of("memberA"));
		List<Feed> result2 = feedRepository.findByStatusAndCreatedBy(List.of(ACCUSE, DELETE),
				List.of("memberA", "memberB"));

		// then
		assertThat(result1).hasSize(2)
				.extracting("status", "createdBy")
				.containsExactlyInAnyOrder(
						tuple(NORMAL, "memberA"),
						tuple(ACCUSE, "memberA")
				);

		assertThat(result2).hasSize(4)
				.extracting("status", "createdBy")
				.containsExactlyInAnyOrder(
						tuple(ACCUSE, "memberA"),
						tuple(ACCUSE, "memberB"),
						tuple(DELETE, "memberA"),
						tuple(DELETE, "memberB")
				);
	}

	@DisplayName("피드 상태와 일치하는 피드 목록을 피드이미지와 함께 조회한다.")
	@Test
	void findFetchJoinByStatusNot() {
		// given
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

		FeedImage feedImage1 = FeedImage.builder()
				.image(image1)
				.build();
		FeedImage feedImage2 = FeedImage.builder()
				.image(image2)
				.build();

		Feed feed1 = new Feed();
		feed1.changeStatus(NORMAL);
		feed1.addFeedImage(feedImage1);
		Feed feed2 = new Feed();
		feed2.changeStatus(ACCUSE);
		feed2.addFeedImage(feedImage2);

		feedRepository.saveAll(List.of(feed1, feed2));
		feedImageRepository.saveAll(List.of(feedImage1, feedImage2));

		// when
		List<Feed> result1 = feedRepository.findFetchJoinByStatus(
				List.of(feed1.getStatus()));

		List<Feed> result2 = feedRepository.findFetchJoinByStatus(
				List.of(feed2.getStatus()));

		List<Feed> result3 = feedRepository.findFetchJoinByStatus(
				List.of(feed1.getStatus(), feed2.getStatus()));

		// then
		// result1
		assertThat(result1).hasSize(1)
				.extracting("status")
				.containsExactlyInAnyOrder(NORMAL);
		assertThat(result1.get(0).getFeedImages()).hasSize(1)
				.extracting("image.imageOriginalName", "image.imageStoreName", "image.imagePath")
				.containsExactlyInAnyOrder(
						tuple(image1.getImageOriginalName(), image1.getImageStoreName(), image1.getImagePath())
				);

		// result2
		assertThat(result2).hasSize(1)
				.extracting("status")
				.containsExactlyInAnyOrder(ACCUSE);
		assertThat(result2.get(0).getFeedImages()).hasSize(1)
				.extracting("image.imageOriginalName", "image.imageStoreName", "image.imagePath")
				.containsExactlyInAnyOrder(
						tuple(image2.getImageOriginalName(), image2.getImageStoreName(), image2.getImagePath())
				);

		// result3
		assertThat(result3).hasSize(2)
				.extracting("status")
				.containsExactlyInAnyOrder(ACCUSE, NORMAL);
		assertThat(result3.get(0).getFeedImages()).hasSize(1)
				.extracting("image.imageOriginalName", "image.imageStoreName", "image.imagePath")
				.containsExactlyInAnyOrder(
						tuple(image1.getImageOriginalName(), image1.getImageStoreName(), image1.getImagePath())
				);
		assertThat(result3.get(1).getFeedImages()).hasSize(1)
				.extracting("image.imageOriginalName", "image.imageStoreName", "image.imagePath")
				.containsExactlyInAnyOrder(
						tuple(image2.getImageOriginalName(), image2.getImageStoreName(), image2.getImagePath())
				);
	}
}
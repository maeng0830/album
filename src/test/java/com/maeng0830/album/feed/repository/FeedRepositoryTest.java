package com.maeng0830.album.feed.repository;

import static com.maeng0830.album.feed.domain.FeedStatus.*;
import static org.assertj.core.api.Assertions.*;

import com.maeng0830.album.common.model.image.Image;
import com.maeng0830.album.feed.domain.Feed;
import com.maeng0830.album.feed.domain.FeedImage;
import java.util.List;
import java.util.stream.Stream;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort.Direction;
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

		// Paging 세팅
		PageRequest pageRequest = PageRequest.of(0, 20);

		// when
		Page<Feed> result1 = feedRepository.searchByStatusAndCreatedBy(List.of(NORMAL, ACCUSE),
				List.of("memberA"), pageRequest);
		Page<Feed> result2 = feedRepository.searchByStatusAndCreatedBy(List.of(ACCUSE, DELETE),
				List.of("memberA", "memberB"), pageRequest);

		// then
		assertThat(result1.getContent()).hasSize(2)
				.extracting("status", "createdBy", "feedImages")
				.containsExactlyInAnyOrder(
						tuple(NORMAL, "memberA", feed1.getFeedImages()),
						tuple(ACCUSE, "memberA", feed2.getFeedImages())
				);

		assertThat(result2.getContent()).hasSize(4)
				.extracting("status", "createdBy", "feedImages")
				.containsExactlyInAnyOrder(
						tuple(ACCUSE, "memberA", feed2.getFeedImages()),
						tuple(DELETE, "memberA", feed3.getFeedImages()),
						tuple(ACCUSE, "memberB", feed5.getFeedImages()),
						tuple(DELETE, "memberB", feed6.getFeedImages())
				);
	}

	@DisplayName("주어진 정렬 기준에 따라, 피드 상태 및 피드 작성자와 일치하는 피드와 피드이미지를 함께 조회한다")
	@MethodSource("providePageRequest")
	@ParameterizedTest
	void searchByStatusAndCreatedBy_Sorted(PageRequest pageRequest) {
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
				.commentCount(1)
				.likeCount(6)
				.build();
		Feed feed2 = Feed.builder()
				.status(ACCUSE)
				.createdBy("memberA")
				.commentCount(2)
				.likeCount(5)
				.build();
		Feed feed3 = Feed.builder()
				.status(DELETE)
				.createdBy("memberA")
				.commentCount(3)
				.likeCount(4)
				.build();
		Feed feed4 = Feed.builder()
				.status(NORMAL)
				.createdBy("memberB")
				.commentCount(4)
				.likeCount(3)
				.build();
		Feed feed5 = Feed.builder()
				.status(ACCUSE)
				.createdBy("memberB")
				.commentCount(5)
				.likeCount(2)
				.build();
		Feed feed6 = Feed.builder()
				.status(DELETE)
				.createdBy("memberB")
				.commentCount(6)
				.likeCount(1)
				.build();

		feed1.addFeedImage(feedImage1);
		feed5.addFeedImage(feedImage2);
		feedRepository.saveAll(List.of(feed1, feed2, feed3, feed4, feed5, feed6));
		feedImageRepository.saveAll(List.of(feedImage1, feedImage2));

		// when
		Page<Feed> result = feedRepository.searchByStatusAndCreatedBy(List.of(NORMAL, ACCUSE, DELETE),
				List.of("memberA", "memberB"), pageRequest);

		// then
		assertThat(result.getContent()).hasSize(6)
				.extracting("likeCount", "commentCount")
				.containsExactly(
						tuple(1, 6),
						tuple(2, 5),
						tuple(3, 4),
						tuple(4, 3),
						tuple(5, 2),
						tuple(6, 1)
				);
	}

	private static Stream<Arguments> providePageRequest() {
		return Stream.of(
				Arguments.of(PageRequest.of(0, 20, Direction.ASC, "likeCount")),
				Arguments.of(PageRequest.of(0, 20, Direction.DESC, "commentCount"))
		);
	}
}
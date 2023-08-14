package com.maeng0830.album.feed.repository;

import static com.maeng0830.album.feed.domain.FeedStatus.ACCUSE;
import static com.maeng0830.album.feed.domain.FeedStatus.DELETE;
import static com.maeng0830.album.feed.domain.FeedStatus.NORMAL;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.tuple;

import com.maeng0830.album.common.filedir.FileDir;
import com.maeng0830.album.common.image.DefaultImage;
import com.maeng0830.album.common.model.image.Image;
import com.maeng0830.album.feed.domain.Feed;
import com.maeng0830.album.feed.domain.FeedImage;
import com.maeng0830.album.feed.domain.FeedStatus;
import com.maeng0830.album.member.domain.Member;
import com.maeng0830.album.member.repository.MemberRepository;
import com.maeng0830.album.support.RepositoryTestSupport;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Stream;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort.Direction;

class FeedRepositoryTest extends RepositoryTestSupport {

	@Autowired
	private FeedRepository feedRepository;

	@Autowired
	private FeedImageRepository feedImageRepository;

	@Autowired
	private MemberRepository memberRepository;

	@Autowired
	private FileDir fileDir;

	@Autowired
	private DefaultImage defaultImage;

	@DisplayName("피드 상태 및 피드 작성자와 일치하는 피드와 피드이미지를 함께 조회한다.")
	@Test
	void searchByCreatedBy() {
		// given
		// Member 세팅
		Member memberA = Member.builder()
				.username("memberA")
				.build();
		Member memberB = Member.builder()
				.username("memberB")
				.build();
		memberRepository.saveAll(List.of(memberA, memberB));

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
				.member(memberA)
				.status(NORMAL)
				.build();
		Feed feed2 = Feed.builder()
				.member(memberA)
				.status(ACCUSE)
				.build();
		Feed feed3 = Feed.builder()
				.member(memberA)
				.status(DELETE)
				.build();
		Feed feed4 = Feed.builder()
				.member(memberB)
				.status(NORMAL)
				.build();
		Feed feed5 = Feed.builder()
				.member(memberB)
				.status(ACCUSE)
				.build();
		Feed feed6 = Feed.builder()
				.member(memberB)
				.status(DELETE)
				.build();

		feed1.addFeedImage(feedImage1);
		feed5.addFeedImage(feedImage2);
		feedRepository.saveAll(List.of(feed1, feed2, feed3, feed4, feed5, feed6));
		feedImageRepository.saveAll(List.of(feedImage1, feedImage2));

		// Paging 세팅
		PageRequest pageRequest = PageRequest.of(0, 20);

		// when
		Page<Feed> result1 = feedRepository.searchByCreatedBy(List.of(NORMAL, ACCUSE),
				List.of("memberA"), pageRequest);
		Page<Feed> result2 = feedRepository.searchByCreatedBy(List.of(ACCUSE, DELETE),
				List.of("memberA", "memberB"), pageRequest);

		// then
		assertThat(result1.getContent()).hasSize(2)
				.extracting("status", "member.username", "feedImages")
				.containsExactlyInAnyOrder(
						tuple(NORMAL, "memberA", feed1.getFeedImages()),
						tuple(ACCUSE, "memberA", feed2.getFeedImages())
				);

		assertThat(result2.getContent()).hasSize(4)
				.extracting("status", "member.username", "feedImages")
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
	void searchByCreatedBy_Sorted(PageRequest pageRequest) {
		// given
		// Member 세팅
		Member memberA = Member.builder()
				.username("memberA")
				.build();
		Member memberB = Member.builder()
				.username("memberB")
				.build();
		memberRepository.saveAll(List.of(memberA, memberB));

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
				.member(memberA)
				.status(NORMAL)
				.commentCount(1)
				.hits(6)
				.build();
		Feed feed2 = Feed.builder()
				.member(memberA)
				.status(ACCUSE)
				.commentCount(2)
				.hits(5)
				.build();
		Feed feed3 = Feed.builder()
				.member(memberA)
				.status(DELETE)
				.commentCount(3)
				.hits(4)
				.build();
		Feed feed4 = Feed.builder()
				.member(memberB)
				.status(NORMAL)
				.commentCount(4)
				.hits(3)
				.build();
		Feed feed5 = Feed.builder()
				.member(memberB)
				.status(ACCUSE)
				.commentCount(5)
				.hits(2)
				.build();
		Feed feed6 = Feed.builder()
				.member(memberB)
				.status(DELETE)
				.commentCount(6)
				.hits(1)
				.build();

		feed1.addFeedImage(feedImage1);
		feed5.addFeedImage(feedImage2);
		feedRepository.saveAll(List.of(feed1, feed2, feed3, feed4, feed5, feed6));
		feedImageRepository.saveAll(List.of(feedImage1, feedImage2));

		// when
		Page<Feed> result = feedRepository.searchByCreatedBy(List.of(NORMAL, ACCUSE, DELETE),
				List.of("memberA", "memberB"), pageRequest);

		// then
		assertThat(result.getContent()).hasSize(6)
				.extracting("hits", "commentCount")
				.containsExactly(
						tuple(1, 6),
						tuple(2, 5),
						tuple(3, 4),
						tuple(4, 3),
						tuple(5, 2),
						tuple(6, 1)
				);
	}

	@DisplayName("피드 상태와 일치하는 피드와 피드이미지를 함께 조회한다."
			+ "searchText가 null인 경우, 모든 피드를 조회한다."
			+ "null이 아닌 경우, searchText와 피드 작성자의 username 또는 nickname이 전방 일치하는 피드를 조회한다.")
	@MethodSource("argumentsForSearchBySearchText")
	@ParameterizedTest
	void searchBySearchText(List<FeedStatus> statuses, String searchText, List<String> usernames,
							List<String> nicknames, int size) {
		// given
		// 멤버 세팅
		Member member1 = Member.builder()
				.username("username11")
				.nickname("nickname11")
				.build();

		Member member2 = Member.builder()
				.username("username22")
				.nickname("nickname22")
				.build();

		memberRepository.saveAll(List.of(member1, member2));

		// 피드 세팅
		List<Feed> feeds = new ArrayList<>();
		for (int i = 0; i < 3; i++) {
			if (i == 0) {
				Feed feedForMember1 = Feed.builder()
						.member(member1)
						.status(NORMAL)
						.build();
				Feed feedForMember2 = Feed.builder()
						.member(member2)
						.status(NORMAL)
						.build();
				feeds.add(feedForMember1);
				feeds.add(feedForMember2);
			} else if (i == 1) {
				Feed feedForMember1 = Feed.builder()
						.member(member1)
						.status(ACCUSE)
						.build();
				Feed feedForMember2 = Feed.builder()
						.member(member2)
						.status(ACCUSE)
						.build();
				feeds.add(feedForMember1);
				feeds.add(feedForMember2);
			} else {
				Feed feedForMember1 = Feed.builder()
						.member(member1)
						.status(DELETE)
						.build();
				Feed feedForMember2 = Feed.builder()
						.member(member2)
						.status(DELETE)
						.build();
				feeds.add(feedForMember1);
				feeds.add(feedForMember2);
			}
		}

		feedRepository.saveAll(feeds);

		// 피드 이미지 세팅
		Image defaultFeedImage = Image.createDefaultImage(fileDir, defaultImage.getFeedImage());

		for (Feed feed : feeds) {
			FeedImage feedImage = FeedImage.builder()
					.image(defaultFeedImage)
					.build();

			feed.addFeedImage(feedImage);

			feedImageRepository.save(feedImage);
		}

		PageRequest pageRequest = PageRequest.of(0, 20);

		// when
		Page<Feed> result = feedRepository.searchBySearchText(statuses, searchText, pageRequest);

		// then
		if (searchText != null) {
			assertThat(result.getContent()).hasSize(size)
					.extracting("status", "member.username", "member.nickname")
					.containsExactlyInAnyOrder(
							tuple(statuses.get(0), usernames.get(0), nicknames.get(0)));

			assertThat(result.getContent().get(0).getFeedImages().get(0))
					.extracting("image")
					.usingRecursiveComparison().isEqualTo(defaultFeedImage);
		} else {
			assertThat(result.getContent()).hasSize(size)
					.extracting("status", "member.username", "member.nickname")
					.containsExactlyInAnyOrder(
							tuple(statuses.get(0), usernames.get(0), nicknames.get(0)),
							tuple(statuses.get(0), usernames.get(1), nicknames.get(1))
					);
			assertThat(result.getContent().get(0).getFeedImages().get(0))
					.extracting("image")
					.usingRecursiveComparison().isEqualTo(defaultFeedImage);
			assertThat(result.getContent().get(1).getFeedImages().get(0))
					.extracting("image")
					.usingRecursiveComparison().isEqualTo(defaultFeedImage);
		}
	}

	private static Stream<Arguments> providePageRequest() {
		return Stream.of(
				Arguments.of(PageRequest.of(0, 20, Direction.ASC, "hits")),
				Arguments.of(PageRequest.of(0, 20, Direction.DESC, "commentCount"))
		);
	}

	private static Stream<Arguments> argumentsForSearchBySearchText() {
		return Stream.of(
				Arguments.of(List.of(NORMAL), "username1", List.of("username11"),
						List.of("nickname11"), 1),
				Arguments.of(List.of(NORMAL), "nickname1", List.of("username11"),
						List.of("nickname11"), 1),
				Arguments.of(List.of(NORMAL), "username2", List.of("username22"),
						List.of("nickname22"), 1),
				Arguments.of(List.of(NORMAL), "nickname2", List.of("username22"),
						List.of("nickname22"), 1),
				Arguments.of(List.of(NORMAL), null, List.of("username11", "username22"),
						List.of("nickname11", "nickname22"), 2),
				Arguments.of(List.of(ACCUSE), "username1", List.of("username11"),
						List.of("nickname11"), 1),
				Arguments.of(List.of(ACCUSE), "nickname1", List.of("username11"),
						List.of("nickname11"), 1),
				Arguments.of(List.of(ACCUSE), "username2", List.of("username22"),
						List.of("nickname22"), 1),
				Arguments.of(List.of(ACCUSE), "nickname2", List.of("username22"),
						List.of("nickname22"), 1),
				Arguments.of(List.of(ACCUSE), null, List.of("username11", "username22"),
						List.of("nickname11", "nickname22"), 2),
				Arguments.of(List.of(DELETE), "username1", List.of("username11"),
						List.of("nickname11"), 1),
				Arguments.of(List.of(DELETE), "nickname1", List.of("username11"),
						List.of("nickname11"), 1),
				Arguments.of(List.of(DELETE), "username2", List.of("username22"),
						List.of("nickname22"), 1),
				Arguments.of(List.of(DELETE), "nickname2", List.of("username22"),
						List.of("nickname22"), 1),
				Arguments.of(List.of(DELETE), null, List.of("username11", "username22"),
						List.of("nickname11", "nickname22"), 2)
		);
	}
}
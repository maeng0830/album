package com.maeng0830.album.comment.repository;

import static com.maeng0830.album.comment.domain.CommentStatus.ACCUSE;
import static com.maeng0830.album.comment.domain.CommentStatus.DELETE;
import static com.maeng0830.album.comment.domain.CommentStatus.NORMAL;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.tuple;

import com.maeng0830.album.comment.domain.Comment;
import com.maeng0830.album.feed.domain.Feed;
import com.maeng0830.album.feed.repository.FeedRepository;
import java.util.List;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.annotation.Transactional;

@ActiveProfiles("test")
@Transactional
@SpringBootTest
class CommentRepositoryTest {

	@Autowired
	private CommentRepository commentRepository;
	@Autowired
	private FeedRepository feedRepository;

	@DisplayName("주어진 피드 아이디 및 피드 상태에 해당하는 코멘트를 조회한다.")
	@Test
	void findFetchJoinAll() {
		// given
		// Feed 세팅
		Feed feed1 = Feed.builder()
				.title("feed1Title")
				.build();
		Feed feed2 = Feed.builder()
				.title("feed2Title")
				.build();
		feedRepository.saveAll(List.of(feed1, feed2));

		// Comment 세팅
		Comment feed1_normal = Comment.builder()
				.status(NORMAL)
				.feed(feed1)
				.build();
		Comment feed1_accuse = Comment.builder()
				.status(ACCUSE)
				.feed(feed1)
				.build();
		Comment feed1_delete = Comment.builder()
				.status(DELETE)
				.feed(feed1)
				.build();
		Comment feed2_normal = Comment.builder()
				.status(NORMAL)
				.feed(feed2)
				.build();
		Comment feed2_accuse = Comment.builder()
				.status(ACCUSE)
				.feed(feed2)
				.build();
		Comment feed2_delete = Comment.builder()
				.status(DELETE)
				.feed(feed2)
				.build();
		commentRepository.saveAll(
				List.of(feed1_normal, feed1_accuse, feed1_delete, feed2_normal, feed2_accuse,
						feed2_delete));

		// when
		List<Comment> result1 = commentRepository.findFetchJoinAll(feed1.getId(),
				List.of(NORMAL));
		List<Comment> result2 = commentRepository.findFetchJoinAll(feed1.getId(),
				List.of(NORMAL, ACCUSE));
		List<Comment> result3 = commentRepository.findFetchJoinAll(feed1.getId(),
				List.of(NORMAL, ACCUSE, DELETE));
		List<Comment> result4 = commentRepository.findFetchJoinAll(feed2.getId(),
				List.of(NORMAL, ACCUSE, DELETE));

		// then
		// result1
		assertThat(result1).hasSize(1)
				.extracting("status", "feed")
				.containsExactlyInAnyOrder(
						tuple(NORMAL, feed1)
				);
		// result2
		assertThat(result2).hasSize(2)
				.extracting("status", "feed")
				.containsExactlyInAnyOrder(
						tuple(NORMAL, feed1),
						tuple(ACCUSE, feed1)
				);
		// result3
		assertThat(result3).hasSize(3)
				.extracting("status", "feed")
				.containsExactlyInAnyOrder(
						tuple(NORMAL, feed1),
						tuple(ACCUSE, feed1),
						tuple(DELETE, feed1)
				);
		// result4
		assertThat(result4).hasSize(3)
				.extracting("status", "feed")
				.containsExactlyInAnyOrder(
						tuple(NORMAL, feed2),
						tuple(ACCUSE, feed2),
						tuple(DELETE, feed2)
				);
	}
}
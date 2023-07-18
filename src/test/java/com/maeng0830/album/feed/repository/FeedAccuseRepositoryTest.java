package com.maeng0830.album.feed.repository;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.tuple;

import com.maeng0830.album.feed.domain.Feed;
import com.maeng0830.album.feed.domain.FeedAccuse;
import com.maeng0830.album.member.domain.Member;
import com.maeng0830.album.member.repository.MemberRepository;
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
class FeedAccuseRepositoryTest {

	@Autowired
	private FeedAccuseRepository feedAccuseRepository;

	@Autowired
	private FeedRepository feedRepository;

	@Autowired
	private MemberRepository memberRepository;

	@DisplayName("주어진 피드아이디와 일치하는 피드신고 목록을 조회한다.")
	@Test
	void findFeedAccuseByFeed_Id() {
	    // given
		// 멤버 세팅
		Member member1 = Member.builder()
				.build();
		Member member2 = Member.builder()
				.build();
		memberRepository.saveAll(List.of(member1, member2));

		// 피드 세팅
		Feed feed1 = Feed.builder()
				.member(member1)
				.build();
		Feed feed2 = Feed.builder()
				.member(member2)
				.build();
		feedRepository.saveAll(List.of(feed1, feed2));

		// 피드신고 세팅
		for (int i = 0; i < 10; i++) {
			if (i % 2 == 0) {
				FeedAccuse feedAccuse = FeedAccuse.builder()
						.member(member1)
						.feed(feed2)
						.build();
				feedAccuseRepository.save(feedAccuse);
			} else {
				FeedAccuse feedAccuse = FeedAccuse.builder()
						.member(member2)
						.feed(feed1)
						.build();
				feedAccuseRepository.save(feedAccuse);
			}
		}

		// when
		List<FeedAccuse> result1 = feedAccuseRepository.findFeedAccuseByFeed_Id(
				feed1.getId());
		List<FeedAccuse> result2 = feedAccuseRepository.findFeedAccuseByFeed_Id(
				feed2.getId());

		// then
		assertThat(result1).hasSize(5)
				.extracting("member", "feed")
				.containsExactlyInAnyOrder(
						tuple(member2, feed1),
						tuple(member2, feed1),
						tuple(member2, feed1),
						tuple(member2, feed1),
						tuple(member2, feed1)
				);

		assertThat(result2).hasSize(5)
				.extracting("member", "feed")
				.containsExactlyInAnyOrder(
						tuple(member1, feed2),
						tuple(member1, feed2),
						tuple(member1, feed2),
						tuple(member1, feed2),
						tuple(member1, feed2)
				);
	}
}
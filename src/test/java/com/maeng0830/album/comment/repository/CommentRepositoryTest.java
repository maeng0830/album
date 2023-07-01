package com.maeng0830.album.comment.repository;

import static com.maeng0830.album.comment.domain.CommentStatus.ACCUSE;
import static com.maeng0830.album.comment.domain.CommentStatus.DELETE;
import static com.maeng0830.album.comment.domain.CommentStatus.NORMAL;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.tuple;

import com.maeng0830.album.comment.domain.Comment;
import com.maeng0830.album.comment.domain.CommentStatus;
import com.maeng0830.album.feed.domain.Feed;
import com.maeng0830.album.feed.repository.FeedRepository;
import com.maeng0830.album.member.domain.Member;
import com.maeng0830.album.member.repository.MemberRepository;
import java.util.List;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.domain.PageRequest;
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
	@Autowired
	private MemberRepository memberRepository;

	@DisplayName("피드 아이디에 해당하는 그룹 댓글을 조회할 수 있다.")
	@Test
	void findGroupComment() {
	    // given
		// Member 세팅
		Member member = Member.builder()
				.username("testMember")
				.build();
		memberRepository.save(member);

		// Feed 세팅
		Feed feed = Feed.builder()
				.title("testFeed")
				.build();
		feedRepository.save(feed);

		// comment 세팅
		Comment groupCommentNormal = Comment.builder()
				.member(member)
				.content("groupCommentNormal")
				.status(NORMAL)
				.feed(feed)
				.build();
		groupCommentNormal.saveGroup(groupCommentNormal);
		groupCommentNormal.saveParent(groupCommentNormal);

		Comment groupCommentAccuse = Comment.builder()
				.member(member)
				.content("groupCommentAccuse")
				.status(ACCUSE)
				.feed(feed)
				.build();
		groupCommentAccuse.saveGroup(groupCommentAccuse);
		groupCommentAccuse.saveParent(groupCommentAccuse);

		Comment groupCommentDelete = Comment.builder()
				.member(member)
				.content("groupCommentDelete")
				.status(DELETE)
				.feed(feed)
				.build();
		groupCommentDelete.saveGroup(groupCommentDelete);
		groupCommentDelete.saveParent(groupCommentDelete);

		Comment basicCommentNormal_GN = Comment.builder()
				.member(member)
				.content("basicCommentNormal_GN")
				.status(NORMAL)
				.feed(feed)
				.build();
		basicCommentNormal_GN.saveGroup(groupCommentNormal);
		basicCommentNormal_GN.saveParent(groupCommentNormal);

		Comment basicCommentAccuse_GN = Comment.builder()
				.member(member)
				.content("basicCommentAccuse_GN")
				.status(ACCUSE)
				.feed(feed)
				.build();
		basicCommentAccuse_GN.saveGroup(groupCommentNormal);
		basicCommentAccuse_GN.saveParent(groupCommentNormal);

		Comment basicCommentDelete_BN = Comment.builder()
				.member(member)
				.content("basicCommentDelete_BN")
				.status(DELETE)
				.feed(feed)
				.build();
		basicCommentDelete_BN.saveGroup(groupCommentNormal);
		basicCommentDelete_BN.saveParent(basicCommentNormal_GN);

		commentRepository.saveAll(
				List.of(groupCommentNormal, groupCommentAccuse, groupCommentDelete,
						basicCommentNormal_GN, basicCommentAccuse_GN, basicCommentDelete_BN));

		// paging 세팅
		PageRequest pageRequest = PageRequest.of(0, 20);
		List<CommentStatus> status = List.of(NORMAL, ACCUSE, DELETE);

		// when
		List<Comment> groupComment = commentRepository.findGroupComment(feed.getId(), status,
				pageRequest);

		// then
		assertThat(groupComment).hasSize(3)
				.extracting("content")
				.containsExactly(
						groupCommentNormal.getContent(),
						groupCommentAccuse.getContent(),
						groupCommentDelete.getContent()
				);
	}

	@DisplayName("피드 아이디에 해당하는 베이직 댓글을 조회할 수 있다.")
	@Test
	void findBasicComment() {
		// given
		// Member 세팅
		Member member = Member.builder()
				.username("testMember")
				.build();
		memberRepository.save(member);

		// Feed 세팅
		Feed feed = Feed.builder()
				.title("testFeed")
				.build();
		feedRepository.save(feed);

		// comment 세팅
		Comment groupCommentNormal = Comment.builder()
				.member(member)
				.content("groupCommentNormal")
				.status(NORMAL)
				.feed(feed)
				.build();
		groupCommentNormal.saveGroup(groupCommentNormal);
		groupCommentNormal.saveParent(groupCommentNormal);

		Comment groupCommentAccuse = Comment.builder()
				.member(member)
				.content("groupCommentAccuse")
				.status(ACCUSE)
				.feed(feed)
				.build();
		groupCommentAccuse.saveGroup(groupCommentAccuse);
		groupCommentAccuse.saveParent(groupCommentAccuse);

		Comment groupCommentDelete = Comment.builder()
				.member(member)
				.content("groupCommentDelete")
				.status(DELETE)
				.feed(feed)
				.build();
		groupCommentDelete.saveGroup(groupCommentDelete);
		groupCommentDelete.saveParent(groupCommentDelete);

		Comment basicCommentNormal_GN = Comment.builder()
				.member(member)
				.content("basicCommentNormal_GN")
				.status(NORMAL)
				.feed(feed)
				.build();
		basicCommentNormal_GN.saveGroup(groupCommentNormal);
		basicCommentNormal_GN.saveParent(groupCommentNormal);

		Comment basicCommentAccuse_GN = Comment.builder()
				.member(member)
				.content("basicCommentAccuse_GN")
				.status(ACCUSE)
				.feed(feed)
				.build();
		basicCommentAccuse_GN.saveGroup(groupCommentNormal);
		basicCommentAccuse_GN.saveParent(groupCommentNormal);

		Comment basicCommentDelete_BN = Comment.builder()
				.member(member)
				.content("basicCommentDelete_BN")
				.status(DELETE)
				.feed(feed)
				.build();
		basicCommentDelete_BN.saveGroup(groupCommentNormal);
		basicCommentDelete_BN.saveParent(basicCommentNormal_GN);

		commentRepository.saveAll(
				List.of(groupCommentNormal, groupCommentAccuse, groupCommentDelete,
						basicCommentNormal_GN, basicCommentAccuse_GN, basicCommentDelete_BN));

		// paging 세팅
		List<CommentStatus> status = List.of(NORMAL, ACCUSE, DELETE);

		// when
		List<Comment> basicComment = commentRepository.findBasicComment(feed.getId(), status, 0L, 2L);

		// then
		assertThat(basicComment).hasSize(3)
				.extracting("content")
				.containsExactly(
						basicCommentNormal_GN.getContent(),
						basicCommentAccuse_GN.getContent(),
						basicCommentDelete_BN.getContent()
				);
	}
}
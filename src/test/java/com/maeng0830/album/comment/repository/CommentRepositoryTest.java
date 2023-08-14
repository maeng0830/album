package com.maeng0830.album.comment.repository;

import static com.maeng0830.album.comment.domain.CommentStatus.ACCUSE;
import static com.maeng0830.album.comment.domain.CommentStatus.DELETE;
import static com.maeng0830.album.comment.domain.CommentStatus.NORMAL;
import static org.assertj.core.api.Assertions.assertThat;

import com.maeng0830.album.comment.domain.Comment;
import com.maeng0830.album.comment.domain.CommentAccuse;
import com.maeng0830.album.comment.domain.CommentStatus;
import com.maeng0830.album.feed.domain.Feed;
import com.maeng0830.album.feed.repository.FeedRepository;
import com.maeng0830.album.member.domain.Member;
import com.maeng0830.album.member.repository.MemberRepository;
import com.maeng0830.album.support.RepositoryTestSupport;
import java.util.ArrayList;
import java.util.List;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;

class CommentRepositoryTest extends RepositoryTestSupport {

	@Autowired
	private CommentRepository commentRepository;
	@Autowired
	private CommentAccuseRepository commentAccuseRepository;
	@Autowired
	private FeedRepository feedRepository;
	@Autowired
	private MemberRepository memberRepository;

	@DisplayName("피드아이디에 해당하는 그룹댓글을 조회할 수 있다.")
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

	@DisplayName("피드아이디 및 그룹댓글 범위에 해당하는 베이직댓글을 조회할 수 있다.")
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
		List<Comment> basicComment = commentRepository.findBasicComment(feed.getId(), status,
				groupCommentNormal.getId(),
				groupCommentDelete.getId());

		// then
		assertThat(basicComment).hasSize(3)
				.extracting("content")
				.containsExactly(
						basicCommentNormal_GN.getContent(),
						basicCommentAccuse_GN.getContent(),
						basicCommentDelete_BN.getContent()
				);
	}

	@DisplayName("searchText와 작성자의 username 또는 nickname이 전방 일치하는 댓글 목록을 조회할 수 있다."
			+ "searchText가 null인 경우, 모든 댓글 목록을 조회한다.")
	@CsvSource(value = {",", "username1", "nickname1", "username2", "nickname2"})
	@ParameterizedTest
	void searchBySearchText(String searchText) {
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

		// 댓글 세팅
		List<Comment> comments = new ArrayList<>();
		for (int i = 0; i < 10; i++) {
			Comment comment;

			if (i <= 4) {
				comment = Comment.builder()
						.member(member1)
						.build();
			} else {
				comment = Comment.builder()
						.member(member2)
						.build();
			}

			comments.add(comment);
		}

		commentRepository.saveAll(comments);

		PageRequest pageRequest = PageRequest.of(0, 20);

		// when
		Page<Comment> result = commentRepository.searchBySearchText(searchText, pageRequest);

		// then
		if (searchText == null) {
			assertThat(result.getContent()).hasSize(10)
					.extracting("member")
					.containsExactlyInAnyOrder(
							member1, member1, member1, member1, member1,
							member2, member2, member2, member2, member2
					);
		} else if (searchText.equals("username1") || searchText.equals("nickname1")) {
			assertThat(result.getContent()).hasSize(5)
					.extracting("member")
					.containsExactlyInAnyOrder(
							member1, member1, member1, member1, member1
					);
		} else if (searchText.equals("username2") || searchText.equals("nickname2")) {
			assertThat(result.getContent()).hasSize(5)
					.extracting("member")
					.containsExactlyInAnyOrder(
							member2, member2, member2, member2, member2
					);
		}

	}

	@DisplayName("주어진 댓글 아이디와 일치하는 댓글의 신고 목록을 조회할 수 있다.")
	@Test
	void findCommentAccuseByComment_Id() {
		// given
		// 댓글 세팅
		Comment comment1 = Comment.builder()
				.build();
		Comment comment2 = Comment.builder()
				.build();
		commentRepository.saveAll(List.of(comment1, comment2));

		// 댓글 신고 세팅
		List<CommentAccuse> commentAccuses = new ArrayList<>();
		for (int i = 0; i < 10; i++) {
			CommentAccuse commentAccuse;

			if (i <= 4) {
				commentAccuse = CommentAccuse.builder()
						.comment(comment1)
						.build();
			} else {
				commentAccuse = CommentAccuse.builder()
						.comment(comment2)
						.build();
			}

			commentAccuses.add(commentAccuse);
		}

		commentAccuseRepository.saveAll(commentAccuses);

		// when
		List<CommentAccuse> result1 = commentAccuseRepository.findCommentAccuseByComment_Id(
				comment1.getId());
		List<CommentAccuse> result2 = commentAccuseRepository.findCommentAccuseByComment_Id(
				comment2.getId());

		// then
		assertThat(result1).hasSize(5)
				.extracting("comment")
				.containsExactlyInAnyOrder(
						comment1, comment1, comment1, comment1, comment1
				);
		assertThat(result2).hasSize(5)
				.extracting("comment")
				.containsExactlyInAnyOrder(
						comment2, comment2, comment2, comment2, comment2
				);
	}
}
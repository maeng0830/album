package com.maeng0830.album.comment.service;

import static com.maeng0830.album.comment.domain.CommentStatus.ACCUSE;
import static com.maeng0830.album.comment.domain.CommentStatus.DELETE;
import static com.maeng0830.album.comment.domain.CommentStatus.NORMAL;
import static com.maeng0830.album.member.exception.MemberExceptionCode.NO_AUTHORITY;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.assertj.core.api.Assertions.tuple;

import com.maeng0830.album.comment.domain.Comment;
import com.maeng0830.album.comment.domain.CommentStatus;
import com.maeng0830.album.comment.dto.CommentAccuseDto;
import com.maeng0830.album.comment.dto.request.CommentAccuseForm;
import com.maeng0830.album.comment.dto.request.CommentModifiedForm;
import com.maeng0830.album.comment.dto.request.CommentPostForm;
import com.maeng0830.album.comment.dto.response.BasicComment;
import com.maeng0830.album.comment.dto.response.GroupComment;
import com.maeng0830.album.comment.repository.CommentRepository;
import com.maeng0830.album.common.exception.AlbumException;
import com.maeng0830.album.feed.domain.Feed;
import com.maeng0830.album.feed.repository.FeedRepository;
import com.maeng0830.album.member.domain.Member;
import com.maeng0830.album.member.dto.MemberDto;
import com.maeng0830.album.member.repository.MemberRepository;
import java.util.List;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.domain.PageRequest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.annotation.Transactional;

@ActiveProfiles("test")
@Transactional
@SpringBootTest
class CommentServiceTest {

	@Autowired
	private CommentService commentService;
	@Autowired
	private FeedRepository feedRepository;
	@Autowired
	private CommentRepository commentRepository;
	@Autowired
	private MemberRepository memberRepository;

	@DisplayName("피드 아이디에 해당하는 댓글들을 조회할 수 있다.")
	@Test
	void getFeedComments() {
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

		// when
		List<GroupComment> groupComments = commentService.getFeedComments(feed.getId(), pageRequest);

		// then
		assertThat(groupComments).hasSize(3)
				.extracting("content", "status", "groupId", "parentId")
				.containsExactlyInAnyOrder(
						tuple(groupCommentNormal.getContent(), groupCommentNormal.getStatus(),
								groupCommentNormal.getId(), groupCommentNormal.getId()),
						tuple(groupCommentAccuse.getContent(), groupCommentAccuse.getStatus(),
								groupCommentAccuse.getId(), groupCommentAccuse.getId()),
						tuple(groupCommentDelete.getContent(), groupCommentDelete.getStatus(),
								groupCommentDelete.getId(), groupCommentDelete.getId())
				);

		assertThat(groupComments.get(0).getBasicComments()).hasSize(3)
				.extracting("content", "status", "groupId", "parentId")
				.containsExactlyInAnyOrder(
						tuple(basicCommentNormal_GN.getContent(), basicCommentNormal_GN.getStatus(),
								groupCommentNormal.getId(), groupCommentNormal.getId()),
						tuple(basicCommentAccuse_GN.getContent(), basicCommentAccuse_GN.getStatus(),
								groupCommentNormal.getId(), groupCommentNormal.getId()),
						tuple(basicCommentDelete_BN.getContent(), basicCommentDelete_BN.getStatus(),
								groupCommentNormal.getId(), basicCommentNormal_GN.getId())
				);

		assertThat(groupComments.get(1).getBasicComments()).hasSize(0);

		assertThat(groupComments.get(2).getBasicComments()).hasSize(0);
	}

	@DisplayName("댓글 아이디에 해당하는 댓글을 조회할 수 있다.")
	@Test
	void getComment() {
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

		// Comment 세팅
		Comment comment1 = Comment.builder()
				.member(member)
				.feed(feed)
				.content("comment1")
				.build();
		comment1.saveGroup(comment1);
		comment1.saveParent(comment1);

		Comment comment2 = Comment.builder()
				.member(member)
				.feed(feed)
				.content("comment2")
				.build();
		comment2.saveGroup(comment2);
		comment2.saveParent(comment2);

		commentRepository.saveAll(List.of(comment1, comment2));

		// when
		BasicComment result1 = commentService.getComment(comment1.getId());
		BasicComment result2 = commentService.getComment(comment2.getId());

		// then
		assertThat(result1)
				.extracting("id", "createdBy", "feedId")
				.containsExactlyInAnyOrder(comment1.getId(), comment1.getMember().getUsername(),
						comment1.getFeed().getId());

		assertThat(result2)
				.extracting("id", "createdBy", "feedId")
				.containsExactlyInAnyOrder(comment2.getId(), comment2.getMember().getUsername(),
						comment2.getFeed().getId());
	}

	@DisplayName("로그인 상태인 경우, 댓글을 등록할 수 있다.")
	@Test
	void comment() {
		// given
		// Member 세팅
		Member member = Member.builder()
				.username("testMember")
				.build();
		memberRepository.save(member);
		MemberDto memberDto = MemberDto.from(member);

		// Feed 세팅
		Feed feed = Feed.builder()
				.content("testFeed")
				.build();
		feedRepository.save(feed);

		// Comment 세팅
		Comment comment = Comment.builder()
				.member(member)
				.feed(feed)
				.build();
		comment.saveGroup(comment);
		comment.saveParent(comment);
		commentRepository.save(comment);

		// BasicComment 세팅
		CommentPostForm commentPostForm1 = CommentPostForm.builder()
				.feedId(feed.getId())
				.content("commentPostForm1")
				.build();

		CommentPostForm commentPostForm2 = CommentPostForm.builder()
				.feedId(feed.getId())
				.content("commentPostForm2")
				.groupId(comment.getId())
				.parentId(comment.getId())
				.build();

		// when
		BasicComment result1 = commentService.comment(commentPostForm1, memberDto);
		BasicComment result2 = commentService.comment(commentPostForm2, memberDto);

		// then
		assertThat(result1).extracting("content", "groupId", "parentId", "feedId")
				.containsExactlyInAnyOrder(commentPostForm1.getContent(), result1.getId(),
						result1.getId(), commentPostForm1.getFeedId());

		assertThat(result2).extracting("content", "groupId", "parentId", "feedId")
				.containsExactly(commentPostForm2.getContent(), commentPostForm2.getGroupId(),
						commentPostForm2.getParentId(), commentPostForm2.getFeedId());
	}

	@DisplayName("작성자인 경우, 댓글을 수정할 수 있다.")
	@Test
	void modifiedComment() {
		// given
		// Member 세팅
		Member member = Member.builder()
				.username("testMember")
				.build();
		memberRepository.save(member);
		MemberDto memberDto = MemberDto.from(member);

		// Feed 세팅
		Feed feed = Feed.builder()
				.content("testFeed")
				.build();
		feedRepository.save(feed);

		// Comment 세팅
		Comment comment = Comment.builder()
				.member(member)
				.feed(feed)
				.build();
		comment.saveGroup(comment);
		comment.saveParent(comment);
		commentRepository.save(comment);

		// BasicComment 세팅
		CommentModifiedForm commentModifiedForm = CommentModifiedForm.builder()
				.id(comment.getId())
				.content("modifiedContent")
				.build();

		// when
		BasicComment result = commentService.modifiedComment(commentModifiedForm, memberDto);

		// then
		assertThat(result)
				.extracting("id", "content")
				.containsExactlyInAnyOrder(comment.getId(), commentModifiedForm.getContent());
	}

	@DisplayName("작성자가 아닌 경우, 댓글 수정 시 예외가 발생한다.")
	@Test
	void modifiedComment_noWriter() {
		// given
		// Member 세팅
		Member member = Member.builder()
				.username("writer")
				.build();
		memberRepository.save(member);

		// Feed 세팅
		Feed feed = Feed.builder()
				.content("testFeed")
				.build();
		feedRepository.save(feed);

		// Comment 세팅
		Comment comment = Comment.builder()
				.member(member)
				.feed(feed)
				.build();
		comment.saveGroup(comment);
		comment.saveParent(comment);
		commentRepository.save(comment);

		// BasicComment 세팅
		CommentModifiedForm commentModifiedForm = CommentModifiedForm.builder()
				.id(comment.getId())
				.content("modifiedContent")
				.build();

		// MemberDto 세팅
		MemberDto memberDto = MemberDto.builder()
				.username("noWriter")
				.build();

		// when

		// then
		assertThatThrownBy(() -> commentService.modifiedComment(commentModifiedForm, memberDto))
				.isInstanceOf(AlbumException.class)
				.hasMessage(NO_AUTHORITY.getMessage());
	}

	@DisplayName("로그인 상태인 경우, 댓글을 신고할 수 있다.")
	@Test
	void accuseComment() {
		// given
		// Member 세팅
		Member writer = Member.builder()
				.username("writer")
				.build();
		Member noWriter = Member.builder()
				.username("noWriter")
				.build();
		memberRepository.saveAll(List.of(writer, noWriter));

		// Feed 세팅
		Feed feed = Feed.builder()
				.content("testFeed")
				.build();
		feedRepository.save(feed);

		// Comment 세팅
		Comment comment = Comment.builder()
				.member(writer)
				.feed(feed)
				.build();
		comment.saveGroup(comment);
		comment.saveParent(comment);
		commentRepository.save(comment);

		// CommentAccuseDto 세팅
		CommentAccuseForm commentAccuseForm = CommentAccuseForm.builder()
				.commentId(comment.getId())
				.content("testContent")
				.build();

		// MemberDto 세팅
		MemberDto memberDto = MemberDto.from(noWriter);

		// when
		CommentAccuseDto result = commentService.accuseComment(commentAccuseForm,
				memberDto);

		// then
		assertThat(result).extracting("commentId", "memberId", "content")
				.containsExactlyInAnyOrder(comment.getId(), noWriter.getId(),
						commentAccuseForm.getContent());
	}

	@DisplayName("작성자인 경우, 댓글을 삭제할 수 있다.")
	@Test
	void deleteComment() {
		// given
		// Member 세팅
		Member writer = Member.builder()
				.username("writer")
				.build();
		memberRepository.save(writer);

		// Feed 세팅
		Feed feed = Feed.builder()
				.content("testFeed")
				.build();
		feedRepository.save(feed);

		// Comment 세팅
		Comment comment = Comment.builder()
				.member(writer)
				.feed(feed)
				.build();
		comment.saveGroup(comment);
		comment.saveParent(comment);
		commentRepository.save(comment);

		// BasicComment 세팅
		BasicComment basicComment = BasicComment.builder()
				.id(comment.getId())
				.build();

		// MemberDto 세팅
		MemberDto memberDto = MemberDto.from(writer);

		// when
		BasicComment result = commentService.deleteComment(basicComment, memberDto);

		// then
		assertThat(result).extracting("id", "content", "status")
				.containsExactlyInAnyOrder(comment.getId(), "삭제된 댓글 입니다.", DELETE);
	}

	@DisplayName("작성자가 아닌 경우, 댓글을 삭제 시 예외가 발생한다.")
	@Test
	void deleteComment_noWriter() {
		// given
		// Member 세팅
		Member writer = Member.builder()
				.username("writer")
				.build();
		Member noWriter = Member.builder()
				.username("noWriter")
				.build();
		memberRepository.saveAll(List.of(writer, noWriter));

		// Feed 세팅
		Feed feed = Feed.builder()
				.content("testFeed")
				.build();
		feedRepository.save(feed);

		// Comment 세팅
		Comment comment = Comment.builder()
				.member(writer)
				.feed(feed)
				.build();
		comment.saveGroup(comment);
		comment.saveParent(comment);
		commentRepository.save(comment);

		// BasicComment 세팅
		BasicComment basicComment = BasicComment.builder()
				.id(comment.getId())
				.build();

		// MemberDto 세팅
		MemberDto memberDto = MemberDto.from(noWriter);

		// when

		// then
		assertThatThrownBy(() -> commentService.deleteComment(basicComment, memberDto))
				.isInstanceOf(AlbumException.class)
				.hasMessage(NO_AUTHORITY.getMessage());
	}

	@DisplayName("댓글을 특정 상태로 변경할 수 있다.")
	@CsvSource({"NORMAL", "ACCUSE", "DELETE"})
	@ParameterizedTest
	void changeCommentStatus(CommentStatus commentStatus) {
		// given
		// Member 세팅
		Member writer = Member.builder()
				.username("writer")
				.build();
		memberRepository.save(writer);

		// Feed 세팅
		Feed feed = Feed.builder()
				.content("testFeed")
				.build();
		feedRepository.save(feed);

		// Comment 세팅
		Comment comment = Comment.builder()
				.member(writer)
				.feed(feed)
				.build();
		comment.saveGroup(comment);
		comment.saveParent(comment);
		commentRepository.save(comment);

		// when
		BasicComment result = commentService.changeCommentStatus(comment.getId(), commentStatus);

		// then
		assertThat(result).extracting("id", "status")
				.containsExactlyInAnyOrder(comment.getId(), commentStatus);
	}
}
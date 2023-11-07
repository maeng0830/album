package com.maeng0830.album.comment.service;

import static com.maeng0830.album.comment.domain.CommentStatus.ACCUSE;
import static com.maeng0830.album.comment.domain.CommentStatus.DELETE;
import static com.maeng0830.album.comment.domain.CommentStatus.NORMAL;
import static com.maeng0830.album.member.domain.MemberRole.ROLE_ADMIN;
import static com.maeng0830.album.member.domain.MemberRole.ROLE_MEMBER;
import static com.maeng0830.album.member.exception.MemberExceptionCode.NO_AUTHORITY;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.assertj.core.api.Assertions.tuple;

import com.maeng0830.album.comment.domain.Comment;
import com.maeng0830.album.comment.domain.CommentAccuse;
import com.maeng0830.album.comment.domain.CommentStatus;
import com.maeng0830.album.comment.dto.CommentAccuseDto;
import com.maeng0830.album.comment.dto.request.CommentAccuseForm;
import com.maeng0830.album.comment.dto.request.CommentChangeStatusForm;
import com.maeng0830.album.comment.dto.request.CommentModifiedForm;
import com.maeng0830.album.comment.dto.request.CommentPostForm;
import com.maeng0830.album.comment.dto.response.BasicComment;
import com.maeng0830.album.comment.dto.response.GroupComment;
import com.maeng0830.album.comment.repository.CommentAccuseRepository;
import com.maeng0830.album.comment.repository.CommentRepository;
import com.maeng0830.album.common.exception.AlbumException;
import com.maeng0830.album.feed.domain.Feed;
import com.maeng0830.album.feed.repository.FeedRepository;
import com.maeng0830.album.member.domain.Member;
import com.maeng0830.album.member.dto.MemberDto;
import com.maeng0830.album.member.repository.MemberRepository;
import com.maeng0830.album.support.ServiceTestSupport;
import java.util.ArrayList;
import java.util.List;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;

class CommentServiceTest extends ServiceTestSupport {

	@Autowired
	private CommentService commentService;
	@Autowired
	private FeedRepository feedRepository;
	@Autowired
	private CommentRepository commentRepository;
	@Autowired
	private CommentAccuseRepository commentAccuseRepository;
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
		List<GroupComment> groupComments = commentService.getFeedComments(feed.getId(),
				pageRequest);

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
				.extracting("id", "member.username", "feedId")
				.containsExactlyInAnyOrder(comment1.getId(), comment1.getMember().getUsername(),
						comment1.getFeed().getId());

		assertThat(result2)
				.extracting("id", "member.username", "feedId")
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
				.feedId(feed.getId())
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
		Member feedWriter = Member.builder()
				.username("feedWriter")
				.build();
		Member commentWriter = Member.builder()
				.username("commentWriter")
				.build();
		Member commentNoWriter = Member.builder()
				.username("commentNoWriter")
				.build();
		memberRepository.saveAll(List.of(feedWriter, commentWriter, commentNoWriter));

		// Feed 세팅
		Feed feed = Feed.builder()
				.member(feedWriter)
				.content("testFeed")
				.build();
		feedRepository.save(feed);

		// Comment 세팅
		Comment comment = Comment.builder()
				.member(commentWriter)
				.feed(feed)
				.build();
		comment.saveGroup(comment);
		comment.saveParent(comment);
		commentRepository.save(comment);

		// CommentAccuseForm 세팅
		CommentAccuseForm commentAccuseForm = CommentAccuseForm.builder()
				.id(comment.getId())
				.content("testContent")
				.build();

		// MemberDto 세팅
		MemberDto memberDto = MemberDto.from(commentNoWriter);

		// when
		CommentAccuseDto result = commentService.accuseComment(commentAccuseForm, memberDto);

		// then
		assertThat(result).extracting("comment.id", "member.id", "content")
				.containsExactlyInAnyOrder(comment.getId(), commentNoWriter.getId(),
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
		BasicComment result = commentService.deleteComment(comment.getId(), memberDto);

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
		assertThatThrownBy(() -> commentService.deleteComment(comment.getId(), memberDto))
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

		// CommentChangeStatusForm 세팅
		CommentChangeStatusForm commentChangeStatusForm = CommentChangeStatusForm.builder()
				.id(comment.getId())
				.feedId(feed.getId())
				.commentStatus(commentStatus)
				.build();

		// when
		BasicComment result = commentService.changeCommentStatus(commentChangeStatusForm);

		// then
		assertThat(result).extracting("id", "status")
				.containsExactlyInAnyOrder(comment.getId(), commentStatus);
	}

	@DisplayName("관리자인 경우, searchText가 작성자의 username 또는 nickname과 전방 일치하는 댓글을 조회할 수 있다."
			+ "searchText가 null인 경우, 모든 댓글이 조회된다."
			+ "댓글 상태 기준 신고-정상-삭제 순으로 정렬된다.")
	@CsvSource(value = {",", "username1", "nickname1", "username2", "nickname2"})
	@ParameterizedTest
	void getCommentsForAdmin(String searchText) {
		// given
		// 관리자 세팅
		Member admin = Member.builder()
				.role(ROLE_ADMIN)
				.build();
		// 작성자 세팅
		Member writer1 = Member.builder()
				.username("username11")
				.nickname("nickname11")
				.build();
		Member writer2 = Member.builder()
				.username("username22")
				.nickname("nickname22")
				.build();

		memberRepository.saveAll(List.of(admin, writer1, writer2));

		// 피드 세팅
		Feed feed = Feed.builder()
				.build();
		feedRepository.save(feed);

		// 댓글 세팅
		List<Comment> comments = new ArrayList<>();
		for (int i = 0; i < 3; i++) {
			CommentStatus commentStatus;
			Comment comment1;
			Comment comment2;

			if (i == 0) {
				commentStatus = NORMAL;

				comment1 = Comment.builder()
						.member(writer1)
						.feed(feed)
						.status(commentStatus)
						.build();
				comment1.saveGroup(comment1);
				comment1.saveParent(comment1);

				comment2 = Comment.builder()
						.member(writer2)
						.feed(feed)
						.status(commentStatus)
						.build();
				comment2.saveGroup(comment2);
				comment2.saveParent(comment2);
			} else if (i == 1) {
				commentStatus = ACCUSE;

				comment1 = Comment.builder()
						.member(writer1)
						.feed(feed)
						.status(commentStatus)
						.build();
				comment1.saveGroup(comment1);
				comment1.saveParent(comment1);

				comment2 = Comment.builder()
						.member(writer2)
						.feed(feed)
						.status(commentStatus)
						.build();
				comment2.saveGroup(comment2);
				comment2.saveParent(comment2);
			} else {
				commentStatus = DELETE;

				comment1 = Comment.builder()
						.member(writer1)
						.feed(feed)
						.status(commentStatus)
						.build();
				comment1.saveGroup(comment1);
				comment1.saveParent(comment1);

				comment2 = Comment.builder()
						.member(writer2)
						.feed(feed)
						.status(commentStatus)
						.build();
				comment2.saveGroup(comment2);
				comment2.saveParent(comment2);
			}

			comments.add(comment1);
			comments.add(comment2);
		}

		commentRepository.saveAll(comments);

		PageRequest pageRequest = PageRequest.of(0, 20);

		// when
		Page<BasicComment> result = commentService.getCommentsForAdmin(
				MemberDto.from(admin), searchText, pageRequest);

		// then
		if (searchText == null) {
			assertThat(result.getContent()).hasSize(6)
					.extracting("status")
					.containsExactly(
							ACCUSE, ACCUSE,
							NORMAL, NORMAL,
							DELETE, DELETE
					);
		} else if (searchText.equals("username1") || searchText.equals("nickname1")) {
			assertThat(result.getContent()).hasSize(3)
					.extracting("status", "member.username", "member.nickname")
					.containsExactly(
							tuple(ACCUSE, writer1.getUsername(), writer1.getNickname()),
							tuple(NORMAL, writer1.getUsername(), writer1.getNickname()),
							tuple(DELETE, writer1.getUsername(), writer1.getNickname())
					);
		} else if (searchText.equals("username2") || searchText.equals("nickname2")) {
			assertThat(result.getContent()).hasSize(3)
					.extracting("status", "member.username", "member.nickname")
					.containsExactly(
							tuple(ACCUSE, writer2.getUsername(), writer2.getNickname()),
							tuple(NORMAL, writer2.getUsername(), writer2.getNickname()),
							tuple(DELETE, writer2.getUsername(), writer2.getNickname())
					);
		}
	}

	@DisplayName("관리자가 아닌 경우, searchText가 작성자의 username 또는 nickname과 전방 일치하는 댓글을 조회할 때"
			+ "예외가 발생한다.")
	@Test
	void getCommentsForAdmin_noAdmin() {
		// given
		Member noAdmin = Member.builder()
				.role(ROLE_MEMBER)
				.build();
		MemberDto noAdminDto = MemberDto.from(noAdmin);

		// when
		assertThatThrownBy(() -> commentService.getCommentsForAdmin(noAdminDto, null, null))
				.isInstanceOf(AlbumException.class)
				.hasMessage(NO_AUTHORITY.getMessage());

		// then
	}

	@DisplayName("관리자인 경우, 주어진 댓글아이디에 해당하는 댓글신고 목록을 조회할 수 있다.")
	@Test
	void getCommentAccuses() {
		// given
		// 관리자 세팅
		Member admin = Member.builder()
				.role(ROLE_ADMIN)
				.build();
		// 작성자 세팅
		Member feedWriter = Member.builder()
				.build();
		Member commentWriter = Member.builder()
				.build();
		Member commentAccuseWriter = Member.builder()
				.build();
		memberRepository.saveAll(List.of(admin, feedWriter, commentWriter, commentAccuseWriter));

		// 피드 세팅
		Feed feed = Feed.builder()
				.member(feedWriter)
				.build();
		feedRepository.save(feed);

		// 댓글 세팅
		Comment comment1 = Comment.builder()
				.feed(feed)
				.member(commentWriter)
				.status(ACCUSE)
				.build();
		comment1.saveParent(comment1);
		comment1.saveGroup(comment1);

		Comment comment2 = Comment.builder()
				.feed(feed)
				.member(commentWriter)
				.status(ACCUSE)
				.build();
		comment2.saveParent(comment2);
		comment2.saveGroup(comment2);

		commentRepository.saveAll(List.of(comment1, comment2));

		// 댓글 신고 세팅
		List<CommentAccuse> commentAccuses = new ArrayList<>();
		for (int i = 0; i < 10; i++) {
			CommentAccuse commentAccuse;

			if (i % 2 == 0) {
				commentAccuse = CommentAccuse.builder()
						.member(commentAccuseWriter)
						.comment(comment1)
						.build();
			} else {
				commentAccuse = CommentAccuse.builder()
						.member(commentAccuseWriter)
						.comment(comment2)
						.build();
			}

			commentAccuses.add(commentAccuse);
		}

		commentAccuseRepository.saveAll(commentAccuses);

		// when
		List<CommentAccuseDto> result1 = commentService.getCommentAccuses(
				MemberDto.from(admin), comment1.getId());
		List<CommentAccuseDto> result2 = commentService.getCommentAccuses(
				MemberDto.from(admin), comment2.getId());

		// then
		assertThat(result1).hasSize(5)
				.extracting("comment.id")
				.containsExactlyInAnyOrder(
						comment1.getId(), comment1.getId(), comment1.getId(), comment1.getId(),
						comment1.getId()
				);
		assertThat(result2).hasSize(5)
				.extracting("comment.id")
				.containsExactlyInAnyOrder(
						comment2.getId(), comment2.getId(), comment2.getId(), comment2.getId(),
						comment2.getId()
				);
	}

	@DisplayName("관리자가 아닌 경우, 주어진 댓글아이디에 해당하는 댓글신고 목록을 조회할 때 예외가 발생한다.")
	@Test
	void getCommentAccuses_noAdmin() {
		// given
		Member noAdmin = Member.builder()
				.role(ROLE_MEMBER)
				.build();

		// when
		assertThatThrownBy(() -> commentService.getCommentAccuses(MemberDto.from(noAdmin), null))
				.isInstanceOf(AlbumException.class)
				.hasMessage(NO_AUTHORITY.getMessage());

		// then
	}
}
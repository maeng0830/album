package com.maeng0830.album.comment.service;

import static com.maeng0830.album.comment.exception.CommentExceptionCode.NOT_EXIST_COMMENT;
import static com.maeng0830.album.feed.exception.FeedExceptionCode.NOT_EXIST_FEED;
import static com.maeng0830.album.member.exception.MemberExceptionCode.NOT_EXIST_MEMBER;
import static com.maeng0830.album.member.exception.MemberExceptionCode.NO_AUTHORITY;
import static com.maeng0830.album.member.exception.MemberExceptionCode.REQUIRED_LOGIN;

import com.maeng0830.album.comment.domain.Comment;
import com.maeng0830.album.comment.domain.CommentAccuse;
import com.maeng0830.album.comment.domain.CommentStatus;
import com.maeng0830.album.comment.model.dto.CommentAccuseDto;
import com.maeng0830.album.comment.model.response.BasicComment;
import com.maeng0830.album.comment.model.response.GroupComment;
import com.maeng0830.album.comment.repository.CommentAccuseRepository;
import com.maeng0830.album.comment.repository.CommentRepository;
import com.maeng0830.album.common.exception.AlbumException;
import com.maeng0830.album.feed.domain.Feed;
import com.maeng0830.album.feed.repository.FeedRepository;
import com.maeng0830.album.member.domain.Member;
import com.maeng0830.album.member.dto.MemberDto;
import com.maeng0830.album.member.repository.MemberRepository;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class CommentService {

	private final CommentRepository commentRepository;
	private final CommentAccuseRepository commentAccuseRepository;
	private final FeedRepository feedRepository;
	private final MemberRepository memberRepository;

	public List<GroupComment> getFeedComments(Long feedId) {

		// 해당 피드의 댓글 조회
		List<Comment> comments = commentRepository.findFetchJoinAll(feedId,
				List.of(CommentStatus.NORMAL, CommentStatus.ACCUSE, CommentStatus.DELETE));

		// 그룹댓글 리스트 생성
		List<GroupComment> groupComments = comments.stream()
				.filter(c -> Objects.equals(c.getId(), c.getGroup().getId()))
				.map(GroupComment::from)
				.collect(Collectors.toList());

		for (GroupComment groupComment : groupComments) {
			System.out.println("groupComment = " + groupComment);
		}

		// 자식댓글 리스트 생성
		List<BasicComment> basicComments = comments.stream()
				.filter(c -> !Objects.equals(c.getId(), c.getGroup().getId()))
				.map(BasicComment::from)
				.collect(Collectors.toList());

		for (BasicComment basicComment : basicComments) {
			System.out.println("basicComment = " + basicComment);
		}

		// 그룹댓글 리스트  <- 자식댓글 리스트
		groupComments.forEach(g -> g.setBasicComments(
				basicComments.stream().filter(i -> i.getGroupId().equals(g.getId()))
						.collect(Collectors.toList())));

		return groupComments;
	}

	public BasicComment getComment(Long commentId) {

		Comment findComment = commentRepository.findById(commentId)
				.orElseThrow(() -> new AlbumException(
						NOT_EXIST_COMMENT));

		return BasicComment.from(findComment);
	}

	@Transactional
	public BasicComment comment(BasicComment basicComment, MemberDto memberDto) {

		// 로그인 여부 확인
		if (memberDto == null) {
			throw new AlbumException(REQUIRED_LOGIN);
		}

		// 작성자 조회
		Member loginMember = memberRepository.findById(memberDto.getId())
				.orElseThrow(() -> new AlbumException(NOT_EXIST_MEMBER));

		// 피드 조회
		Feed findFeed = feedRepository.findById(basicComment.getFeedId())
				.orElseThrow(() -> new AlbumException(NOT_EXIST_FEED));

		// 저장할 댓글
		Comment comment = Comment.builder()
				.member(loginMember)
				.feed(findFeed)
				.content(basicComment.getContent())
				.status(CommentStatus.NORMAL)
				.build();

		// 그룹 댓글 조회
		// 그룹 댓글이 없을 경우, 본인을 저장
		Comment group = null;
		if (basicComment.getGroupId() != null) {
			group = commentRepository.findById(basicComment.getGroupId())
					.orElseThrow(() -> new AlbumException(NOT_EXIST_COMMENT));
		}
		comment.saveGroup(group);

		// 부모 댓글 조회 및 저장
		// 부모 댓글이 없을 경우, 본인을 저장
		Comment parent = null;
		if (basicComment.getParentId() != null) {
			parent = commentRepository.findById(basicComment.getParentId())
					.orElseThrow(() -> new AlbumException(NOT_EXIST_COMMENT));
		}
		comment.saveParent(parent);

		commentRepository.save(comment);

		return BasicComment.from(comment);
	}

	@Transactional
	public BasicComment modifiedComment(BasicComment basicComment,
										MemberDto memberDto) {

		// 로그인 여부 확인
		if (memberDto == null) {
			throw new AlbumException(REQUIRED_LOGIN);
		}

		Comment findComment = commentRepository.findById(basicComment.getId())
				.orElseThrow(() -> new AlbumException(NOT_EXIST_COMMENT));

		if (!findComment.getMember().getUsername().equals(memberDto.getUsername())) {
			throw new AlbumException(NO_AUTHORITY);
		}

		findComment.changeContent(basicComment.getContent());

		return BasicComment.from(findComment);
	}

	@Transactional
	public CommentAccuseDto accuseComment(CommentAccuseDto commentAccuseDto,
										  MemberDto memberDto) {

		// 로그인 여부 확인
		if (memberDto == null) {
			throw new AlbumException(REQUIRED_LOGIN);
		}

		Member findMember = memberRepository.findById(memberDto.getId())
				.orElseThrow(() -> new AlbumException(NOT_EXIST_MEMBER));

		Comment findComment = commentRepository.findById(commentAccuseDto.getCommentId())
				.orElseThrow(() -> new AlbumException(NOT_EXIST_COMMENT));

		findComment.accuseComment();

		CommentAccuse commentAccuse = CommentAccuse.builder()
				.comment(findComment)
				.member(findMember)
				.content(commentAccuseDto.getContent())
				.build();

		commentAccuseRepository.save(commentAccuse);

		return CommentAccuseDto.from(commentAccuse);
	}

	@Transactional
	public BasicComment deleteComment(BasicComment basicComment,
									  MemberDto memberDto) {

		// 로그인 여부 확인
		if (memberDto == null) {
			throw new AlbumException(REQUIRED_LOGIN);
		}

		Comment findComment = commentRepository.findById(basicComment.getId())
				.orElseThrow(() -> new AlbumException(NOT_EXIST_COMMENT));

		if (!findComment.getMember().getUsername().equals(memberDto.getUsername())) {
			throw new AlbumException(NO_AUTHORITY);
		}

		findComment.changeContentForDelete();
		findComment.changeStatus(CommentStatus.DELETE);

		return BasicComment.from(findComment);
	}

	@Transactional
	public BasicComment changeCommentStatus(Long commentId, CommentStatus commentStatus) {
		Comment findComment = commentRepository.findById(commentId)
				.orElseThrow(() -> new AlbumException(NOT_EXIST_COMMENT));

		findComment.changeStatus(commentStatus);

		return BasicComment.from(findComment);
	}
}

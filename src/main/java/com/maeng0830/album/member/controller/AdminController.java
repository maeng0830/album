package com.maeng0830.album.member.controller;

import com.maeng0830.album.comment.domain.CommentStatus;
import com.maeng0830.album.comment.dto.response.BasicComment;
import com.maeng0830.album.comment.service.CommentService;
import com.maeng0830.album.feed.domain.FeedStatus;
import com.maeng0830.album.feed.dto.FeedDto;
import com.maeng0830.album.feed.service.FeedService;
import com.maeng0830.album.member.domain.MemberStatus;
import com.maeng0830.album.member.dto.MemberDto;
import com.maeng0830.album.member.service.MemberService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/admin")
@RequiredArgsConstructor
public class AdminController {

	private final MemberService memberService;
	private final FeedService feedService;
	private final CommentService commentService;

	@PutMapping("/members/{memberId}/status")
	public MemberDto changeMemberStatus(@PathVariable Long memberId, @RequestBody MemberStatus memberStatus) {
		return memberService.changeMemberStatus(memberId, memberStatus);
	}

	@PutMapping("/feeds/{feedId}/status")
	public FeedDto changeFeedStatus(@PathVariable Long feedId, @RequestBody FeedStatus feedStatus) {
		return feedService.changeFeedStatus(feedId, feedStatus);
	}

	@PutMapping("/comments/{commentId}/status")
	public BasicComment changeCommentStatus(@PathVariable Long commentId, @RequestBody
											CommentStatus commentStatus) {
		return commentService.changeCommentStatus(commentId, commentStatus);
	}
}

package com.maeng0830.album.member.controller;

import com.maeng0830.album.comment.dto.CommentAccuseDto;
import com.maeng0830.album.comment.dto.request.CommentChangeStatusForm;
import com.maeng0830.album.comment.dto.response.BasicComment;
import com.maeng0830.album.comment.service.CommentService;
import com.maeng0830.album.common.util.AlbumUtil;
import com.maeng0830.album.feed.dto.FeedAccuseDto;
import com.maeng0830.album.feed.dto.FeedDto;
import com.maeng0830.album.feed.dto.response.FeedAccuseResponse;
import com.maeng0830.album.feed.dto.response.FeedResponse;
import com.maeng0830.album.feed.dto.request.FeedChangeStatusForm;
import com.maeng0830.album.feed.service.FeedService;
import com.maeng0830.album.member.dto.MemberDto;
import com.maeng0830.album.member.dto.request.MemberChangeStatusForm;
import com.maeng0830.album.member.service.MemberService;
import com.maeng0830.album.security.formlogin.PrincipalDetails;
import java.util.List;
import javax.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
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
	private final AlbumUtil albumUtil;

	// Feed
	@GetMapping("/feeds")
	public Page<FeedResponse> getFeedsForAdmin(@AuthenticationPrincipal PrincipalDetails principalDetails,
											   String searchText, Pageable pageable) {
		return feedService.getFeedsForAdmin(albumUtil.checkLogin(principalDetails), searchText, pageable);
	}

	@GetMapping("/feeds/{feedId}/accuses")
	public List<FeedAccuseResponse> getFeedAccuses(@AuthenticationPrincipal PrincipalDetails principalDetails,
												   @PathVariable Long feedId) {
		return feedService.getFeedAccuses(albumUtil.checkLogin(principalDetails), feedId);
	}

	@PutMapping("/feeds/status")
	public FeedDto changeFeedStatus(@Valid @RequestBody FeedChangeStatusForm feedChangeStatusForm) {
		return feedService.changeFeedStatus(feedChangeStatusForm);
	}

	// Member
	@GetMapping("/members")
	public Page<MemberDto> getMembersForAdmin(@AuthenticationPrincipal PrincipalDetails principalDetails,
											  String searchText, Pageable pageable) {
		return memberService.getMembersForAdmin(albumUtil.checkLogin(principalDetails), searchText, pageable);
	}

	@PutMapping("/members/status")
	public MemberDto changeMemberStatus(@Valid @RequestBody MemberChangeStatusForm memberChangeStatusForm) {
		return memberService.changeMemberStatus(memberChangeStatusForm);
	}

	// Comment
	@GetMapping("/comments")
	public Page<BasicComment> getCommentsForAdmin(@AuthenticationPrincipal PrincipalDetails principalDetails,
												  String searchText, Pageable pageable) {
		return commentService.getCommentsForAdmin(albumUtil.checkLogin(principalDetails), searchText, pageable);
	}

	@GetMapping("/comments/{commentId}/accuses")
	public List<CommentAccuseDto> getCommentAccuses(@AuthenticationPrincipal PrincipalDetails principalDetails,
													@PathVariable Long commentId) {
		return commentService.getCommentAccuses(albumUtil.checkLogin(principalDetails), commentId);
	}

	@PutMapping("/comments/status")
	public BasicComment changeCommentStatus(@Valid @RequestBody CommentChangeStatusForm commentChangeStatusForm) {
		return commentService.changeCommentStatus(commentChangeStatusForm);
	}
}

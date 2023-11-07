package com.maeng0830.album.comment.controller;

import com.maeng0830.album.comment.dto.CommentAccuseDto;
import com.maeng0830.album.comment.dto.request.CommentAccuseForm;
import com.maeng0830.album.comment.dto.request.CommentModifiedForm;
import com.maeng0830.album.comment.dto.request.CommentPostForm;
import com.maeng0830.album.comment.dto.response.BasicComment;
import com.maeng0830.album.comment.dto.response.GroupComment;
import com.maeng0830.album.comment.service.CommentService;
import com.maeng0830.album.common.util.AlbumUtil;
import com.maeng0830.album.security.formlogin.PrincipalDetails;
import java.util.List;
import javax.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/comments")
public class CommentController {

	private final CommentService commentService;
	private final AlbumUtil albumUtil;

	@GetMapping()
	public List<GroupComment> getFeedComments(Long feedId, Pageable pageable) {
		return commentService.getFeedComments(feedId, pageable);
	}

	@GetMapping("/{commentId}")
	public BasicComment getComment(@PathVariable Long commentId) {
		return commentService.getComment(commentId);
	}

	@PostMapping()
	public BasicComment comment(@Valid @RequestBody CommentPostForm commentPostForm,
								@AuthenticationPrincipal PrincipalDetails principalDetails) {
		return commentService.comment(commentPostForm, albumUtil.checkLogin(principalDetails));
	}

	@PutMapping()
	public BasicComment modifiedComment(@Valid @RequestBody CommentModifiedForm commentModifiedForm,
										@AuthenticationPrincipal PrincipalDetails principalDetails) {
		return commentService.modifiedComment(commentModifiedForm, albumUtil.checkLogin(principalDetails));
	}

	@PutMapping("/accuse")
	public CommentAccuseDto accuseComment(@Valid @RequestBody CommentAccuseForm commentAccuseForm,
										  @AuthenticationPrincipal PrincipalDetails principalDetails) {
		return commentService.accuseComment(commentAccuseForm, albumUtil.checkLogin(principalDetails));
	}

	@DeleteMapping("/{commentId}")
	public BasicComment deleteComment(@PathVariable Long commentId,
									  @AuthenticationPrincipal PrincipalDetails principalDetails) {
		return commentService.deleteComment(commentId, albumUtil.checkLogin(principalDetails));
	}
}

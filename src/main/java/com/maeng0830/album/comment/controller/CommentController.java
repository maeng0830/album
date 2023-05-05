package com.maeng0830.album.comment.controller;

import com.maeng0830.album.comment.model.dto.CommentAccuseDto;
import com.maeng0830.album.comment.model.dto.CommentDto;
import com.maeng0830.album.comment.model.response.GroupComment;
import com.maeng0830.album.comment.model.response.BasicComment;
import com.maeng0830.album.comment.service.CommentService;
import com.maeng0830.album.security.formlogin.PrincipalDetails;
import java.util.List;
import lombok.RequiredArgsConstructor;
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

	@GetMapping()
	public List<GroupComment> getFeedComments(Long feedId) {
		return commentService.getFeedComments(feedId);
	}

	@GetMapping("/{commentId}")
	public BasicComment getComment(@PathVariable Long commentId) {
		return commentService.getComment(commentId);
	}

	@PostMapping()
	public BasicComment comment(@RequestBody BasicComment basicComment, @AuthenticationPrincipal PrincipalDetails principalDetails) {
		return commentService.comment(basicComment, principalDetails);
	}

	@PutMapping()
	public BasicComment modifiedComment(@RequestBody BasicComment basicComment, @AuthenticationPrincipal PrincipalDetails principalDetails) {
		return commentService.modifiedComment(basicComment, principalDetails);
	}

	@PutMapping("/accuse")
	public CommentAccuseDto accuseComment(@RequestBody CommentAccuseDto commentAccuseDto, @AuthenticationPrincipal PrincipalDetails principalDetails) {
		return commentService.accuseComment(commentAccuseDto, principalDetails);
	}

	@DeleteMapping()
	public BasicComment deleteComment(@RequestBody BasicComment basicComment, @AuthenticationPrincipal PrincipalDetails principalDetails) {
		return commentService.deleteComment(basicComment, principalDetails);
	}
}

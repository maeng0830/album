package com.maeng0830.album.follow.controller;

import com.maeng0830.album.common.util.AlbumUtil;
import com.maeng0830.album.follow.dto.FollowDto;
import com.maeng0830.album.follow.service.FollowService;
import com.maeng0830.album.security.formlogin.PrincipalDetails;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
public class FollowController {

	private final FollowService followService;

	private final AlbumUtil albumUtil;

	// 팔로우 하기
	@PostMapping("/follows/{followeeId}")
	public FollowDto follow(@PathVariable Long followeeId,
							@AuthenticationPrincipal PrincipalDetails principalDetails) {

		return followService.follow(followeeId, albumUtil.checkLogin(principalDetails));
	}

	// 팔로우 끊기
	@DeleteMapping("/follows/{followeeId}")
	public String cancelFollow(@PathVariable Long followeeId,
							   @AuthenticationPrincipal PrincipalDetails principalDetails) {
		return followService.cancelFollow(followeeId, albumUtil.checkLogin(principalDetails));
	}
}

package com.maeng0830.album.follow.controller;

import com.maeng0830.album.common.util.AlbumUtil;
import com.maeng0830.album.follow.dto.FollowDto;
import com.maeng0830.album.follow.service.FollowService;
import com.maeng0830.album.security.formlogin.PrincipalDetails;
import java.util.Map;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RequestMapping("/api/follows")
@RestController
@RequiredArgsConstructor
public class FollowController {

	private final FollowService followService;

	private final AlbumUtil albumUtil;

	// 팔로우 하기
	@PostMapping("/{followingId}")
	public FollowDto follow(@PathVariable Long followingId,
							@AuthenticationPrincipal PrincipalDetails principalDetails) {
		return followService.follow(followingId, albumUtil.checkLogin(principalDetails));
	}

	// 팔로우 취소
	@DeleteMapping("/{followingId}")
	public Map<String, String> cancelFollow(@PathVariable Long followingId,
											@AuthenticationPrincipal PrincipalDetails principalDetails) {
		return followService.cancelFollow(followingId, albumUtil.checkLogin(principalDetails));
	}

	// 팔로잉 목록
	@GetMapping("/following/{followerId}")
	public Page<FollowDto> getFollowings(@PathVariable Long followerId,
										 @AuthenticationPrincipal PrincipalDetails principalDetails,
										 String searchText,
										 Pageable pageable) {
		return followService.getFollowings(followerId, albumUtil.checkLogin(principalDetails), searchText, pageable);
	}

	// 팔로워 목록
	@GetMapping("/follower/{followingId}")
	public Page<FollowDto> getFollowers(@PathVariable Long followingId,
										 @AuthenticationPrincipal PrincipalDetails principalDetails,
										 String searchText,
										 Pageable pageable) {
		return followService.getFollowers(followingId, albumUtil.checkLogin(principalDetails), searchText, pageable);
	}
}

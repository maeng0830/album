package com.maeng0830.album.feed.controller;

import com.maeng0830.album.feed.dto.FeedDto;
import com.maeng0830.album.feed.dto.FeedResponse;
import com.maeng0830.album.feed.service.FeedService;
import com.maeng0830.album.security.formlogin.PrincipalDetails;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

@RestController
@RequiredArgsConstructor
@RequestMapping("/feeds")
public class FeedController {

	private final FeedService feedService;

	@GetMapping
	public List<FeedDto> getFeeds(@AuthenticationPrincipal PrincipalDetails principalDetails) {
		return feedService.getFeeds(principalDetails);
	}

	@GetMapping("/{feedId}")
	public FeedDto getFeed(@PathVariable Long feedId) {
		return feedService.getFeed(feedId);
	}

	@PostMapping
	public FeedResponse feed(@RequestPart FeedDto feedDto,
							 @RequestPart List<MultipartFile> imageFiles,
							 @AuthenticationPrincipal PrincipalDetails principalDetails) {
		return feedService.feed(feedDto, imageFiles, principalDetails);
	}

	@DeleteMapping("/{feedId}")
	public FeedDto deleteFeed(@PathVariable Long feedId,
							 @AuthenticationPrincipal PrincipalDetails principalDetails) {
		return feedService.deleteFeed(feedId, principalDetails);
	}
}

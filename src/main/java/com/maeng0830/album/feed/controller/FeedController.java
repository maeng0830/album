package com.maeng0830.album.feed.controller;

import com.maeng0830.album.common.util.AlbumUtil;
import com.maeng0830.album.feed.dto.FeedAccuseDto;
import com.maeng0830.album.feed.dto.FeedDto;
import com.maeng0830.album.feed.dto.FeedResponse;
import com.maeng0830.album.feed.dto.request.FeedAccuseRequestForm;
import com.maeng0830.album.feed.dto.request.FeedRequestForm;
import com.maeng0830.album.feed.service.FeedService;
import com.maeng0830.album.security.formlogin.PrincipalDetails;
import java.util.List;
import javax.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

@RestController
@RequiredArgsConstructor
@RequestMapping("/feeds")
public class FeedController {

	private final FeedService feedService;
	private final AlbumUtil albumUtil;

	// 메인 페이지 전체 피드 목록 조회, 로그인 여부에 따라 다른 피드 목록 반환
	// searchText(회원 닉네임) != null -> 해당 닉네임이 작성자인 피드 목록 반환
	@GetMapping()
	public Page<FeedResponse> getFeedsForMain(@AuthenticationPrincipal PrincipalDetails principalDetails, String searchText, Pageable pageable) {
		if (searchText == null) {
			return feedService.getFeedsForMain(albumUtil.checkLogin(principalDetails), pageable);
		} else {
			return feedService.getFeedsForMainWithSearchText(searchText, pageable);
		}
	}

	@GetMapping("/{feedId}")
	public FeedResponse getFeed(@PathVariable Long feedId) {
		return feedService.getFeed(feedId);
	}

	@PostMapping
	public FeedResponse feed(@Valid @RequestPart FeedRequestForm feedRequestForm,
							 @RequestPart(required = false) List<MultipartFile> imageFiles,
							 @AuthenticationPrincipal PrincipalDetails principalDetails) {
		return feedService.feed(feedRequestForm, imageFiles, albumUtil.checkLogin(principalDetails));
	}

	@DeleteMapping("/{domainId}")
	public FeedDto deleteFeed(@PathVariable Long domainId,
							  @AuthenticationPrincipal PrincipalDetails principalDetails) {
		return feedService.deleteFeed(domainId, albumUtil.checkLogin(principalDetails));
	}

	@PutMapping("/{feedId}")
	public FeedResponse modifiedFeed(@PathVariable Long feedId,
									 @Valid @RequestPart FeedRequestForm feedRequestForm,
									 @RequestPart List<MultipartFile> imageFiles,
									 @AuthenticationPrincipal PrincipalDetails principalDetails) {
		return feedService.modifiedFeed(feedId, feedRequestForm, imageFiles, albumUtil.checkLogin(principalDetails));
	}

	@PutMapping("/{domainId}/accuse")
	public FeedAccuseDto accuseFeed(@PathVariable Long domainId,
									@Valid @RequestBody FeedAccuseRequestForm feedAccuseRequestForm,
									@AuthenticationPrincipal PrincipalDetails principalDetails) {
		return feedService.accuseFeed(domainId, feedAccuseRequestForm, albumUtil.checkLogin(principalDetails));
	}

	@GetMapping("/members/{memberId}")
	public Page<FeedResponse> getMyFeeds(@PathVariable Long memberId,
										 @AuthenticationPrincipal PrincipalDetails principalDetails,
										 Pageable pageable) {
		return feedService.getMyFeeds(memberId, albumUtil.checkLogin(principalDetails), pageable);
	}
}

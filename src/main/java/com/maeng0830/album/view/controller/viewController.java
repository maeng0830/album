package com.maeng0830.album.view.controller;

import com.maeng0830.album.feed.dto.FeedResponse;
import com.maeng0830.album.feed.service.FeedService;
import com.maeng0830.album.member.dto.MemberDto;
import com.maeng0830.album.member.service.MemberService;
import com.maeng0830.album.security.formlogin.PrincipalDetails;
import javax.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

@Controller
@RequiredArgsConstructor
public class viewController {

	private final MemberService memberService;
	private final FeedService feedService;

	@GetMapping("/")
	public String home(String searchText, Model model) {

		model.addAttribute("searchText", searchText);

		return "index";
	}

	@GetMapping("/form-signup")
	public String formSignup() {
		return "formSignup";
	}

	@GetMapping("/withdraw")
	public String withdraw() {
		return "withdraw";
	}

	@GetMapping("/set-password-oauth2")
	public String setPasswordOauth2() {
		return "setPasswordOauth2";
	}

	@GetMapping("/members/post-feed")
	public String postFeed() {

		return "postFeed";
	}

	@GetMapping("/members/modified-feed/{feedId}")
	public String modifiedFeed(@PathVariable Long feedId, Model model) {
		FeedResponse feed = feedService.getFeed(feedId);

		model.addAttribute("feed", feed);

		return "modifiedFeed";
	}

	@GetMapping("/feed-page/{feedId}")
	public String feedPage(@PathVariable Long feedId,
						   @AuthenticationPrincipal PrincipalDetails principalDetails,
						   Model model) {

		model.addAttribute("feedId", feedId);
		if (principalDetails != null) {
			model.addAttribute("loginId", principalDetails.getMemberDto().getId());
		} else {
			model.addAttribute("loginId", -1);
		}
		return "feedPage";
	}

	@GetMapping("/admin/feedList")
	public String adminFeedList() {
		return "adminFeeds";
	}

	@GetMapping("/admin/memberList")
	public String adminMemberList() {
		return "adminMembers";
	}

	@GetMapping("/admin/commentList")
	public String adminCommentList() {
		return "adminComments";
	}

	@GetMapping("/members/my-profile")
	public String myProfile(@AuthenticationPrincipal PrincipalDetails principalDetails, Model model) {
		// login 회원 정보
		MemberDto memberDto = memberService.getMember(principalDetails.getMemberDto().getId());

		model.addAttribute("memberDto", memberDto);

		return "myProfile";
	}

	@GetMapping("/members/my-follow")
	public String myFollow(@AuthenticationPrincipal PrincipalDetails principalDetails,
								String searchText, Model model) {
		// login 회원 정보
		MemberDto memberDto = principalDetails.getMemberDto();

		model.addAttribute("memberDto", memberDto);
		model.addAttribute("searchText", searchText);

		return "myFollow";
	}

	@GetMapping("/members/my-feed")
	public String myFeed(@AuthenticationPrincipal PrincipalDetails principalDetails,
						 Model model) {
		// login 회원 정보
		MemberDto memberDto = principalDetails.getMemberDto();

		model.addAttribute("memberDto", memberDto);

		return "myFeed";
	}

	// 로그인 예외 발생 시 호출
	@RequestMapping(value = "/fail-Authentication", method = {RequestMethod.GET, RequestMethod.POST})
	public String loginForm(HttpServletRequest request, Model model) {
		String loginFailMsg = String.valueOf(request.getAttribute("loginFailMsg"));

		model.addAttribute("loginFailMsg", loginFailMsg);

		return "failAuthentication";
	}

	@GetMapping(value = {"/fail-authorize", "/require-login"})
	public String failAuthorize() {
		return "failAuthorize";
	}
}

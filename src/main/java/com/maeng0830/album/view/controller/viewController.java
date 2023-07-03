package com.maeng0830.album.view.controller;

import com.maeng0830.album.member.dto.MemberDto;
import com.maeng0830.album.member.service.MemberService;
import com.maeng0830.album.security.formlogin.PrincipalDetails;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

@Controller
@RequiredArgsConstructor
public class viewController {

	private final MemberService memberService;

	@GetMapping("/")
	public String home() {
		return "/index";
	}

	@GetMapping("/members/my-profile")
	public String myProfile(@AuthenticationPrincipal PrincipalDetails principalDetails, Model model) {
		// login 회원 정보
		MemberDto memberDto = memberService.getMember(principalDetails.getMemberDto().getId());

		model.addAttribute("memberDto", memberDto);

		return "/myProfile";
	}

	@GetMapping("/members/post-feed")
	public String postFeed() {

		return "/postFeed";
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
		return "/feedPage";
	}
}

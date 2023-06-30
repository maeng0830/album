package com.maeng0830.album.view.controller;

import com.maeng0830.album.member.dto.MemberDto;
import com.maeng0830.album.member.service.MemberService;
import com.maeng0830.album.security.formlogin.PrincipalDetails;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

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
}

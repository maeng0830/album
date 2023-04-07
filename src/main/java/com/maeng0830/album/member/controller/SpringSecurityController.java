package com.maeng0830.album.member.controller;

import com.maeng0830.album.member.dto.MemberDto;
import com.maeng0830.album.member.service.SpringSecurityService;
import com.maeng0830.album.security.PrincipalDetails;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
public class SpringSecurityController {

	private final SpringSecurityService springSecurityService;

	@PostMapping("/join")
	public MemberDto join(@ModelAttribute MemberDto memberDto) {
		return springSecurityService.join(memberDto);
	}

	@GetMapping("/login/test")
	public MemberDto login(@AuthenticationPrincipal PrincipalDetails principalDetails) {

		return principalDetails.getMemberDto();
	}
}

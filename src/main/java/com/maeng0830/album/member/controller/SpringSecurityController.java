package com.maeng0830.album.member.controller;

import com.maeng0830.album.member.dto.MemberDto;
import com.maeng0830.album.member.service.SpringSecurityService;
import com.maeng0830.album.security.PrincipalDetails;
import javax.servlet.http.HttpServletRequest;
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

	// 회원 가입
	@PostMapping("/join")
	public MemberDto join(@ModelAttribute MemberDto memberDto) {
		return springSecurityService.join(memberDto);
	}

	// 로그인 테스트
	@GetMapping("/login/test")
	public MemberDto login(@AuthenticationPrincipal PrincipalDetails principalDetails) {

		return principalDetails.getMemberDto();
	}

	// 로그인 예외 발생 시 호출
	@PostMapping("/loginForm")
	public String loginForm(HttpServletRequest request) {
		String loginFailMsg = String.valueOf(request.getAttribute("loginFailMsg"));

		if (loginFailMsg != null) {
			return loginFailMsg;
		} else {
			return null;
		}
	}
}

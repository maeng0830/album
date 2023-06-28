package com.maeng0830.album.member.controller;

import com.maeng0830.album.common.util.AlbumUtil;
import com.maeng0830.album.member.dto.MemberDto;
import com.maeng0830.album.member.dto.request.MemberJoinForm;
import com.maeng0830.album.member.dto.request.MemberModifiedForm;
import com.maeng0830.album.member.dto.request.MemberPasswordModifiedForm;
import com.maeng0830.album.member.service.MemberService;
import com.maeng0830.album.security.formlogin.PrincipalDetails;
import java.util.List;
import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

@RestController
@RequiredArgsConstructor
public class MemberController {

	private final MemberService memberService;

	private final AlbumUtil albumUtil;

	// 회원 가입
	@PostMapping("/join")
	public MemberDto join(@Valid @ModelAttribute MemberJoinForm memberJoinForm) {
		return memberService.join(memberJoinForm);
	}

	// form login 테스트
	@GetMapping("/form-login/test")
	public MemberDto formLoginTest(@AuthenticationPrincipal PrincipalDetails principalDetails) {

		return principalDetails.getMemberDto();
	}

	// Oauth2 login 테스트
	@GetMapping("/oauth-login/test")
	public MemberDto oauthLoginTest(@AuthenticationPrincipal PrincipalDetails principalDetails) {
		return principalDetails.getMemberDto();
	}

	// 로그인 예외 발생 시 호출
	@PostMapping("/loginForm")
	public String loginForm(HttpServletRequest request) {

		return String.valueOf(request.getAttribute("loginFailMsg"));
	}

	// 회원 탈퇴
	@DeleteMapping("/members")
	public MemberDto withdrawMember(@AuthenticationPrincipal PrincipalDetails principalDetails) {
		return memberService.withdraw(albumUtil.checkLogin(principalDetails));
	}

	// 전체 회원 조회
	@GetMapping("/members")
	public List<MemberDto> getMembers() {
		return memberService.getMembers();
	}

	// 회원 단건 조회
	@GetMapping("/members/{id}")
	public MemberDto getMember(@PathVariable Long id) {
		return memberService.getMember(id);
	}

	// 회원 정보 수정(본인)-nickname, phone, image
	@PutMapping("/members")
	public MemberDto modifiedMember(@AuthenticationPrincipal PrincipalDetails principalDetails,
									@Valid @RequestPart(value = "memberModifiedForm") MemberModifiedForm memberModifiedForm,
									@RequestPart(value = "imageFile", required = false) MultipartFile imageFile) {
		System.out.println("MemberController.modifiedMember");
		return memberService.modifiedMember(albumUtil.checkLogin(principalDetails), memberModifiedForm, imageFile);
	}

	// 회원 비밀번호 수정(본인)
	@PutMapping("/members/password")
	public MemberDto modifiedMemberPassword(@AuthenticationPrincipal PrincipalDetails principalDetails,
											@Valid @RequestPart(value = "memberPasswordModifiedForm") MemberPasswordModifiedForm memberPasswordModifiedForm) {
		return memberService.modifiedMemberPassword(albumUtil.checkLogin(principalDetails), memberPasswordModifiedForm);
	}
}

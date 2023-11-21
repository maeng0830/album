package com.maeng0830.album.member.controller;

import com.maeng0830.album.common.aop.annotation.MemberCheck;
import com.maeng0830.album.common.util.AlbumUtil;
import com.maeng0830.album.member.dto.MemberDto;
import com.maeng0830.album.member.dto.request.MemberJoinForm;
import com.maeng0830.album.member.dto.request.MemberModifiedForm;
import com.maeng0830.album.member.dto.request.MemberPasswordModifiedForm;
import com.maeng0830.album.member.dto.request.MemberWithdrawForm;
import com.maeng0830.album.member.dto.request.Oauth2PasswordForm;
import com.maeng0830.album.member.service.MemberService;
import com.maeng0830.album.security.formlogin.PrincipalDetails;
import javax.annotation.security.PermitAll;
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

@RequestMapping("/api/members")
@RestController
@RequiredArgsConstructor
public class MemberController {

	private final MemberService memberService;

	// 회원 가입
	@PostMapping()
	public MemberDto join(@Valid @RequestBody MemberJoinForm memberJoinForm) {
		return memberService.join(memberJoinForm);
	}

	// 회원 탈퇴
	@MemberCheck
	@DeleteMapping()
	public MemberDto withdrawMember(@AuthenticationPrincipal PrincipalDetails principalDetails,
									@Valid @RequestBody MemberWithdrawForm memberWithdrawForm) {
		return memberService.withdraw(principalDetails.getMemberDto(), memberWithdrawForm);
	}

	// 전체 회원 조회
	@GetMapping()
	public Page<MemberDto> getMembers(String searchText, Pageable pageable) {
		return memberService.getMembers(searchText, pageable);
	}

	// 회원 단건 조회
	@GetMapping("/{id}")
	public MemberDto getMember(@PathVariable Long id) {
		return memberService.getMember(id);
	}

	// 회원 정보 수정(본인)-nickname, phone, image
	@MemberCheck
	@PutMapping()
	public MemberDto modifiedMember(@AuthenticationPrincipal PrincipalDetails principalDetails,
									@Valid @RequestPart(value = "memberModifiedForm") MemberModifiedForm memberModifiedForm,
									@RequestPart(value = "imageFile", required = false) MultipartFile imageFile) {
		return memberService.modifiedMember(principalDetails.getMemberDto(), memberModifiedForm, imageFile);
	}

	// 회원 비밀번호 수정(본인)
	@MemberCheck
	@PutMapping("/password")
	public MemberDto modifiedMemberPassword(@AuthenticationPrincipal PrincipalDetails principalDetails,
											@Valid @RequestBody MemberPasswordModifiedForm memberPasswordModifiedForm) {
		return memberService.modifiedMemberPassword(principalDetails.getMemberDto(), memberPasswordModifiedForm);
	}

	@MemberCheck
	@PutMapping("/oauth2-password")
	public MemberDto setOauth2Password(@AuthenticationPrincipal PrincipalDetails principalDetails,
									   @Valid @RequestBody Oauth2PasswordForm oauth2PasswordForm) {
		return memberService.setOauth2Password(principalDetails.getMemberDto(), oauth2PasswordForm);
	}
}

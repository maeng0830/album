package com.maeng0830.album.member.controller;

import com.maeng0830.album.member.domain.MemberStatus;
import com.maeng0830.album.member.dto.MemberDto;
import com.maeng0830.album.member.service.MemberService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/admin")
@RequiredArgsConstructor
public class AdminController {

	private final MemberService memberService;

	@PutMapping("/members/{id}/status")
	public MemberDto changeMemberStatus(@PathVariable Long id, @RequestBody MemberStatus memberStatus) {
		return memberService.changeMemberStatus(id, memberStatus);
	}
}

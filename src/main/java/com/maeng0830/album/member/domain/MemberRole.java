package com.maeng0830.album.member.domain;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum MemberRole {
	ROLE_ADMIN("관리자"),
	ROLE_MEMBER("회원");

	private final String description;
}

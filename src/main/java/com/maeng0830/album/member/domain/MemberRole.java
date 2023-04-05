package com.maeng0830.album.member.domain;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum MemberRole {
	ADMIN("관리자"),
	MEMBER("회원");

	private final String description;
}

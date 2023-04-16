package com.maeng0830.album.member.domain;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum MemberStatus {
	FIRST("첫 로그인"),
	NORMAL("정상"),
	LOCKED("정지"),
	WITHDRAW("탈퇴");

	private final String description;
}

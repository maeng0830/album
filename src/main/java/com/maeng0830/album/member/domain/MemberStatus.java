package com.maeng0830.album.member.domain;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum MemberStatus {
	REQUIRED("인증 필요"),
	NORMAL("정상"),
	SUSPENSION("정지"),
	WITHDRAW("탈퇴");

	private final String description;
}

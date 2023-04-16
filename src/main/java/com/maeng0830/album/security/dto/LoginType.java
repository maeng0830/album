package com.maeng0830.album.security.dto;

public enum LoginType {
	FORM("일반 로그인"),
	OAUTH_GOOGLE("구글 로그인"),
	OAUTH_NAVER("네이버 로그인");

	private final String description;

	LoginType(String description) {
		this.description = description;
	}
}

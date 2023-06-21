package com.maeng0830.album.follow.exception;

import com.maeng0830.album.common.exception.code.ExceptionCode;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum FollowExceptionCode implements ExceptionCode {

	// 스프링 시큐리티 예외
	NOT_EXIST_FOLLOW("팔로우 관계가 존재하지 않습니다."),;

	private final String description;

	@Override
	public String getMessage() {
		return this.description;
	}
}

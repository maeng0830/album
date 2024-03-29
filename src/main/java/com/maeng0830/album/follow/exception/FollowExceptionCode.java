package com.maeng0830.album.follow.exception;

import com.maeng0830.album.common.exception.code.ExceptionCode;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum FollowExceptionCode implements ExceptionCode {

	// 스프링 시큐리티 예외
	NOT_EXIST_FOLLOW("팔로우 관계가 존재하지 않습니다."),
	ALREADY_EXIST_FOLLOW("이미 팔로우 하고 있습니다.."),
	NOT_ALLOW_FOLLOW_YOURSELF("자신에게 팔로우 신청할 수 없습니다.");

	private final String description;

	@Override
	public String getMessage() {
		return this.description;
	}
}

package com.maeng0830.album.member.exception;

import com.maeng0830.album.common.exception.ExceptionCode;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public enum SpringSecurityExceptionCode implements ExceptionCode {

	EXIST_USERNAME("존재하는 아이디입니다."),
	EXIST_NICKNAME("존재하는 닉네임입니다."),
	SUSPENSION_MEMBER("정지된 회원입니다.");

	private final String description;

	@Override
	public String getMessage() {
		return this.description;
	}
}

package com.maeng0830.album.member.exception;

import com.maeng0830.album.common.exception.ExceptionCode;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum SpringSecurityExceptionCode implements ExceptionCode {

	// 스프링 시큐리티 예외
	INCORRECT_ID_OR_PASSWORD_MEMBER("아이디 또는 비밀번호가 틀렸습니다."),
	LOCKED_MEMBER("정지된 회원입니다."),
	INACTIVE_MEMBER("인증이 필요한 회원입니다."),


	EXIST_USERNAME("존재하는 아이디입니다."),
	EXIST_NICKNAME("존재하는 닉네임입니다."),
	;

	private final String description;

	@Override
	public String getMessage() {
		return this.description;
	}
}

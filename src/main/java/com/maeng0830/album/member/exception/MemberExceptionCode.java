package com.maeng0830.album.member.exception;

import com.maeng0830.album.common.exception.code.ExceptionCode;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum MemberExceptionCode implements ExceptionCode {

	// 스프링 시큐리티 예외
	INCORRECT_ID_OR_PASSWORD_MEMBER("아이디 또는 비밀번호가 틀렸습니다."),
	LOCKED_MEMBER("정지된 회원입니다."),
	INACTIVE_MEMBER("인증이 필요한 회원입니다."),
	REQUIRED_LOGIN("로그인이 필요합니다."),
	NOT_SAME_PASSWORD_REPASSWORD("비밀번호와 확인 비밀번호가 일치하지 않습니다."),
	INCORRECT_PASSWORD("현재 비밀번호가 틀렸습니다."),
	NOT_OAUTH2_LOGIN_MEMBER("소셜 회원이 아닙니다."),
	ALREADY_SET_REQUIRED_OAUTH2_PASSWORD("필수 비밀번호 설정을 완료한 소셜 회원입니다."),


	EXIST_USERNAME("존재하는 아이디입니다."),
	EXIST_NICKNAME("존재하는 닉네임입니다."),
	NOT_EXIST_MEMBER("존재하지 않는 회원입니다."),
	NO_AUTHORITY("권한이 없습니다.");

	private final String description;

	@Override
	public String getMessage() {
		return this.description;
	}
}

package com.maeng0830.album.common.exception.code;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum ApiExceptionCode implements ExceptionCode {
	NOT_READABLE("정확한 형식의 값을 입력해주세요.");

	private final String description;

	@Override
	public String getMessage() {
		return null;
	}
}


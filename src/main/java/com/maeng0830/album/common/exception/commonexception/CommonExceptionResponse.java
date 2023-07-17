package com.maeng0830.album.common.exception.commonexception;

import com.maeng0830.album.common.exception.ExceptionResponse;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public class CommonExceptionResponse {
	private final String exceptionName;
	private final String message;

	public CommonExceptionResponse(Exception e) {
		this.exceptionName = e.getClass().getSimpleName();
		this.message = e.getMessage();
	}
}

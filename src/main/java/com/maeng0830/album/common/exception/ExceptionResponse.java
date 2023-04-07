package com.maeng0830.album.common.exception;

import lombok.AllArgsConstructor;

@AllArgsConstructor
public class ExceptionResponse {

	private ExceptionCode exceptionCode;
	private String exceptionMessage;
}

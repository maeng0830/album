package com.maeng0830.album.common.exception;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class ExceptionResponse {

	private ExceptionCode exceptionCode;
	private String exceptionMessage;
}

package com.maeng0830.album.common.exception;

import com.maeng0830.album.common.exception.code.ExceptionCode;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class ExceptionResponse {

	private ExceptionCode code;
	private String message;
}

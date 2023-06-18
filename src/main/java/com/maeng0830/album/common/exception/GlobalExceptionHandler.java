package com.maeng0830.album.common.exception;

import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
public class GlobalExceptionHandler {

	@ExceptionHandler(AlbumException.class)
	public ExceptionResponse albumExceptionHandler(AlbumException e) {
		return new ExceptionResponse(e.getCode(), e.getMessage());
	}
}

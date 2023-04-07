package com.maeng0830.album.common.exception;

import lombok.Getter;

@Getter
public class AlbumException extends RuntimeException {

	private ExceptionCode exceptionCode;
	private String exceptionMessage;

	public AlbumException(ExceptionCode exceptionCode) {
		this.exceptionCode = exceptionCode;
		this.exceptionMessage = exceptionCode.getMessage();
	}
}

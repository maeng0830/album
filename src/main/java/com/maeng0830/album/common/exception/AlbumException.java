package com.maeng0830.album.common.exception;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@AllArgsConstructor
@NoArgsConstructor
public class AlbumException extends RuntimeException {

	private ExceptionCode exceptionCode;
	private String exceptionMessage;

	public AlbumException(ExceptionCode exceptionCode) {
		this.exceptionCode = exceptionCode;
		this.exceptionMessage = exceptionCode.getMessage();
	}

	public AlbumException(ExceptionCode exceptionCode, Throwable cause) {
		super(cause);
		this.exceptionCode = exceptionCode;
		this.exceptionMessage = exceptionCode.getMessage();
	}
}

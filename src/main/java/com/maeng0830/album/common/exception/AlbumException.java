package com.maeng0830.album.common.exception;

import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class AlbumException extends RuntimeException {

	private ExceptionCode code;

	public AlbumException(ExceptionCode code) {
		super(code.getMessage());
		this.code = code;
	}

	public AlbumException(ExceptionCode code, Throwable cause) {
		super(code.getMessage(), cause);
		this.code = code;
	}
}

package com.maeng0830.album.common.exception.bindexception;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public class BindExceptionResponse {
	private final String code;
	private final String message;
}

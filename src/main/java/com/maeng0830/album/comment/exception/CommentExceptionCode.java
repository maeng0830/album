package com.maeng0830.album.comment.exception;

import com.maeng0830.album.common.exception.ExceptionCode;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum CommentExceptionCode implements ExceptionCode {
	NOT_EXIST_COMMENT("댓글이 존재하지 않습니다.");

	private final String description;

	@Override
	public String getMessage() {
		return null;
	}
}

package com.maeng0830.album.feed.exception;

import com.maeng0830.album.common.exception.ExceptionCode;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum FeedExceptionCode implements ExceptionCode {
	NOT_EXIST_FEED("존재하지 않는 피드입니다.");

	private final String description;

	@Override
	public String getMessage() {
		return null;
	}
}

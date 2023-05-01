package com.maeng0830.album.feed.exception;

import com.maeng0830.album.common.exception.ExceptionCode;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum FeedExceptionCode implements ExceptionCode {
	NOT_EXIST_FEED("피드가 존재하지 않습니다."),
	NOT_EXIST_FEED_IMAGE("피드 이미지가 존재하지 않습니다.");

	private final String description;

	@Override
	public String getMessage() {
		return null;
	}
}

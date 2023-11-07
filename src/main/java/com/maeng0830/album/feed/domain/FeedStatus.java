package com.maeng0830.album.feed.domain;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.maeng0830.album.common.enumconvertor.EnumConvertor;
import com.maeng0830.album.common.enumconvertor.EnumType;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum FeedStatus implements EnumType {
	NORMAL("정상", "002"),
	ACCUSE("신고", "001"),
	DELETE("삭제", "003");

	private final String description;
	private final String code;

	public static class FeedStatusConvertor extends EnumConvertor<FeedStatus> {
		public FeedStatusConvertor() {
			super(FeedStatus.class);
		}
	}

	@JsonCreator
	public static FeedStatus from(String val){
		for(FeedStatus feedStatus : FeedStatus.values()){
			if(feedStatus.name().equals(val)){
				return feedStatus;
			}
		}
		return null;
	}
}

package com.maeng0830.album.feed.domain;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;

@AllArgsConstructor
public enum FeedStatus {
	NORMAL("정상"),
	ACCUSE("신고"),
	DELETE("삭제");

	private final String description;

	@JsonCreator
	public static FeedStatus from(@JsonProperty("feedStatus") String val){
		for(FeedStatus feedStatus : FeedStatus.values()){
			if(feedStatus.name().equals(val)){
				return feedStatus;
			}
		}
		return null;
	}
}

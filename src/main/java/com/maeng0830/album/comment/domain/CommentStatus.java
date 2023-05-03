package com.maeng0830.album.comment.domain;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;

@AllArgsConstructor
public enum CommentStatus {

	NORMAL("정상"),
	ACCUSE("신고"),
	DELETE("삭제");

	private final String description;

	@JsonCreator
	public static CommentStatus from(@JsonProperty("commentStatus") String val){
		for(CommentStatus commentStatus : CommentStatus.values()){
			if(commentStatus.name().equals(val)){
				return commentStatus;
			}
		}
		return null;
	}
}

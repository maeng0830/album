package com.maeng0830.album.comment.domain;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonValue;
import com.maeng0830.album.common.enumconvertor.EnumConvertor;
import com.maeng0830.album.common.enumconvertor.EnumType;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum CommentStatus implements EnumType {

	NORMAL("정상", "002"),
	ACCUSE("신고", "001"),
	DELETE("삭제", "003");

	private final String description;
	private final String code;

	public static class CommentStatusConvertor extends EnumConvertor<CommentStatus> {
		public CommentStatusConvertor() {
			super(CommentStatus.class);
		}
	}

	@JsonCreator
	public static CommentStatus from(String val){
		for(CommentStatus commentStatus : CommentStatus.values()){
			if(commentStatus.name().equals(val)){
				return commentStatus;
			}
		}
		return null;
	}
}

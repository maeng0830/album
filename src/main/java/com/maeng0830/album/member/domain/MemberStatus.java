package com.maeng0830.album.member.domain;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.maeng0830.album.common.enumconvertor.EnumConvertor;
import com.maeng0830.album.common.enumconvertor.EnumType;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum MemberStatus implements EnumType {

	NORMAL("정상", "001"),
	LOCKED("정지", "002"),
	WITHDRAW("탈퇴", "003"),
	FIRST("첫 로그인", "004");

	private final String description;
	private final String code;

	public static class MemberStatusConvertor extends EnumConvertor<MemberStatus> {
		public MemberStatusConvertor() {
			super(MemberStatus.class);
		}
	}

	@JsonCreator
	public static MemberStatus from(String val){
		for(MemberStatus memberStatus : MemberStatus.values()){
			if(memberStatus.name().equals(val)){
				return memberStatus;
			}
		}
		return null;
	}
}

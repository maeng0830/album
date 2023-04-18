package com.maeng0830.album.member.domain;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum MemberStatus {
	FIRST("첫 로그인"),
	NORMAL("정상"),
	LOCKED("정지"),
	WITHDRAW("탈퇴");

	private final String description;

	@JsonCreator
	public static MemberStatus from(@JsonProperty("memberStatus") String val){
		for(MemberStatus memberStatus : MemberStatus.values()){
			if(memberStatus.name().equals(val)){
				return memberStatus;
			}
		}
		return null;
	}
}

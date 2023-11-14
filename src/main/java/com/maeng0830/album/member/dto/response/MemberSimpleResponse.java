package com.maeng0830.album.member.dto.response;

import com.maeng0830.album.common.model.image.Image;
import com.maeng0830.album.member.domain.Member;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class MemberSimpleResponse {

	private Long id;
	private String username;
	private String nickname;
	private Image image;

	@Builder
	public MemberSimpleResponse(Long id, String username, String nickname, Image image) {
		this.id = id;
		this.username = username;
		this.nickname = nickname;
		this.image = image;
	}

	public static MemberSimpleResponse from(Member member) {
		return MemberSimpleResponse.builder()
				.id(member.getId())
				.username(member.getUsername())
				.nickname(member.getNickname())
				.image(member.getImage())
				.build();
	}
}

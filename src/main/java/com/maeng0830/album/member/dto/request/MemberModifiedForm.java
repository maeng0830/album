package com.maeng0830.album.member.dto.request;

import javax.validation.constraints.NotBlank;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class MemberModifiedForm {
	@NotBlank
	private String nickname;

	@NotBlank
	private String phone;

	@Builder
	private MemberModifiedForm(String nickname, String phone) {
		this.nickname = nickname;
		this.phone = phone;
	}
}

package com.maeng0830.album.member.dto.request;

import javax.validation.constraints.NotBlank;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class MemberWithdrawForm {
	@NotBlank
	private String password;
	@NotBlank
	private String checkedPassword;

	@Builder
	private MemberWithdrawForm(String password, String checkedPassword) {
		this.password = password;
		this.checkedPassword = checkedPassword;
	}
}

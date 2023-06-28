package com.maeng0830.album.member.dto.request;

import javax.validation.constraints.NotBlank;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class MemberPasswordModifiedForm {
	@NotBlank
	private String currentPassword;
	@NotBlank
	private String modPassword;
	@NotBlank
	private String checkedModPassword;

	@Builder
	private MemberPasswordModifiedForm(String currentPassword, String modPassword, String checkedModPassword) {
		this.currentPassword = currentPassword;
		this.modPassword = modPassword;
		this.checkedModPassword = checkedModPassword;
	}
}

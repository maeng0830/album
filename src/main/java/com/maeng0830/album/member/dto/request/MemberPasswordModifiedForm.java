package com.maeng0830.album.member.dto.request;

import javax.validation.constraints.Pattern;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class MemberPasswordModifiedForm {

	@Pattern(regexp = "^(?=.*[A-Za-z])(?=.*\\d)(?=.*[@#$%^&+=!])(?=\\S+$).{8,16}$")
	private String currentPassword;

	@Pattern(regexp = "^(?=.*[A-Za-z])(?=.*\\d)(?=.*[@#$%^&+=!])(?=\\S+$).{8,16}$")
	private String modPassword;

	@Pattern(regexp = "^(?=.*[A-Za-z])(?=.*\\d)(?=.*[@#$%^&+=!])(?=\\S+$).{8,16}$")
	private String checkedModPassword;

	@Builder
	private MemberPasswordModifiedForm(String currentPassword, String modPassword,
									   String checkedModPassword) {
		this.currentPassword = currentPassword;
		this.modPassword = modPassword;
		this.checkedModPassword = checkedModPassword;
	}
}

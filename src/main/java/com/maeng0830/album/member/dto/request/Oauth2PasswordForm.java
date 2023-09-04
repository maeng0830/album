package com.maeng0830.album.member.dto.request;

import javax.validation.constraints.Pattern;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class Oauth2PasswordForm {

	@Pattern(regexp = "^(?=.*[A-Za-z])(?=.*\\d)(?=.*[@#$%^&+=!])(?=\\S+$).{8,16}$")
	private String password;

	@Pattern(regexp = "^(?=.*[A-Za-z])(?=.*\\d)(?=.*[@#$%^&+=!])(?=\\S+$).{8,16}$")
	private String checkedPassword;

	@Builder
	private Oauth2PasswordForm(String password, String checkedPassword) {
		this.password = password;
		this.checkedPassword = checkedPassword;
	}
}

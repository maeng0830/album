package com.maeng0830.album.member.dto.request;

import javax.validation.constraints.Pattern;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class MemberJoinForm {

	@Pattern(regexp = "^[A-Za-z0-9]{6,16}$")
	private String username;

	@Pattern(regexp = "^[A-Za-z0-9]{6,16}$")
	private String nickname;

	@Pattern(regexp = "^(?=.*[A-Za-z])(?=.*\\d)(?=.*[@#$%^&+=!])(?=\\S+$).{8,16}$")
	private String password;

	@Pattern(regexp = "^(?=.*[A-Za-z])(?=.*\\d)(?=.*[@#$%^&+=!])(?=\\S+$).{8,16}$")
	private String checkedPassword;

	@Pattern(regexp = "^[0-9]{9,12}$")
	private String phone;

	@Builder
	private MemberJoinForm(String username, String nickname, String password,
						   String checkedPassword, String phone) {
		this.username = username;
		this.nickname = nickname;
		this.password = password;
		this.checkedPassword = checkedPassword;
		this.phone = phone;
	}
}

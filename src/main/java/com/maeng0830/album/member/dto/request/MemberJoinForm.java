package com.maeng0830.album.member.dto.request;

import java.time.LocalDateTime;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class MemberJoinForm {

	@NotBlank
	private String username;
	@NotBlank
	private String nickname;
	@NotBlank
	private String password;
	@NotBlank
	private String checkedPassword;

	@Builder
	private MemberJoinForm(String username, String nickname, String password, String checkedPassword) {
		this.username = username;
		this.nickname = nickname;
		this.password = password;
		this.checkedPassword = checkedPassword;
	}
}

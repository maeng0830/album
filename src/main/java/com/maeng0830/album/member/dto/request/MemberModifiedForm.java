package com.maeng0830.album.member.dto.request;

import java.time.LocalDate;
import javax.validation.constraints.Pattern;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;

@Getter
@NoArgsConstructor
public class MemberModifiedForm {

	@Pattern(regexp = "^[A-Za-z0-9]{6,16}$")
	private String nickname;

	@Pattern(regexp = "^[0-9]{9,12}$")
	private String phone;

	@DateTimeFormat(pattern = "yyyy-MM-dd")
	private LocalDate birthDate;

	@Builder
	private MemberModifiedForm(String nickname, String phone, LocalDate birthDate) {
		this.nickname = nickname;
		this.phone = phone;
		this.birthDate = birthDate;
	}
}

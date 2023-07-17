package com.maeng0830.album.member.dto.request;

import java.time.LocalDate;
import java.time.LocalDateTime;
import javax.validation.constraints.NotBlank;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;

@Getter
@NoArgsConstructor
public class MemberModifiedForm {
	@NotBlank
	private String nickname;
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

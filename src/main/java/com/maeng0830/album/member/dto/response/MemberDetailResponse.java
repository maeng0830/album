package com.maeng0830.album.member.dto.response;

import com.maeng0830.album.common.model.image.Image;
import com.maeng0830.album.member.domain.MemberRole;
import com.maeng0830.album.member.domain.MemberStatus;
import java.time.LocalDate;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class MemberDetailResponse {

	private Long id;
	private String username;
	private String nickname;
	private String phone;
	private Image image;
	private LocalDate birthDate;
	private MemberStatus status;
	private MemberRole role;

	@Builder
	public MemberDetailResponse(Long id, String username, String nickname, String phone,
								Image image, LocalDate birthDate, MemberStatus status,
								MemberRole role) {
		this.id = id;
		this.username = username;
		this.nickname = nickname;
		this.phone = phone;
		this.image = image;
		this.birthDate = birthDate;
		this.status = status;
		this.role = role;
	}
}

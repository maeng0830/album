package com.maeng0830.album.member.dto;

import com.maeng0830.album.common.model.entity.BaseEntity;
import com.maeng0830.album.common.model.image.Image;
import com.maeng0830.album.member.domain.Member;
import com.maeng0830.album.member.domain.MemberRole;
import com.maeng0830.album.member.domain.MemberStatus;
import com.maeng0830.album.member.dto.request.MemberModifiedForm;
import com.maeng0830.album.security.dto.LoginType;
import java.time.LocalDate;
import java.time.LocalDateTime;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.SuperBuilder;

@Getter
@Setter
@SuperBuilder
@NoArgsConstructor
public class MemberDto extends BaseEntity {
	private Long id;
	private String username;
	private String nickname;
	private String password;
	private String phone;
	private LocalDate birthDate;
	private MemberStatus status;
	private MemberRole role;
	private Image image;
	private LoginType loginType;

	public static MemberDto from(Member member) {
		return MemberDto.builder()
				.id(member.getId())
				.username(member.getUsername())
				.nickname(member.getNickname())
				.password(member.getPassword())
				.phone(member.getPhone())
				.birthDate(member.getBirthDate())
				.status(member.getStatus())
				.role(member.getRole())
				.image(member.getImage())
				.createdAt(member.getCreatedAt())
				.modifiedAt(member.getModifiedAt())
				.modifiedBy(member.getModifiedBy())
				.loginType(member.getLoginType())
				.build();
	}

	public void modifiedBasicInfo(MemberModifiedForm memberModifiedForm) {
		this.nickname = memberModifiedForm.getNickname();
		this.phone = memberModifiedForm.getPhone();
		this.birthDate = memberModifiedForm.getBirthDate();
	}
}


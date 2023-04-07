package com.maeng0830.album.member.dto;

import com.maeng0830.album.common.TimeStamp;
import com.maeng0830.album.member.domain.Member;
import com.maeng0830.album.member.domain.MemberRole;
import com.maeng0830.album.member.domain.MemberStatus;
import java.time.LocalDateTime;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.SuperBuilder;

@Getter
@Setter
@SuperBuilder
@NoArgsConstructor
public class MemberDto extends TimeStamp {
	private Long id;
	private String username;
	private String nickname;
	private String password;
	private String phone;
	private LocalDateTime birthDate;
	private MemberStatus status;
	private MemberRole role;
	private String memberImageName;
	private String memberImagePath;

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
				.memberImageName(member.getMemberImageName())
				.memberImagePath(member.getMemberImagePath())
				.createdAt(member.getCreatedAt())
				.modifiedAt(member.getModifiedAt())
				.build();
	}
}


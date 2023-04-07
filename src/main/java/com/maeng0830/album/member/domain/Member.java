package com.maeng0830.album.member.domain;

import com.maeng0830.album.common.TimeStamp;
import com.maeng0830.album.member.dto.MemberDto;
import java.time.LocalDateTime;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

@Entity
@Getter
@SuperBuilder
@NoArgsConstructor
public class Member extends TimeStamp {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;
	private String username;
	private String nickname;
	private String password;
	private String phone;
	private LocalDateTime birthDate;
	@Enumerated(EnumType.STRING)
	private MemberStatus status;
	@Enumerated(EnumType.STRING)
	private MemberRole role;
	private String memberImageName;
	private String memberImagePath;

	public static Member from(MemberDto memberDto) {
		return Member.builder()
				.id(memberDto.getId())
				.username(memberDto.getUsername())
				.nickname(memberDto.getNickname())
				.password(memberDto.getPassword())
				.phone(memberDto.getPhone())
				.birthDate(memberDto.getBirthDate())
				.status(memberDto.getStatus())
				.role(memberDto.getRole())
				.memberImageName(memberDto.getMemberImageName())
				.memberImagePath(memberDto.getMemberImagePath())
				.createdAt(memberDto.getCreatedAt())
				.modifiedAt(memberDto.getModifiedAt())
				.build();
	}
}

package com.maeng0830.album.member.domain;

import com.maeng0830.album.common.TimeStamp;
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
	private MemberRole role;
	private String memberImageName;
	private String memberImagePath;
}

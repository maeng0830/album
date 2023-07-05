package com.maeng0830.album.member.domain;

import com.fasterxml.jackson.annotation.JsonManagedReference;
import com.maeng0830.album.common.model.entity.BaseEntity;
import com.maeng0830.album.common.model.image.Image;
import com.maeng0830.album.follow.domain.Follow;
import com.maeng0830.album.member.domain.MemberStatus.MemberStatusConvertor;
import com.maeng0830.album.member.dto.MemberDto;
import com.maeng0830.album.security.dto.LoginType;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import javax.persistence.Convert;
import javax.persistence.Embedded;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.OneToMany;
import javax.persistence.OrderBy;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.SuperBuilder;

@Entity
@Getter
@Setter
@SuperBuilder
@NoArgsConstructor
public class Member extends BaseEntity {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;
	private String username;
	private String nickname;
	private String password;
	private String phone;
	private LocalDateTime birthDate;
	@Convert(converter = MemberStatusConvertor.class)
	private MemberStatus status;
	@Enumerated(EnumType.STRING)
	private MemberRole role;

	@Embedded
	private Image image;

	@Enumerated(EnumType.STRING)
	private LoginType loginType;

	@Builder.Default
	@JsonManagedReference
	@OneToMany(mappedBy = "follower")
	private List<Follow> followers = new ArrayList<>();

	@Builder.Default
	@JsonManagedReference
	@OneToMany(mappedBy = "followee")
	private List<Follow> followees = new ArrayList<>();

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
				.loginType(memberDto.getLoginType())
				.image(memberDto.getImage())
				.createdAt(memberDto.getCreatedAt())
				.modifiedAt(memberDto.getModifiedAt())
				.modifiedBy(memberDto.getModifiedBy())
				.build();
	}

	public void changeStatus(MemberStatus status) {
		this.status = status;
	}
}

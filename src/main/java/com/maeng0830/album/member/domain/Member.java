package com.maeng0830.album.member.domain;

import com.fasterxml.jackson.annotation.JsonManagedReference;
import com.maeng0830.album.common.model.entity.BaseEntity;
import com.maeng0830.album.common.model.image.Image;
import com.maeng0830.album.follow.domain.Follow;
import com.maeng0830.album.member.domain.MemberStatus.MemberStatusConvertor;
import com.maeng0830.album.member.dto.MemberDto;
import com.maeng0830.album.member.dto.request.MemberModifiedForm;
import com.maeng0830.album.security.dto.LoginType;
import java.time.LocalDate;
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
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

@Entity
@Getter
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
	private LocalDate birthDate;
	@Convert(converter = MemberStatusConvertor.class)
	private MemberStatus status;
	@Enumerated(EnumType.STRING)
	private MemberRole role;

	@Embedded
	private Image image;

	@Enumerated(EnumType.STRING)
	private LoginType loginType;

	@Builder.Default
	@OneToMany(mappedBy = "follower")
	private List<Follow> followers = new ArrayList<>();

	@Builder.Default
	@OneToMany(mappedBy = "following")
	private List<Follow> followings = new ArrayList<>();

	public void changeStatus(MemberStatus status) {
		this.status = status;
	}

	public void modifiedBasicInfo(MemberModifiedForm memberModifiedForm) {
		this.nickname = memberModifiedForm.getNickname();
		this.phone = memberModifiedForm.getPhone();
		this.birthDate = memberModifiedForm.getBirthDate();
	}

	public void changePassword(String encodingPassword) {
		this.password = encodingPassword;
	}

	public void changeImage(Image image) {
		this.image = image;
	}
}

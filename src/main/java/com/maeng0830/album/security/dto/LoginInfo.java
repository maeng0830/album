package com.maeng0830.album.security.dto;

import com.maeng0830.album.member.domain.MemberRole;
import com.maeng0830.album.member.domain.MemberStatus;
import java.util.Set;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.ToString;

@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@ToString
public class LoginInfo {

	private Long id;
	private String loginIp;
	private String loginSessionId;
	private String username;
	private MemberRole memberRoles;
	private MemberStatus memberStatus;
	private LoginType loginType;
	private String password;
}

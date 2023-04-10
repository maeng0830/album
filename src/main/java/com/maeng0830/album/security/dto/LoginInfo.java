package com.maeng0830.album.security.dto;

import java.util.List;
import java.util.Set;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class LoginInfo {

	private String loginIp;
	private String loginSessionId;
	private String username;
	private Set<String> memberRoles;
}

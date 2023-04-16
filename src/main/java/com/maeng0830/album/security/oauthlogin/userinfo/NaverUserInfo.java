package com.maeng0830.album.security.oauthlogin.userinfo;

import com.maeng0830.album.security.dto.LoginType;
import java.util.Map;

public class NaverUserInfo implements OAuthUserInfo {

	private Map<String, Object> attributes;

	public NaverUserInfo(Map<String, Object> attributes) {
		this.attributes = attributes;
	}

	@Override
	public LoginType getLoginType() { return LoginType.OAUTH_NAVER; } // OAuth Type

	@Override
	public String getUsername() {
		return String.valueOf(attributes.get("email"));
	} // OAuth email
}

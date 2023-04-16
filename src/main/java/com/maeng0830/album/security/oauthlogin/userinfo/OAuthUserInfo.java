package com.maeng0830.album.security.oauthlogin.userinfo;

import com.maeng0830.album.security.dto.LoginType;

public interface OAuthUserInfo {
	LoginType getLoginType();
	String getUsername();
}

package com.maeng0830.album.common.util;

import com.maeng0830.album.member.dto.MemberDto;
import com.maeng0830.album.security.formlogin.PrincipalDetails;


public class AlbumUtil {

	// 로그인 여부 확인
	public MemberDto checkLogin(PrincipalDetails principalDetails) {
		try {
			return principalDetails.getMemberDto();
		} catch (NullPointerException e) {
			return null;
		}
	}

}

package com.maeng0830.album.common.util;

import static com.maeng0830.album.member.exception.MemberExceptionCode.REQUIRED_LOGIN;

import com.maeng0830.album.common.exception.AlbumException;
import com.maeng0830.album.member.dto.MemberDto;
import com.maeng0830.album.security.formlogin.PrincipalDetails;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;


public class AlbumUtil {

	// 로그인 여부 확인
	public MemberDto checkLogin(PrincipalDetails principalDetails) {
		try {
			return principalDetails.getMemberDto();
		} catch (NullPointerException e) {
			throw new AlbumException(REQUIRED_LOGIN, e);
		}
	}

}

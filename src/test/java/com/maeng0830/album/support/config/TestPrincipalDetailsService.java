package com.maeng0830.album.support.config;

import static com.maeng0830.album.member.domain.MemberStatus.NORMAL;

import com.maeng0830.album.common.filedir.FileDir;
import com.maeng0830.album.common.image.DefaultImage;
import com.maeng0830.album.common.model.image.Image;
import com.maeng0830.album.member.domain.MemberRole;
import com.maeng0830.album.member.dto.MemberDto;
import com.maeng0830.album.security.dto.LoginType;
import com.maeng0830.album.security.formlogin.PrincipalDetails;
import java.time.LocalDate;
import java.time.LocalDateTime;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;

public class TestPrincipalDetailsService implements UserDetailsService {

	@Autowired
	private FileDir fileDir;
	@Autowired
	private DefaultImage defaultImage;

	private MemberDto getMember() {
		return MemberDto.builder()
				.id(1L)
				.username("testMember@naver.com")
				.nickname("testMember")
				.status(NORMAL)
				.role(MemberRole.ROLE_MEMBER)
				.loginType(LoginType.FORM)
				.password("!@asd123")
				.phone("01011111111")
				.birthDate(LocalDate.now())
				.image(Image.createDefaultImage(fileDir, defaultImage.getMemberImage()))
				.createdAt(LocalDateTime.now())
				.modifiedAt(LocalDateTime.now())
				.modifiedBy("testMember@naver.com")
				.build();
	}

	private MemberDto getAdmin() {
		return MemberDto.builder()
				.id(2L)
				.username("testAdmin@naver.com")
				.nickname("testAdmin")
				.status(NORMAL)
				.role(MemberRole.ROLE_ADMIN)
				.loginType(LoginType.FORM)
				.password("!@asd123")
				.phone("01011111111")
				.birthDate(LocalDate.now())
				.image(Image.createDefaultImage(fileDir, defaultImage.getMemberImage()))
				.createdAt(LocalDateTime.now())
				.modifiedAt(LocalDateTime.now())
				.modifiedBy("testAdmin@naver.com")
				.build();
	}

	private MemberDto getOauth2Member() {
		return MemberDto.builder()
				.id(3L)
				.username("testOauth2Member@naver.com")
				.nickname("testOauth2Member")
				.status(NORMAL)
				.role(MemberRole.ROLE_MEMBER)
				.loginType(LoginType.OAUTH_GOOGLE)
				.password("!@asd123")
				.phone("01033333333")
				.birthDate(LocalDate.now())
				.image(Image.createDefaultImage(fileDir, defaultImage.getMemberImage()))
				.createdAt(LocalDateTime.now())
				.modifiedAt(LocalDateTime.now())
				.modifiedBy("testOauth2Member@naver.com")
				.build();
	}

	@Override
	public UserDetails loadUserByUsername(String role) throws UsernameNotFoundException {
		if (role.equals("member")) {
			return new PrincipalDetails(getMember());
		} else if (role.equals("admin")) {
			return new PrincipalDetails(getAdmin());
		} else if (role.equals("oauth2Member")){
			return new PrincipalDetails(getOauth2Member());
		}

		return null;
	}
}

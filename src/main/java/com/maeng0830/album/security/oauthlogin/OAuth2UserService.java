package com.maeng0830.album.security.oauthlogin;

import com.maeng0830.album.member.domain.Member;
import com.maeng0830.album.member.domain.MemberRole;
import com.maeng0830.album.member.domain.MemberStatus;
import com.maeng0830.album.member.dto.MemberDto;
import com.maeng0830.album.member.repository.MemberRepository;
import com.maeng0830.album.security.formlogin.PrincipalDetails;
import com.maeng0830.album.security.oauthlogin.userinfo.GoogleUserInfo;
import com.maeng0830.album.security.oauthlogin.userinfo.NaverUserInfo;
import com.maeng0830.album.security.oauthlogin.userinfo.OAuthUserInfo;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.oauth2.client.userinfo.DefaultOAuth2UserService;
import org.springframework.security.oauth2.client.userinfo.OAuth2UserRequest;
import org.springframework.security.oauth2.core.OAuth2AuthenticationException;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@RequiredArgsConstructor
public class OAuth2UserService extends DefaultOAuth2UserService {

	private final MemberRepository memberRepository;
	private final BCryptPasswordEncoder passwordEncoder;

	@Value("${oauth2.password}")
	private String oauth2Password;

	@Override
	public OAuth2User loadUser(OAuth2UserRequest userRequest) throws OAuth2AuthenticationException {

		// 회원 프로필 획득
		OAuth2User oAuth2User = super.loadUser(userRequest);

		OAuthUserInfo oAuthUserInfo = null;

		if (userRequest.getClientRegistration().getRegistrationId().equals("google")) {
			log.info("구글 로그인 요청");
			oAuthUserInfo = new GoogleUserInfo(oAuth2User.getAttributes());
		} else if (userRequest.getClientRegistration().getRegistrationId().equals("naver")) {
			log.info("네이버 로그인 요청");
			oAuthUserInfo = new NaverUserInfo(oAuth2User.getAttributes());
		} else {
			log.info("지원하지 않는 소셜 로그인 요청");
			return null;
		}

		Optional<Member> findMember = memberRepository.findByUsername(oAuthUserInfo.getUsername());

		if (findMember.isPresent()) {
			return new PrincipalDetails(MemberDto.from(findMember.get()), oAuth2User.getAttributes());
		} else {
			Member saveMember = memberRepository.save(
					Member.builder()
							.username(oAuthUserInfo.getUsername())
							.password(passwordEncoder.encode(oauth2Password))
							.status(MemberStatus.FIRST)
							.role(MemberRole.ROLE_MEMBER)
							.loginType(oAuthUserInfo.getLoginType())
							.build()
			);
			return new PrincipalDetails(MemberDto.from(saveMember), oAuth2User.getAttributes());
		}
	}
}

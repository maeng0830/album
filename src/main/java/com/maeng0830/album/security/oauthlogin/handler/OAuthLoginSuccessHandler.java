package com.maeng0830.album.security.oauthlogin.handler;

import static com.maeng0830.album.member.domain.MemberStatus.FIRST;
import static com.maeng0830.album.member.domain.MemberStatus.NORMAL;
import static com.maeng0830.album.member.exception.MemberExceptionCode.NOT_EXIST_MEMBER;

import com.maeng0830.album.common.exception.AlbumException;
import com.maeng0830.album.member.domain.Member;
import com.maeng0830.album.member.domain.MemberRole;
import com.maeng0830.album.member.domain.MemberStatus;
import com.maeng0830.album.member.dto.MemberDto;
import com.maeng0830.album.member.repository.MemberRepository;
import com.maeng0830.album.security.dto.LoginInfo;
import com.maeng0830.album.security.formlogin.PrincipalDetails;
import java.io.IOException;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.web.DefaultRedirectStrategy;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;
import org.springframework.security.web.authentication.WebAuthenticationDetails;
import org.springframework.security.web.savedrequest.HttpSessionRequestCache;
import org.springframework.security.web.savedrequest.SavedRequest;
import org.springframework.stereotype.Service;

@Service
@Slf4j
@RequiredArgsConstructor
public class OAuthLoginSuccessHandler implements AuthenticationSuccessHandler {

	private final MemberRepository memberRepository;
	private final BCryptPasswordEncoder passwordEncoder;
	@Value("${oauth2.password}")
	private String oauth2Password;

	@Override
	public void onAuthenticationSuccess(HttpServletRequest request, HttpServletResponse response,
										Authentication authentication)
			throws IOException, ServletException {

		// Authentication -> 정보 추출
		WebAuthenticationDetails web = (WebAuthenticationDetails) authentication.getDetails();
		PrincipalDetails principalDetails = (PrincipalDetails) authentication.getPrincipal();
		MemberDto memberDto = principalDetails.getMemberDto();

		// 로그인 정보 객체 생성 및 로그
		LoginInfo loginInfo = getLoginInfo(web, memberDto);

		// // 로그인 후 redirect
		redirectLogic(request, response, loginInfo);
	}

	private void redirectLogic(HttpServletRequest request, HttpServletResponse response,
							   LoginInfo loginInfo) throws IOException {
		SavedRequest savedRequest = new HttpSessionRequestCache().getRequest(request, response);
		DefaultRedirectStrategy redirectStrategy = new DefaultRedirectStrategy();

		// 로그인 페이지에서 로그인한 경우
		if (passwordEncoder.matches(oauth2Password, loginInfo.getPassword())) {
			log.info("소셜 로그인 비밀번호 설정이 되지 않았습니다. 비밀 번호 설정 페이지로 이동합니다.");
			redirectStrategy.sendRedirect(request, response, "/members/set-password-oauth2");
		} else if (loginInfo.getMemberStatus().equals(FIRST)) {
			log.info("첫 번째 소셜 로그인입니다. 추가 정보 입력 페이지로 이동합니다.");
			// memberStatus: FIRST -> NORMAL
			changeMemberStatus(loginInfo.getId());
			redirectStrategy.sendRedirect(request, response, "/members/my-profile");
		} else if (savedRequest == null) {
			redirectStrategy.sendRedirect(request, response, "/");
		} else {
			// 인증이 필요한 페이지에 미인증으로 접근하여 로그인한 경우
			String redirectUrl = getRedirectUrl(savedRequest);

			// 인증 권한에 따라 redirect
			if (redirectUrl.contains("admin") && loginInfo.getMemberRoles().equals(MemberRole.ROLE_ADMIN)) {
				redirectStrategy.sendRedirect(request, response, redirectUrl);
			} else if (redirectUrl.contains("admin") && !loginInfo.getMemberRoles().equals(MemberRole.ROLE_ADMIN)) {
				redirectStrategy.sendRedirect(request, response, "/");
			} else {
				redirectStrategy.sendRedirect(request, response, getRedirectUrl(savedRequest));
			}
		}
	}

	private LoginInfo getLoginInfo(WebAuthenticationDetails web, MemberDto memberDto) {

		LoginInfo loginInfo = LoginInfo.builder()
				.id(memberDto.getId())
				.loginIp(web.getRemoteAddress())
				.loginSessionId(web.getSessionId())
				.username(memberDto.getUsername())
				.memberRoles(memberDto.getRole())
				.memberStatus(memberDto.getStatus())
				.password(memberDto.getPassword())
				.build();

		log.info("loginInfo = {}", loginInfo);
		return loginInfo;
	}

	private String getRedirectUrl(SavedRequest savedRequest) {
		return savedRequest.getRedirectUrl();
	}

	private void changeMemberStatus(Long id) {
		Member loginMember = memberRepository.findById(id)
				.orElseThrow(() -> new AlbumException(NOT_EXIST_MEMBER));

		loginMember.changeStatus(NORMAL);

		memberRepository.save(loginMember);
	}
}

package com.maeng0830.album.security.loginhandler;

import com.maeng0830.album.member.domain.MemberRole;
import com.maeng0830.album.security.dto.LoginInfo;
import java.io.IOException;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.authority.AuthorityUtils;
import org.springframework.security.web.DefaultRedirectStrategy;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;
import org.springframework.security.web.authentication.WebAuthenticationDetails;
import org.springframework.security.web.savedrequest.HttpSessionRequestCache;
import org.springframework.security.web.savedrequest.SavedRequest;
import org.springframework.stereotype.Service;

@Slf4j
@Service
public class LoginSuccessHandler implements AuthenticationSuccessHandler {

	@Override
	public void onAuthenticationSuccess(HttpServletRequest request, HttpServletResponse response,
										Authentication authentication)
			throws IOException, ServletException {

		// 로그인 정보 객체 생성 및 로그
		LoginInfo loginInfo = getLoginInfo(authentication);

		// 로그인 후 redirect
		redirectLogic(request, response, loginInfo);
	}

	private void redirectLogic(HttpServletRequest request, HttpServletResponse response,
						   LoginInfo loginInfo) throws IOException {
		SavedRequest savedRequest = new HttpSessionRequestCache().getRequest(request, response);
		DefaultRedirectStrategy redirectStrategy = new DefaultRedirectStrategy();

		// 로그인 페이지에서 로그인한 경우
		if (savedRequest == null) {
			redirectStrategy.sendRedirect(request, response, "/");
		} else {
			// 인증이 필요한 페이지에 미인증으로 접근하여 로그인한 경우
			String redirectUrl = getRedirectUrl(savedRequest);

			if (redirectUrl.contains("admin") && loginInfo.getMemberRoles().contains(
					MemberRole.ADMIN)) {
				redirectStrategy.sendRedirect(request, response, redirectUrl);
			} else if (redirectUrl.contains("admin") && !loginInfo.getMemberRoles().contains(MemberRole.ADMIN)) {
				redirectStrategy.sendRedirect(request, response, "/");
			} else {
				redirectStrategy.sendRedirect(request, response, getRedirectUrl(savedRequest));
			}
		}
	}

	private LoginInfo getLoginInfo(Authentication authentication) {
		WebAuthenticationDetails web = (WebAuthenticationDetails) authentication.getDetails();

		LoginInfo loginInfo = LoginInfo.builder()
				.loginIp(web.getRemoteAddress())
				.loginSessionId(web.getSessionId())
				.username(authentication.getName())
				.memberRoles(
						AuthorityUtils.authorityListToSet(authentication.getAuthorities()))
				.build();

		log.info("loginInfo = {}", loginInfo);
		return loginInfo;
	}

	private String getRedirectUrl(SavedRequest savedRequest) {
		return savedRequest.getRedirectUrl();
	}
}

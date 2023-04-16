package com.maeng0830.album.security.oauthlogin.handler;

import java.io.IOException;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.authentication.AuthenticationFailureHandler;
import org.springframework.stereotype.Service;

@Service
@Slf4j
public class OAuthLoginFailureHandler implements AuthenticationFailureHandler {

	@Override
	public void onAuthenticationFailure(HttpServletRequest request, HttpServletResponse response,
										AuthenticationException exception)
			throws IOException, ServletException {

			response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
			response.getWriter().write("소셜 로그인 실패. 서버 로그를 확인해주세요.");
			log.info("소셜 로그인 실패. {}", exception.getMessage());
	}
}

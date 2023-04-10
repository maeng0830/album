package com.maeng0830.album.security.loginhandler;

import java.io.IOException;
import javax.servlet.RequestDispatcher;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.DisabledException;
import org.springframework.security.authentication.InternalAuthenticationServiceException;
import org.springframework.security.authentication.LockedException;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.authentication.AuthenticationFailureHandler;
import org.springframework.stereotype.Service;

@Service
public class LoginFailureHandler implements AuthenticationFailureHandler {

	@Override
	public void onAuthenticationFailure(HttpServletRequest request, HttpServletResponse response,
										AuthenticationException exception)
			throws IOException, ServletException {

		if (exception instanceof InternalAuthenticationServiceException) {
			request.setAttribute("loginFailMsg", "아이디 또는 비밀번호가 틀렸습니다.");
		} else if (exception instanceof BadCredentialsException) {
			request.setAttribute("loginFailMsg", "아이디 또는 비밀번호가 틀렸습니다.");
		} else if (exception instanceof LockedException) {
			request.setAttribute("loginFailMsg", "정지된 회원입니다.");
		} else if (exception instanceof DisabledException) {
			request.setAttribute("loginFailMsg", "인증이 필요한 회원입니다.");
		}

		RequestDispatcher dispatcher = request.getRequestDispatcher("/loginForm");
		dispatcher.forward(request, response);
	}
}

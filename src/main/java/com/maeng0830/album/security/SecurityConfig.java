package com.maeng0830.album.security;

import com.maeng0830.album.security.formlogin.handler.FormLoginFailureHandler;
import com.maeng0830.album.security.formlogin.handler.FormLoginSuccessHandler;
import com.maeng0830.album.security.oauthlogin.handler.OAuthLoginFailureHandler;
import com.maeng0830.album.security.oauthlogin.handler.OAuthLoginSuccessHandler;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.method.configuration.EnableGlobalMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.web.SecurityFilterChain;

@EnableGlobalMethodSecurity(prePostEnabled=true)
@EnableWebSecurity
@Configuration
@RequiredArgsConstructor
public class SecurityConfig {

	private final FormLoginSuccessHandler formLoginSuccessHandler;
	private final FormLoginFailureHandler formLoginFailureHandler;
	private final OAuthLoginSuccessHandler oAuthLoginSuccessHandler;
	private final OAuthLoginFailureHandler oAuthLoginFailureHandler;

	@Bean
	public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
		http.csrf().disable();

		http.authorizeRequests()
//				.antMatchers("/members/**").access("hasRole('ADMIN') or hasRole('MEMBER')")
				.antMatchers("/follows/**").access("hasRole('ADMIN') or hasRole('MEMBER')")
				.antMatchers("/admin/**").access("hasRole('ADMIN')")
				.antMatchers("/static/css/**").permitAll()
				.antMatchers("/static/js/**").permitAll()
				.antMatchers("/static/file/**").permitAll()
				.anyRequest().permitAll()
				.and()
				.formLogin()
				.loginPage("/require-login")
				.loginProcessingUrl("/login")
				.successHandler(formLoginSuccessHandler)
				.failureHandler(formLoginFailureHandler)
				.and()
				.oauth2Login()
				.successHandler(oAuthLoginSuccessHandler)
				.failureHandler(oAuthLoginFailureHandler)
				.and()
				.logout()
				.logoutSuccessUrl("/")
				.and()
				.exceptionHandling().accessDeniedPage("/fail-authorize");

		return http.build();
	}
}

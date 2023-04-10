package com.maeng0830.album.security;

import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.AuthenticationFailureHandler;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;

@Configuration
@RequiredArgsConstructor
public class SecurityConfig {

	private final AuthenticationFailureHandler authenticationFailureHandler;
	private final AuthenticationSuccessHandler authenticationSuccessHandler;

	@Bean
	public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
		http.csrf().disable();

		http.authorizeRequests()
				.antMatchers("/members/**").access("hasRole('ADMIN') or hasRole('MEMBER')")
				.antMatchers("**/admin/**").access("hasRole('ADMIN')")
				.anyRequest().permitAll()
				.and()
				.formLogin()
				.loginPage("/loginForm")
				.loginProcessingUrl("/login")
				.successHandler(authenticationSuccessHandler)
				.failureHandler(authenticationFailureHandler);

		return http.build();
	}
}

package com.maeng0830.album.security;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
public class SecurityConfig {

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
				.defaultSuccessUrl("/");

		return http.build();
	}
}

package com.maeng0830.album.common.util;

import com.maeng0830.album.security.formlogin.PrincipalDetails;
import java.util.Optional;
import org.springframework.context.annotation.Profile;
import org.springframework.data.domain.AuditorAware;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;

@Profile(value = {"dev", "prod"})
@Component
public class SpringSecurityAuditorAware implements AuditorAware<String> {

	@Override
	public Optional<String> getCurrentAuditor() {
		Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

		if (authentication == null || !authentication.isAuthenticated()) {
			return Optional.empty();
		} else {
			PrincipalDetails principalDetails = (PrincipalDetails) authentication.getPrincipal();
			String username = principalDetails.getUsername();
			return Optional.ofNullable(username);
		}
	}
}

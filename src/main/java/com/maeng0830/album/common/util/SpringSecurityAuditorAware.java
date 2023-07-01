package com.maeng0830.album.common.util;

import com.maeng0830.album.security.formlogin.PrincipalDetails;
import java.util.Optional;
import org.springframework.context.annotation.Profile;
import org.springframework.data.domain.AuditorAware;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;

@Profile(value = {"dev", "prod"})
@Component
public class SpringSecurityAuditorAware implements AuditorAware<String> {

	@Override
	public Optional<String> getCurrentAuditor() {

		PrincipalDetails principalDetails = (PrincipalDetails) SecurityContextHolder.getContext()
				.getAuthentication();

		String username = principalDetails.getUsername();

		return Optional.of(username);
	}
}

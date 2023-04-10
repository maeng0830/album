package com.maeng0830.album.security;

import com.maeng0830.album.member.domain.MemberStatus;
import com.maeng0830.album.member.dto.MemberDto;
import java.util.ArrayList;
import java.util.Collection;
import lombok.Getter;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

@Getter
public class PrincipalDetails implements UserDetails {

	private final MemberDto memberDto;

	public PrincipalDetails(MemberDto memberDto) {
		this.memberDto = memberDto;
	}

	@Override
	public Collection<? extends GrantedAuthority> getAuthorities() {
		Collection<GrantedAuthority> collection = new ArrayList<>();
		collection.add(new GrantedAuthority() {
			@Override
			public String getAuthority() {
				return memberDto.getRole().name();
			}
		});
		return collection;
	}

	@Override
	public String getPassword() {
		return memberDto.getPassword();
	}

	@Override
	public String getUsername() {
		return memberDto.getUsername();
	}

	@Override
	public boolean isAccountNonExpired() {
		return true;
	}

	@Override
	public boolean isAccountNonLocked() {
		return !memberDto.getStatus().equals(MemberStatus.LOCKED);
	}

	@Override
	public boolean isCredentialsNonExpired() {
		return true;
	}

	@Override
	public boolean isEnabled() {
		return !memberDto.getStatus().equals(MemberStatus.REQUIRED);
	}
}

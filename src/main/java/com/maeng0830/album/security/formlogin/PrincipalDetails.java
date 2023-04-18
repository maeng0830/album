package com.maeng0830.album.security.formlogin;

import com.maeng0830.album.member.domain.MemberStatus;
import com.maeng0830.album.member.dto.MemberDto;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Map;
import lombok.Getter;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.oauth2.core.user.OAuth2User;

@Getter
public class PrincipalDetails implements UserDetails, OAuth2User {

	private MemberDto memberDto;
	private Map<String, Object> attributes;

	public PrincipalDetails(MemberDto memberDto) {
		this.memberDto = memberDto;
	}

	public PrincipalDetails(MemberDto memberDto, Map<String, Object> attributes) {
		this.memberDto = memberDto;
		this.attributes = attributes;
	}

	@Override
	public Collection<? extends GrantedAuthority> getAuthorities() {
		Collection<GrantedAuthority> collection = new ArrayList<>();
		collection.add(new GrantedAuthority() {
			@Override
			public String getAuthority() {
				return memberDto.getRole().toString();
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
		return true;
	}

	//OAuth2
	@Override
	public Map<String, Object> getAttributes() {
		return attributes;
	}

	//OAuth2
	@Override
	public String getName() {
		return String.valueOf(attributes.get("sub"));
	}
}

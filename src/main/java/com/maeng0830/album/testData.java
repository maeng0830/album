package com.maeng0830.album;

import com.maeng0830.album.member.domain.Member;
import com.maeng0830.album.member.domain.MemberRole;
import com.maeng0830.album.member.domain.MemberStatus;
import com.maeng0830.album.member.repository.MemberRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.event.EventListener;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class testData {

	private final MemberRepository memberRepository;
	private final BCryptPasswordEncoder passwordEncoder;

	@EventListener(ApplicationReadyEvent.class)
	public void init() {
		Member member = Member.builder()
				.username("member@naver.com")
				.password(passwordEncoder.encode("123"))
				.role(MemberRole.ROLE_MEMBER)
				.status(MemberStatus.NORMAL)
				.build();
		memberRepository.save(member);

		Member admin = Member.builder()
				.username("admin@naver.com")
				.password(passwordEncoder.encode("123"))
				.role(MemberRole.ROLE_ADMIN)
				.status(MemberStatus.NORMAL)
				.build();

		memberRepository.save(admin);
	}
}

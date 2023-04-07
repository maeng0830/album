package com.maeng0830.album.member.service;

import com.maeng0830.album.common.exception.AlbumException;
import com.maeng0830.album.member.domain.Member;
import com.maeng0830.album.member.domain.MemberRole;
import com.maeng0830.album.member.domain.MemberStatus;
import com.maeng0830.album.member.dto.MemberDto;
import com.maeng0830.album.member.exception.SpringSecurityExceptionCode;
import com.maeng0830.album.member.repository.MemberRepository;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class SpringSecurityService {

	private final MemberRepository memberRepository;
	private final BCryptPasswordEncoder passwordEncoder;

	public MemberDto join(MemberDto memberDto) {
		// username 존재 여부 확인
		Optional<Member> optionalMember = memberRepository.findByUsernameOrNickname(
				memberDto.getUsername(), memberDto.getNickname());
		if (optionalMember.isPresent()) {
			Member member = optionalMember.get();

			if (memberDto.getUsername().equals(member.getUsername())) {
				throw new AlbumException(SpringSecurityExceptionCode.EXIST_USERNAME);
			} else {
				throw new AlbumException(SpringSecurityExceptionCode.EXIST_NICKNAME);
			}
		}

		// 비밀번호 암호화, 상태 및 권한 설정 -> DB 저장(회원가입)
		memberDto.setPassword(passwordEncoder.encode(memberDto.getPassword()));
		memberDto.setStatus(MemberStatus.REQUIRED);
		memberDto.setRole(MemberRole.MEMBER);
		Member saveMember = memberRepository.save(Member.from(memberDto));

		// 회원가입 멤버 반환
		return MemberDto.from(saveMember);
	}
}

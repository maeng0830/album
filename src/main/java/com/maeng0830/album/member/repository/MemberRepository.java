package com.maeng0830.album.member.repository;

import com.maeng0830.album.member.domain.Member;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;

public interface MemberRepository extends JpaRepository<Member, Long> {

	Optional<Member> findByUsernameOrNickname(String username, String nickname);

	Optional<Member> findByUsername(String username);
}

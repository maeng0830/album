package com.maeng0830.album.member.repository;

import com.maeng0830.album.member.domain.Member;
import com.maeng0830.album.member.repository.custom.MemberRepositoryCustom;
import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;

public interface MemberRepository extends JpaRepository<Member, Long>, MemberRepositoryCustom {

	List<Member> findByUsernameOrNickname(String username, String nickname);

	Optional<Member> findByNickname(String nickname);

	Optional<Member> findByUsername(String username);
}

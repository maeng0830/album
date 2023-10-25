package com.maeng0830.album.member.repository;

import static com.maeng0830.album.member.domain.MemberStatus.NORMAL;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.tuple;

import com.maeng0830.album.member.domain.Member;
import com.maeng0830.album.member.domain.MemberStatus;
import com.maeng0830.album.member.repository.MemberRepository;
import com.maeng0830.album.support.RepositoryTestSupport;
import java.util.List;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;

class MemberRepositoryTest extends RepositoryTestSupport {

	@Autowired
	private MemberRepository memberRepository;

	@DisplayName("주어진 username 또는 nickname에 일치하는 회원들을 조회할 수 있다.")
	@Test
	void findByUsernameOrNickname() {
		// given
		Member member1 = Member.builder()
				.username("member1_username")
				.nickname("member1_nickname")
				.build();
		Member member2 = Member.builder()
				.username("member2_username")
				.nickname("member2_nickname")
				.build();
		memberRepository.saveAll(List.of(member1, member2));

		// when
		List<Member> result1 = memberRepository.findByUsernameOrNickname(member1.getUsername(),
				null);
		List<Member> result2 = memberRepository.findByUsernameOrNickname(null,
				member1.getNickname());
		List<Member> result3 = memberRepository.findByUsernameOrNickname(member2.getUsername(),
				null);
		List<Member> result4 = memberRepository.findByUsernameOrNickname(null,
				member2.getNickname());
		List<Member> result5 = memberRepository.findByUsernameOrNickname(member1.getUsername(),
				member2.getNickname());

		// then
		assertThat(result1).hasSize(1)
				.extracting("username", "nickname")
				.containsExactlyInAnyOrder(tuple(member1.getUsername(), member1.getNickname()));
		assertThat(result2).hasSize(1)
				.extracting("username", "nickname")
				.containsExactlyInAnyOrder(tuple(member1.getUsername(), member1.getNickname()));
		assertThat(result3).hasSize(1)
				.extracting("username", "nickname")
				.containsExactlyInAnyOrder(tuple(member2.getUsername(), member2.getNickname()));
		assertThat(result4).hasSize(1)
				.extracting("username", "nickname")
				.containsExactlyInAnyOrder(tuple(member2.getUsername(), member2.getNickname()));
		assertThat(result5).hasSize(2)
				.extracting("username", "nickname")
				.containsExactlyInAnyOrder(
						tuple(member1.getUsername(), member1.getNickname()),
						tuple(member2.getUsername(), member2.getNickname())
				);
	}

	@DisplayName("주어진 username에 일치하는 회원을 조회할 수 있다.")
	@Test
	void findByUsername() {
		// given
		Member member1 = Member.builder()
				.username("member1_username")
				.nickname("member1_nickname")
				.build();
		Member member2 = Member.builder()
				.username("member2_username")
				.nickname("member2_nickname")
				.build();
		memberRepository.saveAll(List.of(member1, member2));

		// when
		Member result1 = memberRepository.findByUsername(member1.getUsername()).get();
		Member result2 = memberRepository.findByUsername(member2.getUsername()).get();
		// then
		assertThat(result1).usingRecursiveComparison().isEqualTo(member1);
		assertThat(result2).usingRecursiveComparison().isEqualTo(member2);
	}

	@DisplayName("주어진 nickname에 일치하는 회원을 조회할 수 있다.")
	@Test
	void findByNickname() {
		// given
		Member member1 = Member.builder()
				.username("member1_username")
				.nickname("member1_nickname")
				.build();
		Member member2 = Member.builder()
				.username("member2_username")
				.nickname("member2_nickname")
				.build();
		memberRepository.saveAll(List.of(member1, member2));

		// when
		Member result1 = memberRepository.findByNickname(member1.getNickname()).get();
		Member result2 = memberRepository.findByNickname(member2.getNickname()).get();
		// then
		assertThat(result1).usingRecursiveComparison().isEqualTo(member1);
		assertThat(result2).usingRecursiveComparison().isEqualTo(member2);
	}

	@DisplayName("주어진 searchText와 username 또는 nickname이 전방 일치하는 회원들을 조회할 수 있다.")
	@Test
	void searchBySearchText() {
		// given
		// 회원 세팅
		Member member1 = Member.builder()
				.status(NORMAL)
				.username("username1")
				.nickname("nickname1")
				.build();
		Member member2 = Member.builder()
				.status(NORMAL)
				.username("username2")
				.nickname("nickname2")
				.build();
		memberRepository.saveAll(List.of(member1, member2));

		// 데이터 조회 조건 세팅
		List<MemberStatus> statuses = List.of(NORMAL);
		PageRequest pageRequest = PageRequest.of(0, 20);

		// when
		Page<Member> result1 = memberRepository.searchBySearchText(statuses, "username1",
				pageRequest);
		Page<Member> result2 = memberRepository.searchBySearchText(statuses, "nickname1",
				pageRequest);
		Page<Member> result3 = memberRepository.searchBySearchText(statuses, "username2",
				pageRequest);
		Page<Member> result4 = memberRepository.searchBySearchText(statuses, "nickname2",
				pageRequest);
		Page<Member> result5 = memberRepository.searchBySearchText(statuses, "username",
				pageRequest);
		Page<Member> result6 = memberRepository.searchBySearchText(statuses, "nickname",
				pageRequest);

		// then
		assertThat(result1.getContent()).hasSize(1)
				.extracting("username", "nickname")
				.containsExactlyInAnyOrder(tuple(member1.getUsername(), member1.getNickname()));
		assertThat(result2.getContent()).hasSize(1)
				.extracting("username", "nickname")
				.containsExactlyInAnyOrder(tuple(member1.getUsername(), member1.getNickname()));
		assertThat(result3.getContent()).hasSize(1)
				.extracting("username", "nickname")
				.containsExactlyInAnyOrder(tuple(member2.getUsername(), member2.getNickname()));
		assertThat(result4.getContent()).hasSize(1)
				.extracting("username", "nickname")
				.containsExactlyInAnyOrder(tuple(member2.getUsername(), member2.getNickname()));
		assertThat(result5.getContent()).hasSize(2)
				.extracting("username", "nickname")
				.containsExactlyInAnyOrder(
						tuple(member1.getUsername(), member1.getNickname()),
						tuple(member2.getUsername(), member2.getNickname()));
		assertThat(result6.getContent()).hasSize(2)
				.extracting("username", "nickname")
				.containsExactlyInAnyOrder(
						tuple(member1.getUsername(), member1.getNickname()),
						tuple(member2.getUsername(), member2.getNickname()));
	}
}
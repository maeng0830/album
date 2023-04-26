package com.maeng0830.album;

import com.maeng0830.album.feed.domain.Feed;
import com.maeng0830.album.feed.domain.FeedStatus;
import com.maeng0830.album.feed.repository.FeedRepository;
import com.maeng0830.album.follow.domain.Follow;
import com.maeng0830.album.follow.repository.FollowRepository;
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
	private final FollowRepository followRepository;
	private final FeedRepository feedRepository;

	@EventListener(ApplicationReadyEvent.class)
	public void init() {
		// 관리자 데이터 추가
		Member admin = Member.builder()
				.username("admin@naver.com")
				.password(passwordEncoder.encode("123"))
				.role(MemberRole.ROLE_ADMIN)
				.status(MemberStatus.NORMAL)
				.build();

		memberRepository.save(admin);

		// 일반 회원 데이터 추가
		for (int i = 0; i < 10; i++) {
			Member member = Member.builder()
					.username((i + 1) + "member@naver.com")
					.password(passwordEncoder.encode("123"))
					.role(MemberRole.ROLE_MEMBER)
					.status(MemberStatus.NORMAL)
					.build();
			memberRepository.save(member);

			// 팔로우 데이터 추가
			followRepository.save(Follow.builder()
					.follower(admin)
					.followee(member)
					.build());

			followRepository.save(Follow.builder()
					.follower(member)
					.followee(admin)
					.build());

			// 피드 데이터 추가
			if (i < 5) {
				feedRepository.save(Feed.builder()
								.member(member)
						.createdBy(member.getUsername())
						.status(FeedStatus.NORMAL)
						.build());
			} else {
				feedRepository.save(Feed.builder()
								.member(member)
						.createdBy("NOT_follow" + member.getUsername())
						.status(FeedStatus.NORMAL)
						.build());
			}
		}
	}
}

package com.maeng0830.album;

import com.maeng0830.album.comment.domain.Comment;
import com.maeng0830.album.comment.domain.CommentStatus;
import com.maeng0830.album.comment.repository.CommentRepository;
import com.maeng0830.album.common.exception.AlbumException;
import com.maeng0830.album.common.filedir.FileDir;
import com.maeng0830.album.common.image.DefaultImage;
import com.maeng0830.album.common.model.image.Image;
import com.maeng0830.album.feed.domain.Feed;
import com.maeng0830.album.feed.domain.FeedImage;
import com.maeng0830.album.feed.domain.FeedStatus;
import com.maeng0830.album.feed.repository.FeedImageRepository;
import com.maeng0830.album.feed.repository.FeedRepository;
import com.maeng0830.album.follow.domain.Follow;
import com.maeng0830.album.follow.repository.FollowRepository;
import com.maeng0830.album.member.domain.Member;
import com.maeng0830.album.member.domain.MemberRole;
import com.maeng0830.album.member.domain.MemberStatus;
import com.maeng0830.album.member.repository.MemberRepository;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.annotation.Profile;
import org.springframework.context.event.EventListener;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Component;

@Component
@Profile("dev")
@RequiredArgsConstructor
public class testData {

	private final MemberRepository memberRepository;
	private final BCryptPasswordEncoder passwordEncoder;
	private final FollowRepository followRepository;
	private final FeedRepository feedRepository;
	private final FeedImageRepository feedImageRepository;
	private final CommentRepository commentRepository;
	private final FileDir fileDir;
	private final DefaultImage defaultImage;

	@EventListener(ApplicationReadyEvent.class)
	public void init() throws FileNotFoundException {
		Image defaultMemberImage = Image.createDefaultImage(fileDir, defaultImage.getMemberImage());
		Image defaultFeedImage = Image.createDefaultImage(fileDir, defaultImage.getFeedImage());

		// 관리자 데이터 추가
		Member admin = Member.builder()
				.username("admin@naver.com")
				.password(passwordEncoder.encode("!@asd123"))
				.nickname("admin")
				.birthDate(LocalDate.now())
				.phone("01000000000")
				.role(MemberRole.ROLE_ADMIN)
				.status(MemberStatus.NORMAL)
				.image(defaultMemberImage)
				.build();

		memberRepository.save(admin);

		for (int i = 0; i < 10; i++) {
			// 일반 회원 데이터 추가
			Member member = Member.builder()
					.username((i + 1) + "member@naver.com")
					.nickname((i + 1) + "member")
					.password(passwordEncoder.encode("!@asd123"))
					.role(MemberRole.ROLE_MEMBER)
					.status(MemberStatus.NORMAL)
					.image(defaultMemberImage)
					.build();
			memberRepository.save(member);

			// 팔로우 데이터 추가
			followRepository.save(Follow.builder()
					.follower(admin)
					.following(member)
					.build());

			followRepository.save(Follow.builder()
					.follower(member)
					.following(admin)
					.build());

			// 피드 데이터 추가
			Feed feed = feedRepository.save(Feed.builder()
					.title("title" + i)
					.member(member)
					.status(FeedStatus.NORMAL)
					.build());

			// 피드 이미지 데이터 추가
			FeedImage feedImage = FeedImage.builder()
					.feed(feed)
					.image(defaultFeedImage)
					.build();

			feedImageRepository.save(feedImage);
		}

		for (int i = 0; i < 2; i++) {
			Member member = memberRepository.findByUsername((i + 1) + "member@naver.com").get();
			Feed feed = feedRepository.findById(1L).get();

			Comment comment = Comment.builder()
					.member(member)
					.feed(feed)
					.status(CommentStatus.NORMAL)
					.content("groupCommentContent")
					.build();

			comment.saveGroup(null);
			comment.saveParent(null);

			commentRepository.save(comment);
		}

		for (int i = 2; i < 10; i++) {
			Member member = memberRepository.findByUsername((i + 1) + "member@naver.com").get();
			Feed feed = feedRepository.findById(1L).get();

			Comment comment = Comment.builder()
					.member(member)
					.feed(feed)
					.status(CommentStatus.NORMAL)
					.content("BasicCommentContent")
					.build();

			if (i % 2 == 0) {
				Comment groupComment = commentRepository.findById(1L).get();
				comment.saveGroup(groupComment);
				comment.saveParent(groupComment);
				commentRepository.save(comment);
			} else {
				Comment groupComment = commentRepository.findById(2L).get();
				comment.saveGroup(groupComment);
				comment.saveParent(groupComment);
				commentRepository.save(comment);
			}
		}
	}
}

package com.maeng0830.album.feed.service;

import static com.maeng0830.album.feed.domain.FeedStatus.*;
import static org.assertj.core.api.Assertions.*;
import static org.junit.jupiter.api.Assertions.*;

import com.maeng0830.album.common.filedir.FileDir;
import com.maeng0830.album.feed.domain.Feed;
import com.maeng0830.album.feed.domain.FeedImage;
import com.maeng0830.album.feed.domain.FeedStatus;
import com.maeng0830.album.feed.dto.FeedResponse;
import com.maeng0830.album.feed.repository.FeedRepository;
import com.maeng0830.album.follow.domain.Follow;
import com.maeng0830.album.follow.repository.FollowRepository;
import com.maeng0830.album.member.domain.Member;
import com.maeng0830.album.member.dto.MemberDto;
import com.maeng0830.album.member.repository.MemberRepository;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import org.assertj.core.api.Assertions;
import org.assertj.core.groups.Tuple;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

@ActiveProfiles("test")
@Transactional
@SpringBootTest
class FeedServiceTest {

	@Autowired
	private FeedService feedService;

	@Autowired
	private MemberRepository memberRepository;
	@Autowired
	private FeedRepository feedRepository;
	@Autowired
	private FollowRepository followRepository;

	@Autowired
	private FileDir fileDir;

	@DisplayName("주어진 Feed에 대한 FeedImage를 등록할 수 있다")
	@Test
	void saveFeedImage() throws IOException {
		// given
		List<MultipartFile> imageFiles = new ArrayList<>();
		for (int i = 0; i < 3; i++) {
			imageFiles.add(createImageFile("imageFile", "testImage.png",
					"multipart/mixed", fileDir));
		}

		Feed feed = Feed.builder()
				.status(NORMAL)
				.build();
		feedRepository.save(feed);

		// when
		List<FeedImage> feedImages = feedService.saveFeedImage(imageFiles, feed);

		// then
		assertThat(feedImages).hasSize(3)
				.extracting("feed", "image.imageOriginalName")
				.containsExactlyInAnyOrder(
						tuple(feed, "testImage.png"),
						tuple(feed, "testImage.png"),
						tuple(feed, "testImage.png")
				);
	}

	@DisplayName("정상 및 신고 상태인 피드 목록을 조회할 수 있다. 로그인 했을 경우, 팔로우 및 팔로워의 피드를 조회한다.")
	@Test
	void getFeeds() {
		// given
		// 멤버 세팅
		Member loginMember = Member.builder()
				.username("loginMember")
				.build();
		Member followerMember = Member.builder()
				.username("followerMember")
				.build();
		Member followeeMember = Member.builder()
				.username("followeeMember")
				.build();
		Member noRelationMember = Member.builder()
				.username("noRelationMember")
				.build();
		memberRepository.saveAll(
				List.of(loginMember, followerMember, followeeMember, noRelationMember));

		// 팔로우 세팅
		Follow follow1 = Follow.builder()
				.follower(followerMember)
				.followee(loginMember)
				.build();
		Follow follow2 = Follow.builder()
				.follower(loginMember)
				.followee(followeeMember)
				.build();
		followRepository.saveAll(List.of(follow1, follow2));

		// 피드 세팅
		Feed followerFeed = Feed.builder()
				.status(NORMAL)
				.member(followerMember)
				.createdBy("followerMember")
				.build();
		Feed followeeFeed = Feed.builder()
				.status(ACCUSE)
				.member(followeeMember)
				.createdBy("followeeMember")
				.build();
		Feed noRelationFeed = Feed.builder()
				.status(NORMAL)
				.member(noRelationMember)
				.createdBy("noRelationMember")
				.build();
		feedRepository.saveAll(List.of(followerFeed, followeeFeed, noRelationFeed));

		// when
		List<FeedResponse> feeds = feedService.getFeeds(MemberDto.from(loginMember));

		// then
		assertThat(feeds).hasSize(2)
				.extracting("createdBy")
				.containsExactlyInAnyOrder(followerMember.getUsername(),
						followeeMember.getUsername());
	}

	@DisplayName("정상 및 신고 상태인 피드 목록을 조회할 수 있다. 로그인 안했을 경우, 모든 피드를 조회한다.")
	@Test
	void getFeeds_noLogin() {
		// given
		// 멤버 세팅
		Member loginMember = Member.builder()
				.username("loginMember")
				.build();
		Member followerMember = Member.builder()
				.username("followerMember")
				.build();
		Member followeeMember = Member.builder()
				.username("followeeMember")
				.build();
		Member noRelationMember = Member.builder()
				.username("noRelationMember")
				.build();
		memberRepository.saveAll(
				List.of(loginMember, followerMember, followeeMember, noRelationMember));

		// 팔로우 세팅
		Follow follow1 = Follow.builder()
				.follower(followerMember)
				.followee(loginMember)
				.build();
		Follow follow2 = Follow.builder()
				.follower(loginMember)
				.followee(followeeMember)
				.build();
		followRepository.saveAll(List.of(follow1, follow2));

		// 피드 세팅
		Feed followerFeed = Feed.builder()
				.status(NORMAL)
				.member(followerMember)
				.createdBy("followerMember")
				.build();
		Feed followeeFeed = Feed.builder()
				.status(ACCUSE)
				.member(followeeMember)
				.createdBy("followeeMember")
				.build();
		Feed noRelationFeed = Feed.builder()
				.status(NORMAL)
				.member(noRelationMember)
				.createdBy("noRelationMember")
				.build();
		feedRepository.saveAll(List.of(followerFeed, followeeFeed, noRelationFeed));

		// when
		List<FeedResponse> feeds = feedService.getFeeds(null);

		// then
		assertThat(feeds).hasSize(3)
				.extracting("createdBy")
				.containsExactlyInAnyOrder(
						followerMember.getUsername(), followeeMember.getUsername(),
						noRelationMember.getUsername());
	}

	@DisplayName("피드 아이디를 통해 해당하는 피드를 조회할 수 있다")
	@Test
	void getFeed() {
		// given
		Feed feed1 = Feed.builder()
				.title("feed1")
				.build();
		Feed feed2 = Feed.builder()
				.title("feed1")
				.build();
		Feed feed3 = Feed.builder()
				.title("feed1")
				.build();
		feedRepository.saveAll(List.of(feed1, feed2, feed3));

		// when
		FeedResponse feedResponse = feedService.getFeed(feed2.getId());

		// then
		assertThat(feedResponse.getTitle())
				.isEqualTo(feed2.getTitle());
	}

	@DisplayName("로그인 했을 경우, 피드를 등록할 수 있습니다.")
	@Test
	void feed() {
		// given

		// when

		// then
	}

	private MockMultipartFile createImageFile(String name, String originalFilename,
											  String contentType, FileDir fileDir)
			throws IOException {
		String filePath = fileDir.getDir() + originalFilename;
		FileInputStream fileInputStream = new FileInputStream(new File(filePath));

		return new MockMultipartFile(name,
				originalFilename, contentType, fileInputStream);
	}
}
package com.maeng0830.album.feed.service;

import static com.maeng0830.album.feed.domain.FeedStatus.ACCUSE;
import static com.maeng0830.album.feed.domain.FeedStatus.DELETE;
import static com.maeng0830.album.feed.domain.FeedStatus.NORMAL;
import static com.maeng0830.album.member.domain.MemberRole.ROLE_ADMIN;
import static com.maeng0830.album.member.domain.MemberRole.ROLE_MEMBER;
import static com.maeng0830.album.member.exception.MemberExceptionCode.NO_AUTHORITY;
import static com.maeng0830.album.member.exception.MemberExceptionCode.REQUIRED_LOGIN;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.assertj.core.api.Assertions.tuple;
import static org.junit.jupiter.api.Assertions.assertThrows;

import com.maeng0830.album.common.exception.AlbumException;
import com.maeng0830.album.common.filedir.FileDir;
import com.maeng0830.album.common.model.image.Image;
import com.maeng0830.album.feed.domain.Feed;
import com.maeng0830.album.feed.domain.FeedImage;
import com.maeng0830.album.feed.domain.FeedStatus;
import com.maeng0830.album.feed.dto.FeedAccuseDto;
import com.maeng0830.album.feed.dto.FeedDto;
import com.maeng0830.album.feed.dto.FeedResponse;
import com.maeng0830.album.feed.dto.request.FeedAccuseRequestForm;
import com.maeng0830.album.feed.dto.request.FeedRequestForm;
import com.maeng0830.album.feed.repository.FeedImageRepository;
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
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.domain.PageRequest;
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
	private FeedImageRepository feedImageRepository;
	@Autowired
	private FollowRepository followRepository;

	@Autowired
	private FileDir fileDir;

	@DisplayName("주어진 Feed에 대한 FeedImage를 등록할 수 있다")
	@Test
	void saveFeedImage() throws IOException {
		// given
		List<MultipartFile> imageFiles = createImageFiles("imageFile", "testImage.png",
				"multipart/mixed", fileDir, 3);

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

	@DisplayName("정상 및 신고 상태인 피드 목록을 조회할 수 있다. 로그인 했을 경우, 팔로워 및 팔로이의 피드를 조회한다.")
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

		// Paging 세팅
		PageRequest pageRequest = PageRequest.of(0, 20);

		// when
		List<FeedResponse> feeds = feedService.getFeeds(MemberDto.from(loginMember), pageRequest);

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

		// Paging 세팅
		PageRequest pageRequest = PageRequest.of(0, 20);

		// when
		List<FeedResponse> feeds = feedService.getFeeds(null, pageRequest);

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
		Member member = Member.builder()
				.build();
		memberRepository.save(member);

		Feed feed1 = Feed.builder()
				.member(member)
				.title("feed1")
				.build();
		Feed feed2 = Feed.builder()
				.member(member)
				.title("feed1")
				.build();
		Feed feed3 = Feed.builder()
				.member(member)
				.title("feed1")
				.build();
		feedRepository.saveAll(List.of(feed1, feed2, feed3));

		// when
		FeedResponse feedResponse = feedService.getFeed(feed2.getId());

		// then
		assertThat(feedResponse.getTitle())
				.isEqualTo(feed2.getTitle());
	}

	@DisplayName("로그인 상태인 경우, 피드를 등록할 수 있습니다.")
	@Test
	void feed() throws IOException {
		// given
		FeedRequestForm feedRequestForm = FeedRequestForm.builder()
				.title("testTitle")
				.content("testContent")
				.build();

		Member loginMember = memberRepository.save(Member.builder()
				.username("loginMember")
				.build());
		MemberDto memberDto = MemberDto.from(loginMember);

		List<MultipartFile> imageFiles = createImageFiles("imageFile", "testImage.png",
				"multipart/mixed", fileDir, 3);

		// when
		FeedResponse feedResponse = feedService.feed(feedRequestForm, imageFiles, memberDto);

		// then
		assertThat(feedResponse)
				.extracting("title", "content", "hits", "commentCount", "likeCount")
				.containsExactlyInAnyOrder("testTitle", "testContent", 0, 0, 0);

		assertThat(feedResponse.getFeedImages()).hasSize(3)
				.extracting("imageOriginalName")
				.containsExactlyInAnyOrder(
						imageFiles.get(0).getOriginalFilename(),
						imageFiles.get(1).getOriginalFilename(),
						imageFiles.get(2).getOriginalFilename()
				);
	}

	@DisplayName("비로그인 상태인 경우, 피드 등록을 할 때 예외가 발생합니다.")
	@Test
	void feed_noLogin() throws IOException {
		// given
		FeedRequestForm feedRequestForm = FeedRequestForm.builder()
				.title("testTitle")
				.content("testContent")
				.build();

		MemberDto memberDto = null;

		List<MultipartFile> imageFiles = createImageFiles("imageFile", "testImage.png",
				"multipart/mixed", fileDir, 3);

		// when

		// then
		assertThatThrownBy(() -> feedService.feed(feedRequestForm, imageFiles, memberDto))
				.isInstanceOf(AlbumException.class)
				.hasMessage(REQUIRED_LOGIN.getMessage());
	}

	@DisplayName("작성자인 경우, 피드를 삭제할 수 있습니다(FeedStatus: DELETE).")
	@Test
	void deleteFeed_writer() {
		// given
		Member writer = Member.builder()
				.username("testMember")
				.role(ROLE_MEMBER)
				.build();
		memberRepository.save(writer);
		MemberDto writerDto = MemberDto.from(writer);

		Feed feed = Feed.builder()
				.member(writer)
				.createdBy(writer.getUsername())
				.build();

		feedRepository.save(feed);

		// when
		FeedDto feedDto = feedService.deleteFeed(feed.getId(), writerDto);

		// then
		assertThat(feedDto).extracting("id", "status")
				.containsExactly(feed.getId(), DELETE);
	}

	@DisplayName("관리자인 경우, 피드를 삭제할 수 있습니다.")
	@Test
	void deleteFeed_admin() {
		// given
		Member writer = Member.builder()
				.username("testMember")
				.role(ROLE_MEMBER)
				.build();
		Member admin = Member.builder()
				.username("admin")
				.role(ROLE_ADMIN)
				.build();
		memberRepository.saveAll(List.of(writer, admin));
		MemberDto adminDto = MemberDto.from(admin);

		Feed feed = Feed.builder()
				.member(writer)
				.createdBy(writer.getUsername())
				.build();

		feedRepository.save(feed);

		// when
		FeedDto feedDto = feedService.deleteFeed(feed.getId(), adminDto);

		// then
		assertThat(feedDto).extracting("id", "status")
				.containsExactly(feed.getId(), DELETE);
	}

	@DisplayName("작성자 또는 관리자가 아닌 경우, 피드 삭제 시 예외가 발생합니다.")
	@Test
	void deleteFeed_noWriter_noAdmin() {
		// given
		Member writer = Member.builder()
				.username("testMember")
				.role(ROLE_MEMBER)
				.build();
		Member noWriter = Member.builder()
				.username("noWriter")
				.role(ROLE_MEMBER)
				.build();
		memberRepository.saveAll(List.of(writer, noWriter));
		MemberDto noWriterDto = MemberDto.from(noWriter);

		Feed feed = Feed.builder()
				.member(writer)
				.createdBy(writer.getUsername())
				.build();

		feedRepository.save(feed);

		// when
		AlbumException albumException = assertThrows(AlbumException.class,
				() -> feedService.deleteFeed(feed.getId(), noWriterDto));

		// then
		assertThat(albumException.getCode()).isEqualTo(NO_AUTHORITY);
	}

	@DisplayName("작성자인 경우, 피드를 수정할 수 있다.")
	@Test
	void modifiedFeed() throws IOException {
	    // given
		// member 세팅
		Member writer = Member.builder()
				.username("writer")
				.build();
		memberRepository.save(writer);

		// feed 세팅
		Feed feed = Feed.builder()
				.member(writer)
				.createdBy(writer.getUsername())
				.title("prevTitle")
				.content("prevContent")
				.build();
		feedRepository.save(feed);

		// feedImage 세팅
		List<MultipartFile> imageFiles = createImageFiles("imageFile", "prevTestImage.png",
				"multipart/mixed", fileDir, 3);
		for (MultipartFile imageFile : imageFiles) {
			FeedImage feedImage = FeedImage.builder()
					.image(new Image(imageFile, fileDir))
					.feed(feed)
					.build();

			feedImageRepository.save(feedImage);
		}

		// 변경 데이터 세팅
		FeedRequestForm feedRequestForm = FeedRequestForm.builder()
				.title("modTitle")
				.content("modContent")
				.build();
		List<MultipartFile> modImageFiles = createImageFiles("imageFile", "testImage.png",
				"multipart/mixed", fileDir, 3);
		MemberDto writerDto = MemberDto.from(writer);

		System.out.println("feed.getId() = " + feed.getId());

		// when
		FeedResponse feedResponse = feedService.modifiedFeed(feed.getId(), feedRequestForm, modImageFiles,
				writerDto);

		// then
		assertThat(feedResponse)
				.extracting("title", "content")
				.containsExactly(feedRequestForm.getTitle(), feedRequestForm.getContent());
		assertThat(feedResponse.getFeedImages()).hasSize(3)
				.extracting("imageOriginalName")
				.containsExactlyInAnyOrder(
						modImageFiles.get(0).getOriginalFilename(),
						modImageFiles.get(1).getOriginalFilename(),
						modImageFiles.get(2).getOriginalFilename()
				);
	}

	@DisplayName("작성자가 아닌 경우, 피드 수정 시 예외가 발생합니다.")
	@Test
	void modifiedFeed_noWriter() {
		// given
		// member 세팅
		Member writer = Member.builder()
				.username("writer")
				.build();
		memberRepository.save(writer);

		// feed 세팅
		Feed feed = Feed.builder()
				.member(writer)
				.createdBy(writer.getUsername())
				.title("prevTitle")
				.content("prevContent")
				.build();
		feedRepository.save(feed);

		// 변경 데이터 세팅
		FeedRequestForm feedRequestForm = FeedRequestForm.builder()
				.title("modTitle")
				.content("modContent")
				.build();

		Member noWriter = Member.builder()
				.username("noWriter")
				.build();
		MemberDto noWriterDto = MemberDto.from(noWriter);

		// when

		// then
		assertThatThrownBy(() -> feedService.modifiedFeed(feed.getId(), feedRequestForm, null,
				noWriterDto))
				.isInstanceOf(AlbumException.class)
				.hasMessage(NO_AUTHORITY.getMessage());
	}

	@DisplayName("로그인 상태인 경우, 피드를 신고할 수 있다.")
	@Test
	void accuseFeed() {
	    // given
		// member 세팅
		Member loginMember = Member.builder()
				.username("loginMember")
				.build();
		Member writer = Member.builder()
				.username("writer")
				.build();
		memberRepository.saveAll(List.of(loginMember, writer));
		MemberDto loginMemberDto = MemberDto.from(loginMember);

		// feed 세팅
		Feed feed = Feed.builder()
				.member(writer)
				.title("testTitle")
				.content("testContent")
				.status(NORMAL)
				.build();
		feedRepository.save(feed);

		// feedAccuse 세팅
		FeedAccuseRequestForm feedAccuseRequestForm = FeedAccuseRequestForm.builder()
				.content("testContent")
				.build();

		// when
		FeedAccuseDto result = feedService.accuseFeed(feed.getId(), feedAccuseRequestForm,
				loginMemberDto);

		// then
		assertThat(result)
				.extracting("content", "feedDto.status")
				.containsExactlyInAnyOrder(feedAccuseRequestForm.getContent(), ACCUSE);
	}

	@DisplayName("피드를 특정 상태로 변경할 수 있다.")
	@CsvSource({"NORMAL", "ACCUSE", "DELETE"})
	@ParameterizedTest
	void changeFeedStatus(FeedStatus feedStatus) {
		// given
		// member 세팅
		Member writer = Member.builder()
				.username("writer")
				.build();
		memberRepository.save(writer);

		// feed 세팅
		Feed feed = Feed.builder()
				.member(writer)
				.title("testTitle")
				.content("testContent")
				.build();
		feedRepository.save(feed);

		// when
		FeedDto feedDto = feedService.changeFeedStatus(feed.getId(), feedStatus);

		// then
		assertThat(feedDto)
				.extracting("id", "status")
				.containsExactlyInAnyOrder(feed.getId(), feedStatus);
	}


	private MockMultipartFile createImageFile(String name, String originalFilename,
											  String contentType, FileDir fileDir)
			throws IOException {
		String filePath = fileDir.getDir() + originalFilename;
		FileInputStream fileInputStream = new FileInputStream(new File(filePath));

		return new MockMultipartFile(name,
				originalFilename, contentType, fileInputStream);
	}

	private List<MultipartFile> createImageFiles(String name, String originalFilename,
												 String contentType, FileDir fileDir, int count)
			throws IOException {
		List<MultipartFile> imageFiles = new ArrayList<>();

		for (int i = 0; i < count; i++) {
			imageFiles.add(createImageFile(name, originalFilename,
					contentType, fileDir));
		}

		return imageFiles;
	}
}
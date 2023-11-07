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
import static org.mockito.BDDMockito.given;

import com.maeng0830.album.common.exception.AlbumException;
import com.maeng0830.album.common.filedir.FileDir;
import com.maeng0830.album.common.model.image.Image;
import com.maeng0830.album.feed.domain.Feed;
import com.maeng0830.album.feed.domain.FeedAccuse;
import com.maeng0830.album.feed.domain.FeedImage;
import com.maeng0830.album.feed.domain.FeedStatus;
import com.maeng0830.album.feed.dto.FeedAccuseDto;
import com.maeng0830.album.feed.dto.FeedDto;
import com.maeng0830.album.feed.dto.FeedResponse;
import com.maeng0830.album.feed.dto.request.FeedAccuseRequestForm;
import com.maeng0830.album.feed.dto.request.FeedChangeStatusForm;
import com.maeng0830.album.feed.dto.request.FeedModifiedForm;
import com.maeng0830.album.feed.dto.request.FeedPostForm;
import com.maeng0830.album.feed.repository.FeedAccuseRepository;
import com.maeng0830.album.feed.repository.FeedImageRepository;
import com.maeng0830.album.feed.repository.FeedRepository;
import com.maeng0830.album.follow.domain.Follow;
import com.maeng0830.album.follow.repository.FollowRepository;
import com.maeng0830.album.member.domain.Member;
import com.maeng0830.album.member.dto.MemberDto;
import com.maeng0830.album.member.repository.MemberRepository;
import com.maeng0830.album.support.ServiceTestSupport;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.web.multipart.MultipartFile;

class FeedServiceTest extends ServiceTestSupport {

	@Autowired
	private FeedService feedService;
	@Autowired
	private MemberRepository memberRepository;
	@Autowired
	private FeedRepository feedRepository;
	@Autowired
	private FeedImageRepository feedImageRepository;
	@Autowired
	private FeedAccuseRepository feedAccuseRepository;
	@Autowired
	private FollowRepository followRepository;
	@Autowired
	private FileDir fileDir;

	@DisplayName("정상 및 신고 상태인 피드 목록을 조회할 수 있다. 로그인 했을 경우, 팔로워 및 팔로이의 피드를 조회한다."
			+ "조회수 내림차순으로 정렬된다.")
	@Test
	void getFeedsForMain() {
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
				.following(loginMember)
				.build();
		Follow follow2 = Follow.builder()
				.follower(loginMember)
				.following(followeeMember)
				.build();
		followRepository.saveAll(List.of(follow1, follow2));

		// 피드 세팅
		Feed followerFeed = Feed.builder()
				.status(NORMAL)
				.hits(1)
				.member(followerMember)
				.build();
		Feed followeeFeed = Feed.builder()
				.status(ACCUSE)
				.hits(2)
				.member(followeeMember)
				.build();
		Feed noRelationFeed = Feed.builder()
				.status(NORMAL)
				.hits(3)
				.member(noRelationMember)
				.build();
		feedRepository.saveAll(List.of(followerFeed, followeeFeed, noRelationFeed));

		// Paging 세팅
		PageRequest pageRequest = PageRequest.of(0, 20);

		// when
		Page<FeedResponse> feeds = feedService.getFeedsForMain(MemberDto.from(loginMember),
				pageRequest);

		// then
		assertThat(feeds.getContent()).hasSize(2)
				.extracting("member.username", "hits")
				.containsExactlyInAnyOrder(
						tuple(followeeMember.getUsername(), 2),
						tuple(followerMember.getUsername(), 1)
				);
	}

	@DisplayName("정상 및 신고 상태인 피드 목록을 조회할 수 있다. 로그인 안했을 경우, 모든 피드를 조회한다."
			+ "조회수 내림차순으로 정렬된다.")
	@Test
	void getFeedsForMain_noLogin() {
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
				.following(loginMember)
				.build();
		Follow follow2 = Follow.builder()
				.follower(loginMember)
				.following(followeeMember)
				.build();
		followRepository.saveAll(List.of(follow1, follow2));

		// 피드 세팅
		Feed followerFeed = Feed.builder()
				.status(NORMAL)
				.hits(1)
				.member(followerMember)
				.build();
		Feed followeeFeed = Feed.builder()
				.status(ACCUSE)
				.hits(2)
				.member(followeeMember)
				.build();
		Feed noRelationFeed = Feed.builder()
				.status(NORMAL)
				.hits(3)
				.member(noRelationMember)
				.build();
		feedRepository.saveAll(List.of(followerFeed, followeeFeed, noRelationFeed));

		// Paging 세팅
		PageRequest pageRequest = PageRequest.of(0, 20);

		// when
		Page<FeedResponse> feeds = feedService.getFeedsForMain(null, pageRequest);

		// then
		assertThat(feeds.getContent()).hasSize(3)
				.extracting("member.username", "hits")
				.containsExactlyInAnyOrder(
						tuple(noRelationMember.getUsername(), 3),
						tuple(followeeMember.getUsername(), 2),
						tuple(followerMember.getUsername(), 1)
				);
	}

	@DisplayName("searchText와 작성자 닉네임이 전방 일치하는 정상 및 신고 상태의 피드를 조회한다."
			+ "searchText가 null인 경우, 정상 및 신고 상태의 모든 피드를 조회한다."
			+ "조회수 내림차순으로 정렬된다.")
	@CsvSource(value = {",", "nickname1", "nickname2"})
	@ParameterizedTest
	void getFeedsForMainWithSearchText(String searchText) {
		// given
		// 멤버 세팅
		Member member1 = Member.builder()
				.username("username11")
				.nickname("nickname11")
				.build();
		Member member2 = Member.builder()
				.username("username22")
				.nickname("nickname22")
				.build();
		Member member3 = Member.builder()
				.username("username33")
				.nickname("nickname33")
				.build();
		memberRepository.saveAll(List.of(member1, member2, member3));

		// 피드 세팅
		List<Feed> feeds = new ArrayList<>();

		for (int i = 0; i < 15; i++) {
			if (i <= 4) {
				Feed feed = Feed.builder()
						.member(member1)
						.hits(i)
						.status(NORMAL)
						.build();
				feeds.add(feed);
			} else if (i <= 9) {
				Feed feed = Feed.builder()
						.member(member2)
						.hits(i)
						.status(ACCUSE)
						.build();
				feeds.add(feed);
			} else {
				Feed feed = Feed.builder()
						.member(member3)
						.hits(i)
						.status(DELETE)
						.build();
				feeds.add(feed);
			}
		}

		feedRepository.saveAll(feeds);

		// pageRequest 세팅
		PageRequest pageRequest = PageRequest.of(0, 20);

		// when
		Page<FeedResponse> result = feedService.getFeedsForMainWithSearchText(
				searchText, pageRequest);

		// then
		if (searchText == null) {
			assertThat(result.getContent()).hasSize(10)
					.extracting("member.nickname", "hits", "status")
					.containsExactly(
							tuple(member2.getNickname(), 9, ACCUSE),
							tuple(member2.getNickname(), 8, ACCUSE),
							tuple(member2.getNickname(), 7, ACCUSE),
							tuple(member2.getNickname(), 6, ACCUSE),
							tuple(member2.getNickname(), 5, ACCUSE),
							tuple(member1.getNickname(), 4, NORMAL),
							tuple(member1.getNickname(), 3, NORMAL),
							tuple(member1.getNickname(), 2, NORMAL),
							tuple(member1.getNickname(), 1, NORMAL),
							tuple(member1.getNickname(), 0, NORMAL)
					);
		} else if (searchText.equals("nickname1")) {
			assertThat(result.getContent()).hasSize(5)
					.extracting("member.nickname", "hits", "status")
					.containsExactly(
							tuple(member1.getNickname(), 4, NORMAL),
							tuple(member1.getNickname(), 3, NORMAL),
							tuple(member1.getNickname(), 2, NORMAL),
							tuple(member1.getNickname(), 1, NORMAL),
							tuple(member1.getNickname(), 0, NORMAL)
					);
		} else if (searchText.equals("nickname2")) {
			assertThat(result.getContent()).hasSize(5)
					.extracting("member.nickname", "hits", "status")
					.containsExactly(
							tuple(member2.getNickname(), 9, ACCUSE),
							tuple(member2.getNickname(), 8, ACCUSE),
							tuple(member2.getNickname(), 7, ACCUSE),
							tuple(member2.getNickname(), 6, ACCUSE),
							tuple(member2.getNickname(), 5, ACCUSE)
					);
		}
	}

	@DisplayName("관리자인 경우, searchText와 작성자 username 또는 nickname이 전방 일치하는 피드를 조회할 수 있다."
			+ "searchText가 null인 경우, 모든 피드를 조회한다."
			+ "피드 상태 기준, 신고-정상-신고 순으로 정렬된다.")
	@CsvSource(value = {",", "username1", "nickname1", "username2", "nickname2", "username3",
			"nickname3"})
	@ParameterizedTest
	void getFeedsForAdmin(String searchText) {
		// given
		// 로그인 멤버 세팅
		Member admin = Member.builder()
				.role(ROLE_ADMIN)
				.build();

		// 피드 작성자 세팅
		Member member1 = Member.builder()
				.username("username11")
				.nickname("nickname11")
				.build();
		Member member2 = Member.builder()
				.username("username22")
				.nickname("nickname22")
				.build();
		Member member3 = Member.builder()
				.username("username33")
				.nickname("nickname33")
				.build();
		memberRepository.saveAll(List.of(admin, member1, member2, member3));

		// 피드 세팅
		List<Feed> feeds = new ArrayList<>();

		for (int i = 0; i < 15; i++) {
			if (i <= 4) {
				Feed feed = Feed.builder()
						.member(member1)
						.status(NORMAL)
						.build();
				feeds.add(feed);
			} else if (i <= 9) {
				Feed feed = Feed.builder()
						.member(member2)
						.status(ACCUSE)
						.build();
				feeds.add(feed);
			} else {
				Feed feed = Feed.builder()
						.member(member3)
						.status(DELETE)
						.build();
				feeds.add(feed);
			}
		}

		feedRepository.saveAll(feeds);

		// pageRequest 세팅
		PageRequest pageRequest = PageRequest.of(0, 20);

		// when
		Page<FeedResponse> result = feedService.getFeedsForAdmin(MemberDto.from(admin),
				searchText, pageRequest);

		// then
		if (searchText == null) {
			assertThat(result.getContent()).hasSize(15)
					.extracting("member.username", "member.nickname", "status")
					.containsExactly(
							tuple(member2.getUsername(), member2.getNickname(), ACCUSE),
							tuple(member2.getUsername(), member2.getNickname(), ACCUSE),
							tuple(member2.getUsername(), member2.getNickname(), ACCUSE),
							tuple(member2.getUsername(), member2.getNickname(), ACCUSE),
							tuple(member2.getUsername(), member2.getNickname(), ACCUSE),
							tuple(member1.getUsername(), member1.getNickname(), NORMAL),
							tuple(member1.getUsername(), member1.getNickname(), NORMAL),
							tuple(member1.getUsername(), member1.getNickname(), NORMAL),
							tuple(member1.getUsername(), member1.getNickname(), NORMAL),
							tuple(member1.getUsername(), member1.getNickname(), NORMAL),
							tuple(member3.getUsername(), member3.getNickname(), DELETE),
							tuple(member3.getUsername(), member3.getNickname(), DELETE),
							tuple(member3.getUsername(), member3.getNickname(), DELETE),
							tuple(member3.getUsername(), member3.getNickname(), DELETE),
							tuple(member3.getUsername(), member3.getNickname(), DELETE)
					);
		} else if (searchText.equals("username1") || searchText.equals("nickname1")) {
			assertThat(result.getContent()).hasSize(5)
					.extracting("member.username", "member.nickname", "status")
					.containsExactly(
							tuple(member1.getUsername(), member1.getNickname(), NORMAL),
							tuple(member1.getUsername(), member1.getNickname(), NORMAL),
							tuple(member1.getUsername(), member1.getNickname(), NORMAL),
							tuple(member1.getUsername(), member1.getNickname(), NORMAL),
							tuple(member1.getUsername(), member1.getNickname(), NORMAL)
					);
		} else if (searchText.equals("username2") || searchText.equals("nickname2")) {
			assertThat(result.getContent()).hasSize(5)
					.extracting("member.username", "member.nickname", "status")
					.containsExactly(
							tuple(member2.getUsername(), member2.getNickname(), ACCUSE),
							tuple(member2.getUsername(), member2.getNickname(), ACCUSE),
							tuple(member2.getUsername(), member2.getNickname(), ACCUSE),
							tuple(member2.getUsername(), member2.getNickname(), ACCUSE),
							tuple(member2.getUsername(), member2.getNickname(), ACCUSE)
					);
		} else if (searchText.equals("username3") || searchText.equals("nickname3")) {
			assertThat(result.getContent()).hasSize(5)
					.extracting("member.username", "member.nickname", "status")
					.containsExactly(
							tuple(member3.getUsername(), member3.getNickname(), DELETE),
							tuple(member3.getUsername(), member3.getNickname(), DELETE),
							tuple(member3.getUsername(), member3.getNickname(), DELETE),
							tuple(member3.getUsername(), member3.getNickname(), DELETE),
							tuple(member3.getUsername(), member3.getNickname(), DELETE)
					);
		}
	}

	@DisplayName("관리자가 아닌 경우, "
			+ "searchText와 작성자 username 또는 nickname이 전방 일치하는 피드를 조회하고자 시도할 때"
			+ "예외가 발생한다.")
	@Test
	void getFeedsForAdmin_noAdmin() {
		// given
		Member noAdmin = Member.builder()
				.role(ROLE_MEMBER)
				.build();
		MemberDto noAdminDto = MemberDto.from(noAdmin);

		PageRequest pageRequest = PageRequest.of(0, 20);

		// when
		assertThatThrownBy(() -> feedService.getFeedsForAdmin(noAdminDto, null, pageRequest))
				.isInstanceOf(AlbumException.class)
				.hasMessage(NO_AUTHORITY.getMessage());

		// then
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
				.status(NORMAL)
				.build();
		Feed feed2 = Feed.builder()
				.member(member)
				.title("feed1")
				.status(NORMAL)
				.build();
		Feed feed3 = Feed.builder()
				.member(member)
				.title("feed1")
				.status(NORMAL)
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
		FeedPostForm feedPostForm = FeedPostForm.builder()
				.title("testTitle")
				.content("testContent")
				.build();

		Member loginMember = memberRepository.save(Member.builder()
				.username("loginMember")
				.build());
		MemberDto memberDto = MemberDto.from(loginMember);

		List<MultipartFile> imageFiles = createImageFiles("imageFile", "testImage.PNG",
				"multipart/mixed", fileDir, 3);

		// stub
		List<Image> images = createImage(imageFiles);

		given(awsS3Manager.uploadImage(imageFiles))
				.willReturn(
						images
				);

		// when
		FeedResponse feedResponse = feedService.feed(feedPostForm, imageFiles, memberDto);

		// then
		assertThat(feedResponse)
				.extracting("title", "content", "hits", "commentCount")
				.containsExactlyInAnyOrder("testTitle", "testContent", 0, 0);
		assertThat(feedResponse.getFeedImages()).hasSize(3)
				.extracting("imageOriginalName", "imageStoreName", "imagePath")
				.containsExactlyInAnyOrder(
						tuple(images.get(0).getImageOriginalName(),
								images.get(0).getImageStoreName(), images.get(0).getImagePath()),
						tuple(images.get(1).getImageOriginalName(),
								images.get(1).getImageStoreName(), images.get(1).getImagePath()),
						tuple(images.get(2).getImageOriginalName(),
								images.get(2).getImageStoreName(), images.get(2).getImagePath())
				);
	}

	@DisplayName("비로그인 상태인 경우, 피드 등록을 할 때 예외가 발생합니다.")
	@Test
	void feed_noLogin() throws IOException {
		// given
		FeedPostForm feedPostForm = FeedPostForm.builder()
				.title("testTitle")
				.content("testContent")
				.build();

		MemberDto memberDto = null;

		List<MultipartFile> imageFiles = createImageFiles("imageFile", "testImage.PNG",
				"multipart/mixed", fileDir, 3);

		// when

		// then
		assertThatThrownBy(() -> feedService.feed(feedPostForm, imageFiles, memberDto))
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
				.title("prevTitle")
				.content("prevContent")
				.build();
		feedRepository.save(feed);

		// feedImage 세팅
		List<MultipartFile> imageFiles = createImageFiles("imageFile", "prevTestImage.PNG",
				"multipart/mixed", fileDir, 3);

		List<Image> images = createImage(imageFiles);

		for (Image image : images) {
			FeedImage feedImage = FeedImage.builder()
					.image(image)
					.feed(feed)
					.build();

			feedImageRepository.save(feedImage);
		}

		// 변경 데이터 세팅
		FeedModifiedForm feedModifiedForm = FeedModifiedForm.builder()
				.id(feed.getId())
				.title("modTitle")
				.content("modContent")
				.build();

		List<MultipartFile> modImageFiles = createImageFiles("imageFile", "testImage.PNG",
				"multipart/mixed", fileDir, 3);

		MemberDto writerDto = MemberDto.from(writer);

		// stub
		List<Image> modImages = createImage(modImageFiles);

		given(awsS3Manager.uploadImage(modImageFiles))
				.willReturn(
						modImages
				);

		// when
		FeedResponse feedResponse = feedService.modifiedFeed(feedModifiedForm, modImageFiles,
				writerDto);

		// then
		assertThat(feedResponse)
				.extracting("title", "content")
				.containsExactly(feedModifiedForm.getTitle(), feedModifiedForm.getContent());
		assertThat(feedResponse.getFeedImages()).hasSize(3)
				.extracting("imageOriginalName", "imageStoreName", "imagePath")
				.containsExactlyInAnyOrder(
						tuple(modImages.get(0).getImageOriginalName(),
								modImages.get(0).getImageStoreName(),
								modImages.get(0).getImagePath()),
						tuple(modImages.get(1).getImageOriginalName(),
								modImages.get(1).getImageStoreName(),
								modImages.get(1).getImagePath()),
						tuple(modImages.get(2).getImageOriginalName(),
								modImages.get(2).getImageStoreName(),
								modImages.get(2).getImagePath())
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
				.title("prevTitle")
				.content("prevContent")
				.build();
		feedRepository.save(feed);

		// 변경 데이터 세팅
		FeedModifiedForm feedModifiedForm = FeedModifiedForm.builder()
				.id(feed.getId())
				.title("modTitle")
				.content("modContent")
				.build();

		Member noWriter = Member.builder()
				.username("noWriter")
				.build();
		MemberDto noWriterDto = MemberDto.from(noWriter);

		// when

		// then
		assertThatThrownBy(() -> feedService.modifiedFeed(feedModifiedForm, null, noWriterDto))
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
				.id(feed.getId())
				.content("testContent")
				.build();

		// when
		FeedResponse result = feedService.accuseFeed(feedAccuseRequestForm,
				loginMemberDto);

		// then
		assertThat(result)
				.extracting("id", "status")
				.containsExactlyInAnyOrder(feed.getId(), ACCUSE);
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

		// FeedChangeStatusForm 세팅
		FeedChangeStatusForm feedChangeStatusForm = FeedChangeStatusForm.builder()
				.feedStatus(feedStatus)
				.id(feed.getId())
				.build();

		// when
		FeedDto feedDto = feedService.changeFeedStatus(feedChangeStatusForm);

		// then
		assertThat(feedDto)
				.extracting("id", "status")
				.containsExactlyInAnyOrder(feed.getId(), feedStatus);
	}


	@DisplayName("관리자인 경우, 특정 피드의 신고 내역을 조회할 수 있다.")
	@Test
	void getFeedAccuses() {
		// given
		// 관리자 세팅
		Member admin = Member.builder()
				.role(ROLE_ADMIN)
				.build();

		// 작성자 및 신고자 세팅
		Member writer = Member.builder()
				.build();
		Member accuser = Member.builder()
				.build();

		memberRepository.saveAll(List.of(admin, writer, accuser));
		MemberDto adminDto = MemberDto.from(admin);

		// 피드 세팅
		Feed feed1 = Feed.builder()
				.member(writer)
				.status(ACCUSE)
				.build();
		Feed feed2 = Feed.builder()
				.member(writer)
				.status(ACCUSE)
				.build();
		feedRepository.saveAll(List.of(feed1, feed2));

		// 피드 신고 세팅
		List<FeedAccuse> feedAccuses = new ArrayList<>();

		for (int i = 0; i < 10; i++) {
			if (i <= 4) {
				FeedAccuse feedAccuse = FeedAccuse.builder()
						.member(accuser)
						.feed(feed1)
						.build();
				feedAccuses.add(feedAccuse);
			} else {
				FeedAccuse feedAccuse = FeedAccuse.builder()
						.member(accuser)
						.feed(feed2)
						.build();
				feedAccuses.add(feedAccuse);
			}
		}

		feedAccuseRepository.saveAll(feedAccuses);

		// when
		List<FeedAccuseDto> result1 = feedService.getFeedAccuses(adminDto, feed1.getId());
		List<FeedAccuseDto> result2 = feedService.getFeedAccuses(adminDto, feed2.getId());

		// then
		assertThat(result1).hasSize(5)
				.extracting("feedDto.id")
				.containsExactlyInAnyOrder(
						feed1.getId(), feed1.getId(), feed1.getId(), feed1.getId(), feed1.getId()
				);
		assertThat(result2).hasSize(5)
				.extracting("feedDto.id")
				.containsExactlyInAnyOrder(
						feed2.getId(), feed2.getId(), feed2.getId(), feed2.getId(), feed2.getId()
				);
	}

	@DisplayName("관리자가 아닌 경우, 특정 피드의 신고내역을 조회할 때 예외가 발생한다.")
	@Test
	void getFeedAccuses_noAdmin() {
		// given
		Member noAdmin = Member.builder()
				.role(ROLE_MEMBER)
				.build();
		MemberDto noAdminDto = MemberDto.from(noAdmin);

		// when
		assertThatThrownBy(() -> feedService.getFeedAccuses(noAdminDto, 1L))
				.isInstanceOf(AlbumException.class)
				.hasMessage(NO_AUTHORITY.getMessage());

		// then
	}

	@DisplayName("로그인한 경우, 본인이 작성한 정상 및 신고 상태의 피드 목록을 조회할 수 있다.")
	@Test
	void getMyFeeds() {
		// given
		// 작성자 세팅
		Member member1 = Member.builder()
				.build();
		Member member2 = Member.builder()
				.build();
		memberRepository.saveAll(List.of(member1, member2));

		// 피드 세팅
		Feed member1Normal = Feed.builder()
				.member(member1)
				.status(NORMAL)
				.build();
		Feed member2Normal = Feed.builder()
				.member(member2)
				.status(NORMAL)
				.build();
		Feed member1Accuse = Feed.builder()
				.member(member1)
				.status(ACCUSE)
				.build();
		Feed member2Accuse = Feed.builder()
				.member(member2)
				.status(ACCUSE)
				.build();
		Feed member1Delete = Feed.builder()
				.member(member1)
				.status(DELETE)
				.build();
		Feed member2Delete = Feed.builder()
				.member(member2)
				.status(DELETE)
				.build();
		feedRepository.saveAll(
				List.of(member1Normal, member2Normal, member1Accuse, member2Accuse, member1Delete,
						member2Delete));

		PageRequest pageRequest = PageRequest.of(0, 20);

		// when
		Page<FeedResponse> result1 = feedService.getMyFeeds(member1.getId(),
				MemberDto.from(member1), pageRequest);
		Page<FeedResponse> result2 = feedService.getMyFeeds(member2.getId(),
				MemberDto.from(member2), pageRequest);

		// then
		assertThat(result1.getContent()).hasSize(2)
				.extracting("status", "member.id")
				.containsExactlyInAnyOrder(
						tuple(NORMAL, member1.getId()),
						tuple(ACCUSE, member1.getId())
				);
		assertThat(result2.getContent()).hasSize(2)
				.extracting("status", "member.id")
				.containsExactlyInAnyOrder(
						tuple(NORMAL, member2.getId()),
						tuple(ACCUSE, member2.getId())
				);
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

	private List<Image> createImage(List<MultipartFile> imageFiles) {
		List<Image> images = new ArrayList<>();

		for (MultipartFile imageFile : imageFiles) {
			String storeName = UUID.randomUUID() + "." + imageFile.getOriginalFilename();

			Image image = Image.builder()
					.imageOriginalName(imageFile.getOriginalFilename())
					.imageStoreName(storeName)
					.imagePath(fileDir.getDir() + storeName)
					.build();

			images.add(image);
		}

		return images;
	}
}
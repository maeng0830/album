package com.maeng0830.album.feed.service;

import static com.maeng0830.album.feed.domain.FeedStatus.ACCUSE;
import static com.maeng0830.album.feed.domain.FeedStatus.DELETE;
import static com.maeng0830.album.feed.domain.FeedStatus.NORMAL;
import static com.maeng0830.album.feed.exception.FeedExceptionCode.NOT_EXIST_FEED;
import static com.maeng0830.album.member.exception.MemberExceptionCode.NOT_EXIST_MEMBER;
import static com.maeng0830.album.member.exception.MemberExceptionCode.NO_AUTHORITY;
import static com.maeng0830.album.member.exception.MemberExceptionCode.REQUIRED_LOGIN;

import com.maeng0830.album.common.exception.AlbumException;
import com.maeng0830.album.common.filedir.FileDir;
import com.maeng0830.album.common.model.image.Image;
import com.maeng0830.album.common.util.AlbumUtil;
import com.maeng0830.album.feed.domain.Feed;
import com.maeng0830.album.feed.domain.FeedAccuse;
import com.maeng0830.album.feed.domain.FeedImage;
import com.maeng0830.album.feed.domain.FeedStatus;
import com.maeng0830.album.feed.dto.FeedAccuseDto;
import com.maeng0830.album.feed.dto.FeedDto;
import com.maeng0830.album.feed.dto.FeedResponse;
import com.maeng0830.album.feed.dto.request.FeedAccuseRequestForm;
import com.maeng0830.album.feed.dto.request.FeedRequestForm;
import com.maeng0830.album.feed.repository.FeedAccuseRepository;
import com.maeng0830.album.feed.repository.FeedImageRepository;
import com.maeng0830.album.feed.repository.FeedRepository;
import com.maeng0830.album.follow.domain.Follow;
import com.maeng0830.album.follow.repository.FollowRepository;
import com.maeng0830.album.member.domain.Member;
import com.maeng0830.album.member.domain.MemberRole;
import com.maeng0830.album.member.dto.MemberDto;
import com.maeng0830.album.member.repository.MemberRepository;
import java.io.File;
import java.io.IOException;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.domain.Sort.Direction;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

@Service
@RequiredArgsConstructor
public class FeedService {

	private final FeedRepository feedRepository;
	private final FeedImageRepository feedImageRepository;
	private final FeedAccuseRepository feedAccuseRepository;
	private final MemberRepository memberRepository;
	private final FollowRepository followRepository;
	private final FileDir fileDir;
	private final AlbumUtil albumUtil;

	// FeedImage 데이터 등록
	public List<FeedImage> saveFeedImage(List<MultipartFile> imageFiles, Feed findFeed) {
		for (MultipartFile imageFile : imageFiles) {
			FeedImage feedImage = FeedImage.builder()
					.image(new Image(imageFile, fileDir))
					.feed(findFeed)
					.build();

			try {
				imageFile.transferTo(new File(feedImage.getImage().getImagePath()));
			} catch (IOException e) {
				throw new RuntimeException(e);
			}

			feedImageRepository.save(feedImage);
		}

		return feedImageRepository.findByFeed_Id(findFeed.getId());
	}

	// 전체 피드 목록 조회, 로그인 여부에 따라 다른 피드 목록 반환
	public List<FeedResponse> getFeeds(MemberDto memberDto, Pageable pageable) {

		PageRequest pageRequest = PageRequest.of(pageable.getPageNumber(), pageable.getPageSize(),
				Sort.by(Direction.DESC, "hits"));

		List<FeedStatus> feedStatuses = List.of(NORMAL, ACCUSE);

		//로그인 상태
		if (memberDto != null) {
			Member loginMember = memberRepository.findById(memberDto.getId())
					.orElseThrow(() -> new AlbumException(
							NOT_EXIST_MEMBER));

			// 로그인 회원이 팔로우 하는 회원, 로그인 회원을 팔로우하는 회원 조회
			List<Follow> members = followRepository.searchByFollowerOrFollowee(loginMember,
					loginMember);

			// 로그인 회원과 팔로우 관계를 가진 회원의 username 목록
			Set<String> createdBy = new HashSet<>();

			members.stream()
					.filter(f -> !f.getFollowee().getUsername().equals(loginMember.getUsername()))
					.map(f -> f.getFollowee().getUsername()).forEach(createdBy::add);
			members.stream()
					.filter(f -> !f.getFollower().getUsername().equals(loginMember.getUsername()))
					.map(f -> f.getFollower().getUsername()).forEach(createdBy::add);

			Page<Feed> feeds = feedRepository.searchByStatusAndCreatedBy(feedStatuses, createdBy,
					pageRequest);

			return feeds.stream().map(f -> new FeedResponse(f, f.getFeedImages()))
					.collect(Collectors.toList());
		} else {
			//비로그인 상태
			Page<Feed> feeds = feedRepository.searchByStatusAndCreatedBy(feedStatuses, null,
					pageRequest);

			System.out.println("메소드 완료!");

			return feeds.stream().map(f -> new FeedResponse(f, f.getFeedImages()))
					.collect(Collectors.toList());
		}
	}

	// 특정 피드 조회
	public FeedResponse getFeed(Long feedId) {
		Feed findFeed = feedRepository.findById(feedId)
				.orElseThrow(() -> new AlbumException(NOT_EXIST_FEED));

		List<FeedImage> feedImages = feedImageRepository.findByFeed_Id(findFeed.getId());

		return new FeedResponse(findFeed, feedImages);
	}

	// 피드 등록
	public FeedResponse feed(FeedRequestForm feedRequestForm, List<MultipartFile> imageFiles,
							 MemberDto memberDto) {

		if (memberDto == null) {
			throw new AlbumException(REQUIRED_LOGIN);
		}

		Member loginMember = memberRepository.findById(memberDto.getId())
				.orElseThrow(() -> new AlbumException(NOT_EXIST_MEMBER));

		// Feed 데이터 등록
		Feed feed = Feed.builder()
				.title(feedRequestForm.getTitle())
				.content(feedRequestForm.getContent())
				.hits(0)
				.commentCount(0)
				.status(NORMAL)
				.member(loginMember)
				.build();

		feedRepository.save(feed);

		// FeedImage 데이터 등록
		List<FeedImage> feedImages = saveFeedImage(imageFiles, feed);

		return new FeedResponse(feed, feedImages);
	}

	// 피드 삭제
	@Transactional
	public FeedDto deleteFeed(Long feedId, MemberDto memberDto) {

		// 로그인 여부 확인
		if (memberDto == null) {
			throw new AlbumException(REQUIRED_LOGIN);
		}

		// 목표 피드 데이터 조회
		Feed findFeed = feedRepository.findById(feedId)
				.orElseThrow(() -> new AlbumException(NOT_EXIST_FEED));

		// 본인 또는 관리자 여부 확인
		if (!findFeed.getMember().getUsername().equals(memberDto.getUsername())
				&& !memberDto.getRole().equals(
				MemberRole.ROLE_ADMIN)) {
			throw new AlbumException(NO_AUTHORITY);
		}

		// Feed 상태 변경
		findFeed.changeStatus(DELETE);

		return FeedDto.from(findFeed);
	}

	// 피드 수정
	@Transactional
	public FeedResponse modifiedFeed(Long feedId, FeedRequestForm feedRequestForm, List<MultipartFile> imageFiles,
									 MemberDto memberDto) {

		// 로그인 여부 확인
		if (memberDto == null) {
			throw new AlbumException(REQUIRED_LOGIN);
		}

		// 목표 피드 데이터 조회
		Feed findFeed = feedRepository.findById(feedId)
				.orElseThrow(() -> new AlbumException(NOT_EXIST_FEED));

		// 본인 여부 확인
		if (!findFeed.getMember().getUsername().equals(memberDto.getUsername())) {
			throw new AlbumException(NO_AUTHORITY);
		}

		// Feed 데이터 수정
		findFeed.modified(feedRequestForm);

		// 이전 FeedImage 데이터 삭제
		feedImageRepository.deleteFeedImageByFeed_Id(findFeed.getId());

		// 새로운 FeedImage 데이터 등록
		List<FeedImage> feedImages = saveFeedImage(imageFiles, findFeed);

		return new FeedResponse(findFeed, feedImages);
	}

	// 피드 신고
	@Transactional
	public FeedAccuseDto accuseFeed(Long feedId, FeedAccuseRequestForm feedAccuseRequestForm, MemberDto memberDto) {

		// 로그인 여부 확인
		if (memberDto == null) {
			throw new AlbumException(REQUIRED_LOGIN);
		}

		// 신고 피드 조회 및 상태 변경
		Feed findFeed = feedRepository.findById(feedId)
				.orElseThrow(() -> new AlbumException(NOT_EXIST_FEED));

		findFeed.changeStatus(ACCUSE);

		// 신고자 조회
		Member findMember = memberRepository.findByUsername(memberDto.getUsername())
				.orElseThrow(() -> new AlbumException(NOT_EXIST_MEMBER));

		// 신고 내역 저장
		FeedAccuse savedFeedAccuse = feedAccuseRepository.save(
				FeedAccuse.builder()
						.content(feedAccuseRequestForm.getContent())
						.member(findMember)
						.feed(findFeed)
						.build()
		);

		return FeedAccuseDto.from(savedFeedAccuse);
	}

	// 피드 상태 변경
	@Transactional
	public FeedDto changeFeedStatus(Long feedId, FeedStatus feedStatus) {
		Feed findFeed = feedRepository.findById(feedId)
				.orElseThrow(() -> new AlbumException(NOT_EXIST_FEED));

		findFeed.changeStatus(feedStatus);

		return FeedDto.from(findFeed);
	}
}

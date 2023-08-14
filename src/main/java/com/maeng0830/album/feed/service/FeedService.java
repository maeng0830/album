package com.maeng0830.album.feed.service;

import static com.maeng0830.album.feed.domain.FeedStatus.ACCUSE;
import static com.maeng0830.album.feed.domain.FeedStatus.DELETE;
import static com.maeng0830.album.feed.domain.FeedStatus.NORMAL;
import static com.maeng0830.album.feed.exception.FeedExceptionCode.NOT_EXIST_FEED;
import static com.maeng0830.album.member.domain.MemberRole.ROLE_ADMIN;
import static com.maeng0830.album.member.exception.MemberExceptionCode.NOT_EXIST_MEMBER;
import static com.maeng0830.album.member.exception.MemberExceptionCode.NO_AUTHORITY;
import static com.maeng0830.album.member.exception.MemberExceptionCode.REQUIRED_LOGIN;

import com.maeng0830.album.common.aws.AwsS3Manager;
import com.maeng0830.album.common.exception.AlbumException;
import com.maeng0830.album.common.filedir.FileDir;
import com.maeng0830.album.common.image.DefaultImage;
import com.maeng0830.album.common.model.image.Image;
import com.maeng0830.album.feed.domain.Feed;
import com.maeng0830.album.feed.domain.FeedAccuse;
import com.maeng0830.album.feed.domain.FeedImage;
import com.maeng0830.album.feed.domain.FeedStatus;
import com.maeng0830.album.feed.dto.FeedAccuseDto;
import com.maeng0830.album.feed.dto.FeedDto;
import com.maeng0830.album.feed.dto.FeedResponse;
import com.maeng0830.album.feed.dto.request.FeedAccuseRequestForm;
import com.maeng0830.album.feed.dto.request.FeedModifiedForm;
import com.maeng0830.album.feed.dto.request.FeedPostForm;
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
import org.springframework.data.domain.Sort.Order;
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
	private final AwsS3Manager awsS3Manager;
	private final FileDir fileDir;
	private final DefaultImage defaultImage;

	// FeedImage 데이터 등록
	private List<FeedImage> saveFeedImage(List<Image> images, Feed findFeed) {
		// 첨부 파일이 없을 경우, 기본 이미지 파일 사용
		// 첨부 파일이 있을 경우, 파일 저장.
		if (images.isEmpty()) {
			FeedImage feedImage = FeedImage.builder()
					.image(Image.createDefaultImage(fileDir, defaultImage.getFeedImage()))
					.build();

			feedImageRepository.save(feedImage);
		} else {
			for (Image image : images) {
				FeedImage feedImage = FeedImage.builder()
						.image(image)
						.feed(findFeed)
						.build();

				feedImageRepository.save(feedImage);
			}
		}

		return feedImageRepository.findByFeed_Id(findFeed.getId());
	}

	// 메인 페이지 전체 피드 목록 조회, 로그인 여부에 따라 다른 피드 목록 반환
	public Page<FeedResponse> getFeedsForMain(MemberDto memberDto, Pageable pageable) {
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
					.filter(f -> !f.getFollowing().getUsername().equals(loginMember.getUsername()))
					.map(f -> f.getFollowing().getUsername()).forEach(createdBy::add);
			members.stream()
					.filter(f -> !f.getFollower().getUsername().equals(loginMember.getUsername()))
					.map(f -> f.getFollower().getUsername()).forEach(createdBy::add);

			Page<Feed> feeds = feedRepository.searchByCreatedBy(feedStatuses, createdBy,
					pageRequest);

			return feeds.map(f -> FeedResponse.createFeedResponse(f, f.getFeedImages()));
		} else {
			//비로그인 상태
			Page<Feed> feeds = feedRepository.searchByCreatedBy(feedStatuses, null,
					pageRequest);

			return feeds.map(f -> FeedResponse.createFeedResponse(f, f.getFeedImages()));
		}
	}

	// 헤더 검색창(닉네임)을 통한 피드 목록 반환
	public Page<FeedResponse> getFeedsForMainWithSearchText(String nickname, Pageable pageable) {
		PageRequest pageRequest = PageRequest.of(pageable.getPageNumber(), pageable.getPageSize(),
				Sort.by(Direction.DESC, "hits"));

		List<FeedStatus> feedStatuses = List.of(NORMAL, ACCUSE);

		Page<Feed> feeds = feedRepository.searchBySearchText(feedStatuses, nickname, pageRequest);

		return feeds.map(f -> FeedResponse.createFeedResponse(f, f.getFeedImages()));
	}

	// 관리자 페이지 피드 목록 조회
	public Page<FeedResponse> getFeedsForAdmin(MemberDto memberDto, String searchText, Pageable pageable) {
		// 로그인 상태 및 권한 확인
		if (memberDto != null) {
			if (memberDto.getRole() != MemberRole.ROLE_ADMIN) {
				throw new AlbumException(NO_AUTHORITY);
			}
		} else {
			throw new AlbumException(REQUIRED_LOGIN);
		}

		// 데이터 조회 조건 설정
		List<FeedStatus> statuses = List.of(NORMAL, ACCUSE, DELETE);

		PageRequest pageRequest = PageRequest.of(pageable.getPageNumber(), pageable.getPageSize(),
				Sort.by(Order.asc("status"), Order.desc("createdAt")));

		// 데이터 조회
		Page<Feed> feeds = feedRepository.searchBySearchText(statuses, searchText, pageRequest);

		// 변환 및 반환
		return feeds.map(f -> FeedResponse.createFeedResponse(f, f.getFeedImages()));
	}

	// 특정 피드 조회
	@Transactional
	public FeedResponse getFeed(Long feedId) {
		Feed findFeed = feedRepository.findById(feedId)
				.orElseThrow(() -> new AlbumException(NOT_EXIST_FEED));

		List<FeedImage> feedImages = feedImageRepository.findByFeed_Id(findFeed.getId());

		findFeed.addHits();

		return FeedResponse.createFeedResponse(findFeed, feedImages);
	}

	// 피드 등록
	public FeedResponse feed(FeedPostForm feedPostForm, List<MultipartFile> imageFiles,
							 MemberDto memberDto) {
		if (memberDto == null) {
			throw new AlbumException(REQUIRED_LOGIN);
		}

		Member loginMember = memberRepository.findById(memberDto.getId())
				.orElseThrow(() -> new AlbumException(NOT_EXIST_MEMBER));

		// Feed 데이터 등록
		Feed feed = Feed.builder()
				.title(feedPostForm.getTitle())
				.content(feedPostForm.getContent())
				.hits(0)
				.commentCount(0)
				.status(NORMAL)
				.member(loginMember)
				.build();

		feedRepository.save(feed);

		// FeedImage 데이터 등록
		List<Image> images = awsS3Manager.uploadImage(imageFiles);
		List<FeedImage> feedImages = saveFeedImage(images, feed);

		return FeedResponse.createFeedResponse(feed, feedImages);
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
	public FeedResponse modifiedFeed(FeedModifiedForm feedModifiedForm, List<MultipartFile> imageFiles,
									 MemberDto memberDto) {

		// 로그인 여부 확인
		if (memberDto == null) {
			throw new AlbumException(REQUIRED_LOGIN);
		}

		// 목표 피드 데이터 조회
		Feed findFeed = feedRepository.findById(feedModifiedForm.getId())
				.orElseThrow(() -> new AlbumException(NOT_EXIST_FEED));

		// 본인 여부 확인
		if (!findFeed.getMember().getUsername().equals(memberDto.getUsername())) {
			throw new AlbumException(NO_AUTHORITY);
		}

		// Feed 데이터 수정
		findFeed.modified(feedModifiedForm);

		// 이전 FeedImage 데이터 삭제
		feedImageRepository.deleteFeedImageByFeed_Id(findFeed.getId());

		// 새로운 FeedImage 데이터 등록
		List<Image> images = awsS3Manager.uploadImage(imageFiles);
		List<FeedImage> feedImages = saveFeedImage(images, findFeed);

		return FeedResponse.createFeedResponse(findFeed, feedImages);
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

	public List<FeedAccuseDto> getFeedAccuses(MemberDto memberDto, Long feedId) {
		// 로그인 및 권한 확인
		if (memberDto != null) {
			if (memberDto.getRole() != ROLE_ADMIN) {
				throw new AlbumException(NO_AUTHORITY);
			}
		} else {
			throw new AlbumException(REQUIRED_LOGIN);
		}

		// 데이터 조회
		List<FeedAccuse> feedAccuses = feedAccuseRepository.findFeedAccuseByFeed_Id(feedId);

		// 데이터 변환 및 반환
		return feedAccuses.stream().map(FeedAccuseDto::from).collect(Collectors.toList());
	}

	public Page<FeedResponse> getMyFeeds(Long memberId, MemberDto memberDto, Pageable pageable) {
		// 로그인 여부 확인
		if (memberDto == null) {
			throw new AlbumException(REQUIRED_LOGIN);
		}

		List<FeedStatus> statuses = List.of(NORMAL, ACCUSE);

		Page<Feed> feeds = feedRepository.findByStatusInAndMember_Id(statuses,
				memberId, pageable);

		return feeds.map(f -> FeedResponse.createFeedResponse(f, f.getFeedImages()));
	}
}

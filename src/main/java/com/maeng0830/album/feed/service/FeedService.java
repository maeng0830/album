package com.maeng0830.album.feed.service;

import static com.maeng0830.album.feed.exception.FeedExceptionCode.NOT_EXIST_FEED;
import static com.maeng0830.album.member.exception.MemberExceptionCode.NOT_EXIST_MEMBER;
import static com.maeng0830.album.member.exception.MemberExceptionCode.NO_AUTHORITY;

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
import com.maeng0830.album.feed.repository.FeedAccuseRepository;
import com.maeng0830.album.feed.repository.FeedImageRepository;
import com.maeng0830.album.feed.repository.FeedRepository;
import com.maeng0830.album.follow.domain.Follow;
import com.maeng0830.album.follow.repository.FollowRepository;
import com.maeng0830.album.member.domain.Member;
import com.maeng0830.album.member.domain.MemberRole;
import com.maeng0830.album.member.dto.MemberDto;
import com.maeng0830.album.member.repository.MemberRepository;
import com.maeng0830.album.security.formlogin.PrincipalDetails;
import java.io.File;
import java.io.IOException;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
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
	public void saveFeedImage(List<MultipartFile> imageFiles, Feed findFeed) {
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
	}

	// 전체 피드 목록 조회, 로그인 여부에 따라 다른 피드 목록 반환
	public List<FeedResponse> getFeeds(PrincipalDetails principalDetails) {

		boolean loginCheck = principalDetails != null;

		//로그인 상태
		if (loginCheck) {
			MemberDto memberDto = principalDetails.getMemberDto();

			Member loginMember = memberRepository.findById(memberDto.getId())
					.orElseThrow(() -> new AlbumException(
							NOT_EXIST_MEMBER));

			// 로그인 회원이 팔로우 하는 회원
			List<Follow> followers = followRepository.findByFollower(loginMember);
			// 로그인 회원을 팔로우 하는 회원
			List<Follow> followees = followRepository.findByFollowee(loginMember);

			Set<String> createdBy = new HashSet<>();

			followers.stream().map(f -> f.getFollowee().getUsername()).forEach(createdBy::add);
			followees.stream().map(f -> f.getFollower().getUsername()).forEach(createdBy::add);

			List<Feed> feeds = feedRepository.findByStatusAndCreatedBy(FeedStatus.DELETE,
					createdBy);

			return feeds.stream().map(f -> new FeedResponse(f, f.getFeedImages()))
					.collect(Collectors.toList());
		}

		//비로그인 상태
		List<Feed> feeds = feedRepository.findFetchJoinByStatusNot(FeedStatus.DELETE);
		System.out.println("feeds.size() = " + feeds.size());

		return feeds.stream().map(f -> new FeedResponse(f, f.getFeedImages()))
				.collect(Collectors.toList());
	}

	// 특정 피드 조회
	public FeedResponse getFeed(Long feedId) {
		Feed findFeed = feedRepository.findById(feedId)
				.orElseThrow(() -> new AlbumException(NOT_EXIST_FEED));

		List<FeedImage> feedImages = feedImageRepository.findByFeed_Id(findFeed.getId());

		return new FeedResponse(findFeed, feedImages);
	}

	// 피드 등록
	public FeedResponse feed(FeedDto feedDto, List<MultipartFile> imageFiles,
							 PrincipalDetails principalDetails) {

		// 로그인 여부 확인
		MemberDto loginMemberDto = albumUtil.checkLogin(principalDetails);

		Member loginMember = memberRepository.findById(loginMemberDto.getId())
				.orElseThrow(() -> new AlbumException(NOT_EXIST_MEMBER));

		// Feed 데이터 등록
		Feed feed = Feed.builder()
				.title(feedDto.getTitle())
				.content(feedDto.getContent())
				.hits(0)
				.commentCount(0)
				.likeCount(0)
				.status(FeedStatus.NORMAL)
				.member(loginMember)
				.build();

		feedRepository.save(feed);

		// FeedImage 데이터 등록
		saveFeedImage(imageFiles, feed);

		List<FeedImage> feedImages = feedImageRepository.findByFeed_Id(feed.getId());

		return new FeedResponse(feed, feedImages);
	}

	// 피드 삭제
	@Transactional
	public FeedDto deleteFeed(Long feedId, PrincipalDetails principalDetails) {

		// 로그인 여부 확인
		MemberDto loginMemberDto = albumUtil.checkLogin(principalDetails);

		// 목표 피드 데이터 조회
		Feed findFeed = feedRepository.findById(feedId)
				.orElseThrow(() -> new AlbumException(NOT_EXIST_FEED));

		// 본인 또는 관리자 여부 확인
		if (!findFeed.getCreatedBy().equals(loginMemberDto.getUsername())
				&& !loginMemberDto.getRole().equals(
				MemberRole.ROLE_ADMIN)) {
			throw new AlbumException(NO_AUTHORITY);
		}

		// Feed 상태 변경
		findFeed.changeStatus(FeedStatus.DELETE);

		return FeedDto.from(findFeed);
	}

	// 피드 수정
	@Transactional
	public FeedResponse modifiedFeed(Long feedId, FeedDto feedDto, List<MultipartFile> imageFiles,
									 PrincipalDetails principalDetails) {

		// 로그인 여부 확인
		MemberDto loginMemberDto = albumUtil.checkLogin(principalDetails);

		// 목표 피드 데이터 조회
		Feed findFeed = feedRepository.findById(feedId)
				.orElseThrow(() -> new AlbumException(NOT_EXIST_FEED));

		// 본인 여부 확인
		if (!findFeed.getCreatedBy().equals(loginMemberDto.getUsername())) {
			throw new AlbumException(NO_AUTHORITY);
		}

		// Feed 데이터 수정
		findFeed.modified(feedDto);

		// 이전 FeedImage 데이터 삭제
		feedImageRepository.deleteFeedImageByFeed_Id(findFeed.getId());

		// 새로운 FeedImage 데이터 등록
		saveFeedImage(imageFiles, findFeed);

		List<FeedImage> feedImages = feedImageRepository.findByFeed_Id(findFeed.getId());

		return new FeedResponse(findFeed, feedImages);
	}

	// 피드 신고
	@Transactional
	public FeedAccuseDto accuseFeed(Long feedId, FeedAccuseDto feedAccuseDto, PrincipalDetails principalDetails) {

		// 로그인 여부 확인
		MemberDto memberDto = albumUtil.checkLogin(principalDetails);

		// 신고 피드 조회 및 상태 변경
		Feed findFeed = feedRepository.findById(feedId)
				.orElseThrow(() -> new AlbumException(NOT_EXIST_FEED));

		findFeed.changeStatus(FeedStatus.ACCUSE);

		// 신고자 조회
		Member findMember = memberRepository.findByUsername(memberDto.getUsername())
				.orElseThrow(() -> new AlbumException(NOT_EXIST_MEMBER));

		// 신고 내역 저장
		FeedAccuse savedFeedAccuse = feedAccuseRepository.save(
				FeedAccuse.builder()
						.content(feedAccuseDto.getContent())
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

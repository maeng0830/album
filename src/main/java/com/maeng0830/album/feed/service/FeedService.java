package com.maeng0830.album.feed.service;

import static com.maeng0830.album.feed.exception.FeedExceptionCode.*;
import static com.maeng0830.album.member.exception.MemberExceptionCode.*;
import static com.maeng0830.album.member.exception.MemberExceptionCode.NOT_EXIST_MEMBER;
import static com.maeng0830.album.member.exception.MemberExceptionCode.REQUIRED_LOGIN;

import com.maeng0830.album.common.exception.AlbumException;
import com.maeng0830.album.common.filedir.FileDir;
import com.maeng0830.album.common.model.Image;
import com.maeng0830.album.feed.domain.Feed;
import com.maeng0830.album.feed.domain.FeedImage;
import com.maeng0830.album.feed.domain.FeedStatus;
import com.maeng0830.album.feed.dto.FeedDto;
import com.maeng0830.album.feed.dto.FeedResponse;
import com.maeng0830.album.feed.exception.FeedExceptionCode;
import com.maeng0830.album.feed.repository.FeedImageRepository;
import com.maeng0830.album.feed.repository.FeedRepository;
import com.maeng0830.album.follow.domain.Follow;
import com.maeng0830.album.follow.repository.FollowRepository;
import com.maeng0830.album.member.domain.Member;
import com.maeng0830.album.member.domain.MemberRole;
import com.maeng0830.album.member.dto.MemberDto;
import com.maeng0830.album.member.exception.MemberExceptionCode;
import com.maeng0830.album.member.repository.MemberRepository;
import com.maeng0830.album.security.formlogin.PrincipalDetails;
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
	private final MemberRepository memberRepository;
	private final FollowRepository followRepository;
	private final FileDir fileDir;

	// 전체 피드 목록 조회
	// 로그인 여부에 따라 다른 피드 목록 반환
	public List<FeedDto> getFeeds(PrincipalDetails principalDetails) {

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

			List<Feed> feeds = feedRepository.findByStatusNotAndCreatedByIn(FeedStatus.DELETE,
					createdBy);

			return feeds.stream().map(FeedDto::from).collect(Collectors.toList());
		}

		//비로그인 상태
		List<Feed> feeds = feedRepository.findByStatusNot(FeedStatus.DELETE);

		return feeds.stream().map(FeedDto::from).collect(Collectors.toList());
	}

	// 특정 피드 조회
	public FeedDto getFeed(Long feedId) {
		Feed feed = feedRepository.findById(feedId)
				.orElseThrow(() -> new AlbumException(NOT_EXIST_FEED));

		return FeedDto.from(feed);
	}

	// 피드 등록
	public FeedResponse feed(FeedDto feedDto, List<MultipartFile> imageFiles,
						PrincipalDetails principalDetails) {

		// 로그인 여부 확인
		Member loginMember;

		try {
			MemberDto loginMemberDto = principalDetails.getMemberDto();
			loginMember = memberRepository.findById(loginMemberDto.getId())
					.orElseThrow(() -> new AlbumException(NOT_EXIST_MEMBER));
		} catch (NullPointerException e) {
			throw new AlbumException(REQUIRED_LOGIN, e);
		}

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
		for (int i = 0; i < imageFiles.size(); i++) {
			MultipartFile imageFile = imageFiles.get(i);

			FeedImage feedImage = FeedImage.builder()
					.image(new Image(imageFile.getOriginalFilename(),
							fileDir.getDir() + imageFile.getOriginalFilename()))
					.feed(feed)
					.build();

			feedImageRepository.save(feedImage);
		}

		List<FeedImage> feedImages = feedImageRepository.findByFeed_Id(feed.getId());

		return new FeedResponse(feed, feedImages);
	}

	// 피드 삭제
	@Transactional
	public FeedDto deleteFeed(Long feedId, PrincipalDetails principalDetails) {

		// 로그인 여부 확인
		MemberDto loginMemberDto;

		try {
			loginMemberDto = principalDetails.getMemberDto();
		} catch (NullPointerException e) {
			throw new AlbumException(REQUIRED_LOGIN, e);
		}

		// 목표 피드 데이터 조회
		Feed findFeed = feedRepository.findById(feedId)
				.orElseThrow(() -> new AlbumException(NOT_EXIST_FEED));

		// 목표 피드의 작성자 또는 관리자가 아닐 경우 예외 처리
		if (!findFeed.getCreatedBy().equals(loginMemberDto.getUsername()) && !loginMemberDto.getRole().equals(
				MemberRole.ROLE_ADMIN)) {
			throw new AlbumException(NO_AUTHORITY);
		}

		// Feed 상태 변경
		findFeed.changeStatus(FeedStatus.DELETE);

		return FeedDto.from(findFeed);
	}

	// 피드 수정
}

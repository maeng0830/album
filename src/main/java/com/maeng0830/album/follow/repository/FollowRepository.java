package com.maeng0830.album.follow.repository;

import com.maeng0830.album.follow.domain.Follow;
import com.maeng0830.album.follow.repository.custom.FollowRepositoryCustom;
import com.maeng0830.album.member.domain.Member;
import java.util.Optional;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.transaction.annotation.Transactional;

public interface FollowRepository extends JpaRepository<Follow, Long>, FollowRepositoryCustom {

	@Transactional
	int deleteByFollowerAndFollowing(Member follower, Member following);

	Optional<Follow> findByFollower_IdAndFollowing_Id(Long followerId, Long followingId);

	@EntityGraph(attributePaths = {"follower", "following"})
	Page<Follow> getFollowByFollower_IdAndFollowing_Nickname(Long followerId, String followingNickname, Pageable pageable);

	@EntityGraph(attributePaths = {"follower", "following"})
	Page<Follow> getFollowByFollowing_IdAndFollower_Nickname(Long followingId, String followerNickname, Pageable pageable);
}

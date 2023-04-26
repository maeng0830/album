package com.maeng0830.album.follow.repository;

import com.maeng0830.album.follow.domain.Follow;
import com.maeng0830.album.member.domain.Member;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.transaction.annotation.Transactional;

public interface FollowRepository extends JpaRepository<Follow, Long> {

	@Transactional
	void deleteByFollowerAndFollowee(Member follower, Member followee);

	List<Follow> findByFollower(Member follower);

	List<Follow> findByFollowee(Member followee);
}

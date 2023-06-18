package com.maeng0830.album.follow.repository;

import com.maeng0830.album.follow.domain.Follow;
import com.maeng0830.album.member.domain.Member;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.transaction.annotation.Transactional;

public interface FollowRepository extends JpaRepository<Follow, Long> {

	@Transactional
	int deleteByFollowerAndFollowee(Member follower, Member followee);

	@Query("select f from Follow f left join fetch f.followee where f.follower = :follower")
	List<Follow> findByFollower(Member follower);

	@Query("select f from Follow f left join fetch f.follower where f.followee = :followee")
	List<Follow> findByFollowee(Member followee);
}

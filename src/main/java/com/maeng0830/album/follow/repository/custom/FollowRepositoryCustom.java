package com.maeng0830.album.follow.repository.custom;

import com.maeng0830.album.follow.domain.Follow;
import com.maeng0830.album.member.domain.Member;
import java.util.List;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface FollowRepositoryCustom {

	List<Follow> searchByFollowerOrFollowee(Member follower, Member followee);

	Page<Follow> searchForMyFollowings(Long followerId, String followingNickname, Pageable pageable);

	Page<Follow> searchForMyFollowers(Long followingId, String followerNickname, Pageable pageable);
}

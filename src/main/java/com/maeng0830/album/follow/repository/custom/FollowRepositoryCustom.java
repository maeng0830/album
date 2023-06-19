package com.maeng0830.album.follow.repository.custom;

import com.maeng0830.album.follow.domain.Follow;
import com.maeng0830.album.member.domain.Member;
import java.util.List;

public interface FollowRepositoryCustom {

	List<Follow> searchByFollowerOrFollowee(Member follower, Member followee);
}

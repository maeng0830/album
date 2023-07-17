package com.maeng0830.album.follow.repository.custom;

import static com.maeng0830.album.follow.domain.QFollow.follow;
import static com.maeng0830.album.member.domain.QMember.member;

import com.maeng0830.album.follow.domain.Follow;
import com.maeng0830.album.member.domain.Member;
import com.querydsl.core.BooleanBuilder;
import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.jpa.impl.JPAQuery;
import com.querydsl.jpa.impl.JPAQueryFactory;
import java.util.List;
import javax.persistence.EntityManager;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.support.PageableExecutionUtils;

public class FollowRepositoryImpl implements FollowRepositoryCustom {

	private final JPAQueryFactory queryFactory;

	public FollowRepositoryImpl(EntityManager em) {
		this.queryFactory = new JPAQueryFactory(em);
	}

	@Override
	public List<Follow> searchByFollowerOrFollowee(Member follower, Member following) {
		return queryFactory
				.select(follow)
				.from(follow)
				.leftJoin(follow.follower, member)
				.leftJoin(follow.following, member)
				.where(followerOrFollowingCondition(follower, following))
				.fetch();
	}

	public Page<Follow> searchForMyFollowings(Long followerId, String followingNickname,
											  Pageable pageable) {

		List<Follow> content = queryFactory
				.select(follow)
				.from(follow)
				.leftJoin(follow.follower, member).fetchJoin()
				.leftJoin(follow.following, member).fetchJoin()
				.where(searchForMyFollowingsCondition(followerId, followingNickname))
				.offset(pageable.getOffset())
				.limit(pageable.getPageSize())
				.fetch();

		JPAQuery<Long> count = queryFactory
				.select(follow.count())
				.from(follow)
				.where(searchForMyFollowingsCondition(followerId, followingNickname));

		return PageableExecutionUtils.getPage(content, pageable, count::fetchOne);
	}

	@Override
	public Page<Follow> searchForMyFollowers(Long followingId, String followerNickname,
											 Pageable pageable) {
		List<Follow> content = queryFactory
				.select(follow)
				.from(follow)
				.leftJoin(follow.follower, member).fetchJoin()
				.leftJoin(follow.following, member).fetchJoin()
				.where(searchForMyFollowersCondition(followingId, followerNickname))
				.offset(pageable.getOffset())
				.limit(pageable.getPageSize())
				.fetch();

		JPAQuery<Long> count = queryFactory
				.select(follow.count())
				.from(follow)
				.where(searchForMyFollowersCondition(followingId, followerNickname));

		return PageableExecutionUtils.getPage(content, pageable, count::fetchOne);
	}

	private BooleanExpression followerEq(Member follower) {
		return follower == null ? null : follow.follower.eq(follower);
	}

	private BooleanExpression followingEq(Member following) {
		return following == null ? null : follow.following.eq(following);
	}

	private BooleanExpression followerIdEq(Long followerId) {
		return followerId == null ? null : follow.follower.id.eq(followerId);
	}

	private BooleanExpression followingIdEq(Long followingId) {
		return followingId == null ? null : follow.following.id.eq(followingId);
	}

	private BooleanExpression followingNicknameLike(String searchText) {
		return searchText == null ? null : follow.following.nickname.like(searchText + "%");
	}

	private BooleanExpression followerNicknameLike(String searchText) {
		return searchText == null ? null : follow.follower.nickname.like(searchText + "%");
	}

	private BooleanBuilder followerOrFollowingCondition(Member follower, Member following) {
		BooleanBuilder builder = new BooleanBuilder();
		BooleanExpression followerEq = followerEq(follower);
		BooleanExpression followingEq = followingEq(following);

		if (followerEq != null) {
			builder.or(followerEq);
		}

		if (followingEq != null) {
			builder.or(followingEq);
		}

		return builder;
	}

	private BooleanBuilder searchForMyFollowingsCondition(Long followerId, String followingNickname) {
		BooleanBuilder builder = new BooleanBuilder();
		BooleanExpression followerIdEq = followerIdEq(followerId);
		BooleanExpression followingNicknameLike = followingNicknameLike(followingNickname);

		if (followerIdEq != null) {
			builder.and(followerIdEq);
		}

		if (followingNicknameLike != null) {
			builder.and(followingNicknameLike);
		}

		return builder;
	}

	private BooleanBuilder searchForMyFollowersCondition(Long followingId, String followerNickname) {
		BooleanBuilder builder = new BooleanBuilder();
		BooleanExpression followingIdEq = followingIdEq(followingId);
		BooleanExpression followerNicknameLike = followerNicknameLike(followerNickname);

		if (followingIdEq != null) {
			builder.and(followingIdEq);
		}

		if (followerNicknameLike != null) {
			builder.and(followerNicknameLike);
		}

		return builder;
	}
}

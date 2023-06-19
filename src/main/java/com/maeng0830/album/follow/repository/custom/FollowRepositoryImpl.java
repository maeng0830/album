package com.maeng0830.album.follow.repository.custom;

import static com.maeng0830.album.follow.domain.QFollow.follow;
import static com.maeng0830.album.member.domain.QMember.member;

import com.maeng0830.album.follow.domain.Follow;
import com.maeng0830.album.member.domain.Member;
import com.querydsl.core.BooleanBuilder;
import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.jpa.impl.JPAQueryFactory;
import java.util.List;
import javax.persistence.EntityManager;

public class FollowRepositoryImpl implements FollowRepositoryCustom {

	private final JPAQueryFactory queryFactory;

	public FollowRepositoryImpl(EntityManager em) {
		this.queryFactory = new JPAQueryFactory(em);
	}

	@Override
	public List<Follow> searchByFollowerOrFollowee(Member follower, Member followee) {
		return queryFactory
				.select(follow)
				.from(follow)
				.leftJoin(follow.follower, member)
				.leftJoin(follow.followee, member)
				.where(searchCondition(follower, followee))
				.fetch();
	}

	private BooleanExpression followerEq(Member follower) {
		return follower == null ? null : follow.follower.eq(follower);
	}

	private BooleanExpression followeeEq(Member followee) {
		return followee == null ? null : follow.followee.eq(followee);
	}

	private BooleanBuilder searchCondition(Member follower, Member followee) {
		BooleanBuilder builder = new BooleanBuilder();
		BooleanExpression followerEq = followerEq(follower);
		BooleanExpression followeeEq = followeeEq(followee);

		if (followerEq != null) {
			builder.or(followerEq);
		}

		if (followeeEq != null) {
			builder.or(followeeEq);
		}

		return builder;
	}
}

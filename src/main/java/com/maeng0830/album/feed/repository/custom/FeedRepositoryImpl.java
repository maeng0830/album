package com.maeng0830.album.feed.repository.custom;

import static com.maeng0830.album.feed.domain.QFeed.feed;
import static com.maeng0830.album.feed.domain.QFeedImage.feedImage;

import com.maeng0830.album.feed.domain.Feed;
import com.maeng0830.album.feed.domain.FeedStatus;
import com.querydsl.core.BooleanBuilder;
import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.jpa.impl.JPAQueryFactory;
import java.util.Collection;
import java.util.List;
import javax.persistence.EntityManager;

public class FeedRepositoryImpl implements FeedRepositoryCustom {

	private final JPAQueryFactory jpaQueryFactory;

	public FeedRepositoryImpl(EntityManager em) {
		jpaQueryFactory = new JPAQueryFactory(em);
	}

	@Override
	public List<Feed> searchByStatusAndCreatedBy(Collection<FeedStatus> status,
												 Collection<String> createdBy) {
		return jpaQueryFactory
				.select(feed).distinct()
				.from(feed)
				.leftJoin(feed.feedImages, feedImage).fetchJoin()
				.where(searchCondition(status, createdBy))
				.fetch();
	}

	private BooleanExpression statusIn(Collection<FeedStatus> status) {
		return status == null ? null : feed.status.in(status);
	}

	private BooleanExpression createdByIn(Collection<String> createdBy) {
		return createdBy == null ? null : feed.createdBy.in(createdBy);
	}

	private BooleanBuilder searchCondition(Collection<FeedStatus> status,
										   Collection<String> createdBy) {

		BooleanBuilder builder = new BooleanBuilder();
		BooleanExpression statusIn = statusIn(status);
		BooleanExpression createdByIn = createdByIn(createdBy);

		if (statusIn != null) {
			builder.and(statusIn);
		}

		if (createdByIn != null) {
			builder.and(createdByIn);
		}

		return builder;
	}
}

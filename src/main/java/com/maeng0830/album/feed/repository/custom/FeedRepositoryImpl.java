package com.maeng0830.album.feed.repository.custom;

import static com.maeng0830.album.feed.domain.QFeed.feed;
import static com.maeng0830.album.feed.domain.QFeedImage.feedImage;

import com.maeng0830.album.common.util.AlbumUtil;
import com.maeng0830.album.feed.domain.Feed;
import com.maeng0830.album.feed.domain.FeedStatus;
import com.querydsl.core.BooleanBuilder;
import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.jpa.impl.JPAQuery;
import com.querydsl.jpa.impl.JPAQueryFactory;
import java.util.Collection;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.support.PageableExecutionUtils;

@RequiredArgsConstructor
public class FeedRepositoryImpl implements FeedRepositoryCustom {

	private final JPAQueryFactory jpaQueryFactory;
	private final AlbumUtil albumUtil;

	@Override
	public Page<Feed> searchByStatusAndCreatedBy(Collection<FeedStatus> status,
												 Collection<String> createdBy, Pageable pageable) {

		List<Feed> content = jpaQueryFactory
				.select(feed).distinct()
				.from(feed)
				.leftJoin(feed.feedImages, feedImage).fetchJoin()
				.where(searchCondition(status, createdBy))
				.orderBy(albumUtil.getOrderSpecifier(pageable.getSort(), feed))
				.offset(pageable.getOffset())
				.limit(pageable.getPageSize())
				.fetch();

		JPAQuery<Long> count = jpaQueryFactory
				.select(feed.count())
				.from(feed)
				.where(searchCondition(status, createdBy));

		return PageableExecutionUtils.getPage(content, pageable, count::fetchOne);
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

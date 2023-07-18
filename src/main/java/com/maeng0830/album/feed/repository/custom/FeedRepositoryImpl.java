package com.maeng0830.album.feed.repository.custom;

import static com.maeng0830.album.feed.domain.QFeed.feed;
import static com.maeng0830.album.feed.domain.QFeedImage.feedImage;
import static com.maeng0830.album.member.domain.QMember.member;

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
	public Page<Feed> searchByCreatedBy(Collection<FeedStatus> status,
										Collection<String> createdBy, Pageable pageable) {

		List<Feed> content = jpaQueryFactory
				.select(feed).distinct()
				.from(feed)
				.leftJoin(feed.feedImages, feedImage).fetchJoin()
				.leftJoin(feed.member, member).fetchJoin()
				.where(searchByCreatedByCondition(status, createdBy))
				.orderBy(albumUtil.getOrderSpecifier(pageable.getSort(), feed))
				.offset(pageable.getOffset())
				.limit(pageable.getPageSize())
				.fetch();

		JPAQuery<Long> count = jpaQueryFactory
				.select(feed.count())
				.from(feed)
				.where(searchByCreatedByCondition(status, createdBy));

		return PageableExecutionUtils.getPage(content, pageable, count::fetchOne);
	}

	@Override
	public Page<Feed> searchBySearchText(Collection<FeedStatus> status, String searchText,
										 Pageable pageable) {
		List<Feed> content = jpaQueryFactory
				.select(feed).distinct()
				.from(feed)
				.leftJoin(feed.feedImages, feedImage).fetchJoin()
				.leftJoin(feed.member, member).fetchJoin()
				.where(searchBySearchTextCondition(status, searchText))
				.orderBy(albumUtil.getOrderSpecifier(pageable.getSort(), feed))
				.offset(pageable.getOffset())
				.limit(pageable.getPageSize())
				.fetch();

		JPAQuery<Long> count = jpaQueryFactory
				.select(feed.count())
				.from(feed)
				.where(searchBySearchTextCondition(status, searchText));

		return PageableExecutionUtils.getPage(content, pageable, count::fetchOne);
	}

	private BooleanExpression statusIn(Collection<FeedStatus> status) {
		return status == null ? null : feed.status.in(status);
	}

	private BooleanExpression createdByIn(Collection<String> createdBy) {
		return createdBy == null ? null : feed.member.username.in(createdBy);
	}

	private BooleanExpression usernameLike(String searchText) {
		return searchText == null ? null : feed.member.username.like(searchText + "%");
	}

	private BooleanExpression nicknameLike(String searchText) {
		return searchText == null ? null : feed.member.nickname.like(searchText + "%");
	}

	private BooleanBuilder searchByCreatedByCondition(Collection<FeedStatus> status, Collection<String> createdBy) {
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

	private BooleanBuilder searchBySearchTextCondition(Collection<FeedStatus> status,String searchText) {
		BooleanBuilder builder = new BooleanBuilder();
		BooleanExpression statusIn = statusIn(status);
		BooleanExpression usernameLike = usernameLike(searchText);
		BooleanExpression nicknameLike = nicknameLike(searchText);

		if (statusIn != null) {
			builder.and(statusIn);
		}

		if (usernameLike != null && nicknameLike != null) {
			builder.and(usernameLike.or(nicknameLike));
		}

		return builder;
	}
}

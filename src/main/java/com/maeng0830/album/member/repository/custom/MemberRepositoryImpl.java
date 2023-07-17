package com.maeng0830.album.member.repository.custom;

import static com.maeng0830.album.member.domain.QMember.member;

import com.maeng0830.album.common.util.AlbumUtil;
import com.maeng0830.album.member.domain.Member;
import com.maeng0830.album.member.domain.MemberStatus;
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
public class MemberRepositoryImpl implements MemberRepositoryCustom {

	private final JPAQueryFactory jpaQueryFactory;
	private final AlbumUtil albumUtil;

	@Override
	public Page<Member> searchBySearchText(Collection<MemberStatus> status,
										   String searchText,
										   Pageable pageable) {
		List<Member> content = jpaQueryFactory
				.select(member)
				.from(member)
				.where(searchTextCondition(status, searchText))
				.orderBy(albumUtil.getOrderSpecifier(pageable.getSort(), member))
				.offset(pageable.getOffset())
				.limit(pageable.getPageSize())
				.fetch();

		JPAQuery<Long> count = jpaQueryFactory
				.select(member.count())
				.from(member)
				.where(searchTextCondition(status, searchText));

		return PageableExecutionUtils.getPage(content, pageable, count::fetchOne);
	}

	private BooleanExpression usernameLike(String searchText) {
		return searchText == null ? null : member.username.like(searchText + "%");
	}

	private BooleanExpression nicknameLike(String searchText) {
		return searchText == null ? null : member.nickname.like(searchText + "%");
	}

	private BooleanExpression statusIn(Collection<MemberStatus> status) {
		return status == null ? null : member.status.in(status);
	}

	private BooleanBuilder searchTextCondition(Collection<MemberStatus> status, String searchText) {
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

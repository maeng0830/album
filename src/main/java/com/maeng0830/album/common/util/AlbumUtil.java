package com.maeng0830.album.common.util;

import com.maeng0830.album.member.dto.MemberDto;
import com.maeng0830.album.security.formlogin.PrincipalDetails;
import com.querydsl.core.types.NullExpression;
import com.querydsl.core.types.Order;
import com.querydsl.core.types.OrderSpecifier;
import com.querydsl.core.types.OrderSpecifier.NullHandling;
import com.querydsl.core.types.dsl.EntityPathBase;
import com.querydsl.core.types.dsl.PathBuilder;
import com.querydsl.core.types.dsl.PathBuilderFactory;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;
import org.springframework.data.domain.Sort;


public class AlbumUtil {

	// 로그인 여부 확인
	public MemberDto checkLogin(PrincipalDetails principalDetails) {
		try {
			return principalDetails.getMemberDto();
		} catch (NullPointerException e) {
			return null;
		}
	}

	// queryDsl 정렬
	@SuppressWarnings(value = {"unchecked", "rawtypes"})
	public<T> OrderSpecifier<?>[] getOrderSpecifier(Sort sort, EntityPathBase<T> qType) {

		// sort 체크
		boolean hasSort = !sort.isEmpty();

		List<OrderSpecifier> orders = new ArrayList<>();

		if (hasSort) {
			sort.stream().forEach(order -> {
				Order direction = order.isAscending() ? Order.ASC : Order.DESC;
				String property = order.getProperty();

				PathBuilder<?> pathBuilder = new PathBuilder<>(qType.getType(), qType.getMetadata());

				orders.add(new OrderSpecifier(direction, pathBuilder.get(property)));
			});
		} else {
			PathBuilder<?> pathBuilder = new PathBuilder<>(qType.getType(), qType.getMetadata());
			orders.add(new OrderSpecifier(Order.ASC, pathBuilder.get("id")));
		}

		return orders.toArray(OrderSpecifier[]::new);
	}
}

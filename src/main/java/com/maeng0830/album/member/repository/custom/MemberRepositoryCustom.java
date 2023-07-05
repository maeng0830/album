package com.maeng0830.album.member.repository.custom;

import com.maeng0830.album.member.domain.Member;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface MemberRepositoryCustom {

	Page<Member> searchBySearchText(String searchText, Pageable pageable);
}

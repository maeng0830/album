package com.maeng0830.album.member.repository.custom;

import com.maeng0830.album.feed.domain.FeedStatus;
import com.maeng0830.album.member.domain.Member;
import com.maeng0830.album.member.domain.MemberStatus;
import java.util.Collection;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface MemberRepositoryCustom {

	Page<Member> searchBySearchText(Collection<MemberStatus> status, String searchText, Pageable pageable);
}

package com.maeng0830.album.feed.repository;

import com.maeng0830.album.feed.domain.Feed;
import com.maeng0830.album.feed.domain.FeedStatus;
import com.maeng0830.album.feed.repository.custom.FeedRepositoryCustom;
import java.util.Collection;
import java.util.List;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface FeedRepository extends JpaRepository<Feed, Long>, FeedRepositoryCustom {

	@EntityGraph(attributePaths = {"member"})
	Page<Feed> findByStatusInAndMember_Id(Collection<FeedStatus> statuses, Long memberId, Pageable pageable);
}

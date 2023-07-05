package com.maeng0830.album.feed.repository;

import com.maeng0830.album.feed.domain.FeedAccuse;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;

public interface FeedAccuseRepository extends JpaRepository<FeedAccuse, Long> {
	List<FeedAccuse> findFeedAccuseByFeed_Id(Long feedId);
}

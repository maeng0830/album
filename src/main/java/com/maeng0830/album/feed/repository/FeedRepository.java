package com.maeng0830.album.feed.repository;

import com.maeng0830.album.feed.domain.Feed;
import com.maeng0830.album.feed.domain.FeedStatus;
import java.util.Collection;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;

public interface FeedRepository extends JpaRepository<Feed, Long> {

	List<Feed> findByStatusNotAndCreatedByIn(FeedStatus status, Collection<String> createdBy);

	List<Feed> findByStatusNot(FeedStatus status);
}

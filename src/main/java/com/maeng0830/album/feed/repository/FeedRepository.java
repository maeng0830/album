package com.maeng0830.album.feed.repository;

import com.maeng0830.album.feed.domain.Feed;
import com.maeng0830.album.feed.domain.FeedStatus;
import java.util.Collection;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface FeedRepository extends JpaRepository<Feed, Long> {

	@Query("select f from Feed f left join fetch f.feedImages where f.status <> :status and f.createdBy in :createdBy")
	List<Feed> findByStatusAndCreatedBy(@Param("status") FeedStatus status, @Param("createdBy") Collection<String> createdBy);

	@Query("select f from Feed f left join fetch f.feedImages where f.status <> :status")
	List<Feed> findFetchJoinByStatusNot(@Param("status") FeedStatus status);
}

package com.maeng0830.album.feed.repository.custom;

import com.maeng0830.album.feed.domain.Feed;
import com.maeng0830.album.feed.domain.FeedStatus;
import java.util.Collection;
import java.util.List;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface FeedRepositoryCustom {

	Page<Feed> searchByStatusAndCreatedBy(Collection<FeedStatus> status, Collection<String> createdBy, Pageable pageable);
}

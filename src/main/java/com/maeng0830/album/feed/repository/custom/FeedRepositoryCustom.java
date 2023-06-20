package com.maeng0830.album.feed.repository.custom;

import com.maeng0830.album.feed.domain.Feed;
import com.maeng0830.album.feed.domain.FeedStatus;
import java.util.Collection;
import java.util.List;

public interface FeedRepositoryCustom {

	List<Feed> searchByStatusAndCreatedBy(Collection<FeedStatus> status, Collection<String> createdBy);
}

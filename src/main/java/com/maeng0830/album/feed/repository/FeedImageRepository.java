package com.maeng0830.album.feed.repository;

import com.maeng0830.album.feed.domain.FeedImage;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.transaction.annotation.Transactional;

public interface FeedImageRepository extends JpaRepository<FeedImage, Long> {

	List<FeedImage> findByFeed_Id(Long id);

	@Transactional
	void deleteFeedImageByFeed_Id(Long id);
}

package com.maeng0830.album.comment.repository;

import com.maeng0830.album.comment.domain.Comment;
import com.maeng0830.album.comment.domain.CommentStatus;
import com.maeng0830.album.comment.repository.custom.CommentRepositoryCustom;
import java.util.Collection;
import java.util.List;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface CommentRepository extends JpaRepository<Comment, Long>, CommentRepositoryCustom {

	@EntityGraph(attributePaths = {"member", "feed", "group", "parent"})
	@Query("select c from Comment c where "
			+ "c.feed.id = :feedId and "
			+ "c.status in :status and "
			+ "c.id = c.group.id")
	List<Comment> findGroupComment(@Param("feedId") Long feedId,
								   @Param("status") Collection<CommentStatus> status,
								   Pageable pageable);

	@EntityGraph(attributePaths = {"member", "feed", "group", "parent"})
	@Query("select c from Comment c where "
			+ "c.feed.id = :feedId and "
			+ "c.status in :status and "
			+ "c.id <> c.group.id and "
			+ "c.group.id >= :min and "
			+ "c.group.id <= :max")
	List<Comment> findBasicComment(@Param("feedId") Long feedId,
								   @Param("status") Collection<CommentStatus> status,
								   @Param("min") Long min,
								   @Param("max") Long max);
}

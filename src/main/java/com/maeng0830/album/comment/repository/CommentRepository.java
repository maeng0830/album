package com.maeng0830.album.comment.repository;

import com.maeng0830.album.comment.domain.Comment;
import com.maeng0830.album.comment.domain.CommentStatus;
import java.util.List;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

public interface CommentRepository extends JpaRepository<Comment, Long> {

	@EntityGraph(attributePaths = {"member", "feed", "group", "parent"})
	@Query("select c from Comment c where c.feed.id = :feedId and c.status <> :status")
	List<Comment> findFetchJoinAll(Long feedId, CommentStatus status);

}

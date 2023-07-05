package com.maeng0830.album.comment.repository;

import com.maeng0830.album.comment.domain.CommentAccuse;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CommentAccuseRepository extends JpaRepository<CommentAccuse, Long> {

	List<CommentAccuse> findCommentAccuseByComment_Id(Long commentId);
}

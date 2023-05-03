package com.maeng0830.album.comment.repository;

import com.maeng0830.album.comment.domain.Comment;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CommentRepository extends JpaRepository<Comment, Long> {

}

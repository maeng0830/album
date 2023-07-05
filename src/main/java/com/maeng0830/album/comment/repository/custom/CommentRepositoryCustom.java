package com.maeng0830.album.comment.repository.custom;

import com.maeng0830.album.comment.domain.Comment;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface CommentRepositoryCustom {

	Page<Comment> searchBySearchText(String searchText, Pageable pageable);
}

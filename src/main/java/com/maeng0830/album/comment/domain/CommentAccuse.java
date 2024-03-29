package com.maeng0830.album.comment.domain;

import com.maeng0830.album.common.model.entity.BaseEntity;
import com.maeng0830.album.member.domain.Member;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

@Entity
@SuperBuilder
@NoArgsConstructor
@Getter
public class CommentAccuse extends BaseEntity {

	@Id @GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "comment_id")
	private Comment comment;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "member_id")
	private Member member;

	private String content;
}

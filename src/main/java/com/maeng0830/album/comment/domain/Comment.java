package com.maeng0830.album.comment.domain;

import com.maeng0830.album.common.model.entity.BaseEntity;
import com.maeng0830.album.feed.domain.Feed;
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
public class Comment extends BaseEntity {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "member_id")
	private Member member;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "feed_id")
	private Feed feed;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "group_id")
	private Comment group;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "parent_id")
	private Comment parent;

	private String content;

	private CommentStatus status;

	public void saveGroup(Comment group) {
		if (group != null) {
			this.group = group;
		} else {
			this.group = this;
		}
	}

	public void saveParent(Comment parent) {
		if (parent != null) {
			this.parent = parent;
		} else {
			this.parent = this;
		}
	}

	public void changeContent(String content) {
		this.content = content;
	}

	public void changeContentForDelete() {
		this.content = "삭제된 댓글 입니다.";
	}

	public void accuseComment() {
		this.status = CommentStatus.ACCUSE;
	}

	public void changeStatus(CommentStatus commentStatus) {
		this.status = commentStatus;
	}
}

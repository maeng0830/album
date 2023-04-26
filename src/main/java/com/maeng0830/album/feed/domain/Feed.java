package com.maeng0830.album.feed.domain;

import com.maeng0830.album.common.BaseEntity;
import com.maeng0830.album.member.domain.Member;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.Lob;
import javax.persistence.ManyToOne;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

@Entity
@SuperBuilder
@Getter
@NoArgsConstructor
public class Feed extends BaseEntity {

	@Id @GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	private String title;
	@Lob
	private String content;
	private int hits;
	private int commentCount;
	private int likeCount;
	@Enumerated(EnumType.STRING)
	private FeedStatus status;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "member_id")
	private Member member;

	public void changeStatus(FeedStatus status) {
		this.status = status;
	}
}

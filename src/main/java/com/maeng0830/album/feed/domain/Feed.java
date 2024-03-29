package com.maeng0830.album.feed.domain;

import com.maeng0830.album.common.model.entity.BaseEntity;
import com.maeng0830.album.feed.domain.FeedStatus.FeedStatusConvertor;
import com.maeng0830.album.feed.dto.request.FeedModifiedForm;
import com.maeng0830.album.feed.dto.request.FeedPostForm;
import com.maeng0830.album.member.domain.Member;
import java.util.ArrayList;
import java.util.List;
import javax.persistence.Convert;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.Lob;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import lombok.Builder;
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
	@Convert(converter = FeedStatusConvertor.class)
	private FeedStatus status;
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "member_id")
	private Member member;

	@Builder.Default
	@OneToMany(mappedBy = "feed")
	private List<FeedImage> feedImages = new ArrayList<>();

	public void changeStatus(FeedStatus status) {
		this.status = status;
	}

	public void addFeedImage(FeedImage feedImage) {
		this.feedImages.add(feedImage);
		feedImage.assignFeed(this);
	}

	public void modified(FeedModifiedForm feedModifiedForm) {
		this.title = feedModifiedForm.getTitle();
		this.content = feedModifiedForm.getContent();
	}

	public void addHits() {
		this.hits += 1;
	}

	public void addCommentCount() {
		this.commentCount += 1;
	}
}

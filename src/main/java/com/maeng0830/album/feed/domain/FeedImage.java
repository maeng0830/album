package com.maeng0830.album.feed.domain;

import com.maeng0830.album.common.model.entity.TimeEntity;
import com.maeng0830.album.common.model.image.Image;
import javax.persistence.Embedded;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

@Entity
@Getter
@SuperBuilder
@NoArgsConstructor
public class FeedImage extends TimeEntity {

	@Id @GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	@Embedded
	private Image image;

	@ManyToOne
	@JoinColumn(name = "feed_id")
	private Feed feed;
}

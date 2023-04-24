package com.maeng0830.album.feed.domain;

import com.maeng0830.album.common.TimeStamp;
import com.maeng0830.album.common.model.Image;
import javax.persistence.Embedded;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

@Entity
@SuperBuilder
@NoArgsConstructor
public class FeedImage extends TimeStamp {

	@Id @GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	@Embedded
	private Image image;

	@ManyToOne
	@JoinColumn(name = "feed_id")
	private Feed feed;
}

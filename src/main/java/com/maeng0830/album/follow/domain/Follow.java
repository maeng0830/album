package com.maeng0830.album.follow.domain;

import com.fasterxml.jackson.annotation.JsonBackReference;
import com.maeng0830.album.common.model.entity.BaseEntity;
import com.maeng0830.album.member.domain.Member;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import javax.persistence.UniqueConstraint;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

@Getter
@SuperBuilder
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(uniqueConstraints = @UniqueConstraint(name = "followUk", columnNames = {"follower_id", "following_id"}))
public class Follow extends BaseEntity {

	@Id @GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "follower_id")
	private Member follower; // 팔로우 하는 사람(본인)

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "following_id")
	private Member following; // 팔로우 당하는 사람(타인)
}

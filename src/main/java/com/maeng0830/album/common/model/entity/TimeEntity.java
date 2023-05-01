package com.maeng0830.album.common.model.entity;

import java.time.LocalDateTime;
import javax.persistence.EntityListeners;
import javax.persistence.MappedSuperclass;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;


@SuperBuilder
@NoArgsConstructor
@Getter
@EntityListeners(AuditingEntityListener.class)
@MappedSuperclass
public class TimeEntity {

	@CreatedDate
	private LocalDateTime createdAt;
	@LastModifiedDate
	private LocalDateTime modifiedAt;
}

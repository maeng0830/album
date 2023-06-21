package com.maeng0830.album.comment.dto;

import com.maeng0830.album.comment.domain.CommentAccuse;
import com.maeng0830.album.common.model.entity.TimeEntity;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.SuperBuilder;

@Getter
@Setter
@SuperBuilder
@NoArgsConstructor
public class CommentAccuseDto extends TimeEntity {

	private Long id;
	private Long commentId;
	private Long memberId;
	private String content;

	public static CommentAccuseDto from(CommentAccuse commentAccuse) {
		return CommentAccuseDto.builder()
				.id(commentAccuse.getId())
				.commentId(commentAccuse.getComment().getId())
				.memberId(commentAccuse.getMember().getId())
				.content(commentAccuse.getContent())
				.createdAt(commentAccuse.getCreatedAt())
				.modifiedAt(commentAccuse.getModifiedAt())
				.build();
	}
}

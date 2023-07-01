package com.maeng0830.album.comment.dto;

import com.maeng0830.album.comment.domain.CommentAccuse;
import com.maeng0830.album.common.model.entity.BaseEntity;
import com.maeng0830.album.member.dto.MemberDto;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.SuperBuilder;

@Getter
@Setter
@SuperBuilder
@NoArgsConstructor
public class CommentAccuseDto extends BaseEntity {

	private Long id;
	private CommentDto comment;
	private MemberDto member;
	private String content;

	public static CommentAccuseDto from(CommentAccuse commentAccuse) {
		return CommentAccuseDto.builder()
				.id(commentAccuse.getId())
				.comment(CommentDto.from(commentAccuse.getComment()))
				.member(MemberDto.from(commentAccuse.getMember()))
				.content(commentAccuse.getContent())
				.createdAt(commentAccuse.getCreatedAt())
				.modifiedAt(commentAccuse.getModifiedAt())
				.modifiedBy(commentAccuse.getModifiedBy())
				.build();
	}
}

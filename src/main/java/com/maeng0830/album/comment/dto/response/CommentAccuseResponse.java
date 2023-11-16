package com.maeng0830.album.comment.dto.response;

import com.maeng0830.album.comment.domain.CommentAccuse;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class CommentAccuseResponse {

	private Long commentId; // 신고 댓글 번호
	private String username; // 신고자 아이디
	private String nickname; // 신고자 닉네임
	private String content; // 신고 내용

	@Builder
	public CommentAccuseResponse(Long commentId, String username, String nickname, String content) {
		this.commentId = commentId;
		this.username = username;
		this.nickname = nickname;
		this.content = content;
	}

	public static CommentAccuseResponse from(CommentAccuse commentAccuse) {
		return CommentAccuseResponse.builder()
				.commentId(commentAccuse.getComment().getId())
				.username(commentAccuse.getMember().getUsername())
				.nickname(commentAccuse.getMember().getNickname())
				.content(commentAccuse.getContent())
				.build();
	}
}

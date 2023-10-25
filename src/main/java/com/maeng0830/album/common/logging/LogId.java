package com.maeng0830.album.common.logging;

import java.util.UUID;
import lombok.Getter;

@Getter
public class LogId {
	// 로그 아이디. http 클라이언트 요청 별로 동일한 값 유지
	private final String id;
	// 메소드 호출 깊이. ex) memberController.method() -> memberService.method() -> MemberRepository.method()
	private final int depth;

	public LogId() {
		this.depth = 0;
		this.id = createId();
	}

	// createNext(), createPrev()를 위한 생성자
	private LogId(String id, int depth) {
		this.id = id;
		this.depth = depth;
	}

	// 로그 아이디 생성
	private String createId() {
		return UUID.randomUUID().toString().substring(0, 8);
	}

	public LogId createNext() {
		return new LogId(id, depth + 1);
	}

	public LogId createPrev() {
		return new LogId(id, depth - 1);
	}

	public boolean isZeroDepth() {
		return depth == 0;
	}
}

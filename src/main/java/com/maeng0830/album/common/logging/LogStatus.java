package com.maeng0830.album.common.logging;

import lombok.Getter;

@Getter
public class LogStatus {

	private final LogId logId;
	private final Long startTime;
	private final String message;

	public LogStatus(LogId logId, Long startTime, String message) {
		this.logId = logId;
		this.startTime = startTime;
		this.message = message;
	}
}

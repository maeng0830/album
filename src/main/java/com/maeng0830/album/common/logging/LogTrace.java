package com.maeng0830.album.common.logging;

import lombok.extern.slf4j.Slf4j;

@Slf4j
public class LogTrace {

	private static final String START_PREFIX = "-->";
	private static final String COMPLETE_PREFIX = "<--";
	private static final String EX_PREFIX = "<X-";

	private ThreadLocal<LogId> curLogId = new ThreadLocal<>();

	public LogStatus begin(String message) {
		syncLogId();

		LogId logId = curLogId.get();
		Long startTime = System.currentTimeMillis();

		log.info("[{}] {}{}", logId.getId(), decorateLog(START_PREFIX, logId.getDepth()), message);

		return new LogStatus(logId, startTime, message);
	}

	public void end(LogStatus status) {
		complete(status, null);
	}

	public void exception(LogStatus status, Exception e) {
		complete(status, e);
	}

	private void syncLogId() {
		LogId logId = curLogId.get();

		if (logId == null) {
			curLogId.set(new LogId());
		} else {
			curLogId.set(logId.createNext());
		}
	}

	private void complete(LogStatus status, Exception e) {
		Long stopTimeMs = System.currentTimeMillis();
		long resultTimeMs = stopTimeMs - status.getStartTime();
		LogId logId = status.getLogId();

		if (e == null) {
			log.info("[{}] {}{} time={}ms", logId.getId(), decorateLog(COMPLETE_PREFIX, logId.getDepth()), status.getMessage(), resultTimeMs);
		} else {
			log.info("[{}] {}{} time={}ms ex={}", logId.getId(), decorateLog(EX_PREFIX, logId.getDepth()), status.getMessage(), resultTimeMs, e.toString());
		}

		releaseLogId();
	}

	private void releaseLogId() {
		LogId logId = curLogId.get();
		if (logId.isZeroDepth()) {
			curLogId.remove();
		} else {
			curLogId.set(logId.createPrev());
		}
	}

	private static String decorateLog(String prefix, int depth) {
		StringBuilder sb = new StringBuilder();

		for (int i = 0; i < depth; i++) {
			sb.append( (i == depth - 1) ? "|" + prefix : "|   ");
		}
		return sb.toString();
	}
}

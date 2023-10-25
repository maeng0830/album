package com.maeng0830.album.common.aop.aspect;

import com.maeng0830.album.common.logging.LogStatus;
import com.maeng0830.album.common.logging.LogTrace;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;

@Aspect
public class LoggingAspect {

	private static final String POINTCUT_PACKAGE = "com.maeng0830.album.common.aop.Pointcuts.";
	private final LogTrace logTrace;

	public LoggingAspect(LogTrace logTrace) {
		this.logTrace = logTrace;
	}

	@Around(POINTCUT_PACKAGE + "allMatch()")
	public Object doLog(ProceedingJoinPoint joinPoint) throws Throwable {
		LogStatus logStatus = null;

		try {
			String message = joinPoint.getSignature().toShortString();

			logStatus = logTrace.begin(message); // 현재 메소드 호출 로깅

			Object result = joinPoint.proceed();// 현재 메소드 호출

			logTrace.end(logStatus); // 현재 메소드 정상 종료 로깅

			return result;
		} catch (Exception e) {
			logTrace.exception(logStatus, e); // 현재 메소드 예외 종료 로깅
			throw e;
		}
	}
}

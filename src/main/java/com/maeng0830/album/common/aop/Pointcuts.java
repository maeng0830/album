package com.maeng0830.album.common.aop;

import org.aspectj.lang.annotation.Pointcut;

public class Pointcuts {

	@Pointcut("execution(public * com.maeng0830.album..*Repository*.*(..))")
	public void allRepository() {}

	@Pointcut("execution(public * com.maeng0830.album..*Service*.*(..))")
	public void allService() {}

	@Pointcut("execution(public * com.maeng0830.album..*Controller*.*(..))")
	public void allController() {}

	@Pointcut("allRepository() || allService() || allController()")
	public void allMatch() {}
}

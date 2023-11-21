package com.maeng0830.album.common.aop;

import org.aspectj.lang.annotation.Pointcut;

public class Pointcuts {

	@Pointcut("execution(public * com.maeng0830.album..*Repository*.*(..))")
	public void allRepository() {}

	@Pointcut("execution(public * com.maeng0830.album..*Service*.*(..))")
	public void allService() {}

	@Pointcut("execution(public * com.maeng0830.album..*Controller*.*(..))")
	public void allController() {}

	@Pointcut("execution(* com.maeng0830.album.security..*(..))")
	public void security() {}

	@Pointcut("(allRepository() || allService() || allController()) && !security()")
	public void allMatch() {}

	@Pointcut("@annotation(com.maeng0830.album.common.aop.annotation.MemberCheck) && allController() && !security()")
	public void memberCheck() {};

	@Pointcut("@target(com.maeng0830.album.common.aop.annotation.AdminCheck) && allController() && !security()")
	public void adminCheck() {};
}

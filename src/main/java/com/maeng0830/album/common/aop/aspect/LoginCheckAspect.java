package com.maeng0830.album.common.aop.aspect;

import static com.maeng0830.album.member.domain.MemberRole.ROLE_ADMIN;
import static com.maeng0830.album.member.exception.MemberExceptionCode.NO_AUTHORITY;
import static com.maeng0830.album.member.exception.MemberExceptionCode.REQUIRED_LOGIN;

import com.maeng0830.album.common.exception.AlbumException;
import com.maeng0830.album.security.formlogin.PrincipalDetails;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.springframework.core.annotation.Order;

@Order(2)
@Aspect
public class LoginCheckAspect {

	private static final String POINTCUT_PACKAGE = "com.maeng0830.album.common.aop.Pointcuts.";

	@Around(POINTCUT_PACKAGE + "memberCheck()")
	public Object memberCheck(ProceedingJoinPoint proceedingJoinPoint) throws Throwable {
		PrincipalDetails principalDetails = getPrincipalDetails(proceedingJoinPoint);

		// 로그인이 안된 경우 예외 발생
		if (principalDetails == null) {
			throw new AlbumException(REQUIRED_LOGIN);
		}

		return proceedingJoinPoint.proceed();
	}

	@Around(POINTCUT_PACKAGE + "adminCheck()")
	public Object adminCheck(ProceedingJoinPoint proceedingJoinPoint) throws Throwable {
		PrincipalDetails principalDetails = getPrincipalDetails(proceedingJoinPoint);

		// 로그인이 안된 경우, 관리자가 아닌 경우 예외 발생
		if (principalDetails == null) {
			throw new AlbumException(REQUIRED_LOGIN);
		} else {
			if (principalDetails.getMemberDto().getRole() != ROLE_ADMIN) {
				throw new AlbumException(NO_AUTHORITY);
			}
		}

		return proceedingJoinPoint.proceed();
	}

	private static PrincipalDetails getPrincipalDetails(ProceedingJoinPoint proceedingJoinPoint) {
		Object[] args = proceedingJoinPoint.getArgs();

		PrincipalDetails principalDetails = null;

		// PrincipalDetails 타입의 인수 조회
		for (Object arg : args) {
			if (arg != null && arg.getClass() == PrincipalDetails.class) {
				principalDetails = (PrincipalDetails) arg;
			}
		}

		return principalDetails;
	}
}

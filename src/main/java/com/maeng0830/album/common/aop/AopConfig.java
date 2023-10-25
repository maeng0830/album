package com.maeng0830.album.common.aop;

import com.maeng0830.album.common.aop.aspect.LoggingAspect;
import com.maeng0830.album.common.logging.LogTrace;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class AopConfig {

	@Bean
	public LogTrace logTrace() {
		return new LogTrace();
	}

	@Bean
	public LoggingAspect loggingAspect() {
		return new LoggingAspect(logTrace());
	}
}

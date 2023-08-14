package com.maeng0830.album.support.config;

import com.maeng0830.album.support.util.TestFileManager;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;

@TestConfiguration
public class ServiceTestConfig {
	@Bean
	public TestFileManager testFileManager() {
		return new TestFileManager();
	}
}

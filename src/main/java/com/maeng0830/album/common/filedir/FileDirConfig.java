package com.maeng0830.album.common.filedir;

import lombok.RequiredArgsConstructor;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;

@EnableConfigurationProperties(FileDirProperties.class)
@Configuration
@RequiredArgsConstructor
public class FileDirConfig {


	private final FileDirProperties fileDirProperties;

	@Profile("dev")
	@Bean(name = "fileDir")
	public FileDir fileDirDev() {

		System.out.println("fileDirProperties.getDev() = " + fileDirProperties.getDev());
		return new FileDir(fileDirProperties.getDev());
	}

	@Profile("test")
	@Bean(name = "fileDir")
	public FileDir fileDirTest() {
		System.out.println("fileDirProperties.getTest() = " + fileDirProperties.getTest());
		return new FileDir(fileDirProperties.getTest());
	}
}

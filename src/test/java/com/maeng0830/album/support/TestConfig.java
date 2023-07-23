package com.maeng0830.album.support;

import com.maeng0830.album.common.filedir.FileDir;
import com.maeng0830.album.common.filedir.FileDirProperties;
import com.maeng0830.album.common.image.DefaultImage;
import com.maeng0830.album.common.image.DefaultImageProperties;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;

@Configuration
@EnableConfigurationProperties(value = {FileDirProperties.class, DefaultImageProperties.class})
public class TestConfig {

	@Autowired
	private FileDirProperties fileDirProperties;

	@Autowired
	private DefaultImageProperties defaultImageProperties;

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

	@Bean
	public DefaultImage defaultImage() {
		return new DefaultImage(defaultImageProperties.getMember(), defaultImageProperties.getFeed());
	}
}

package com.maeng0830.album.support.config;

import com.maeng0830.album.common.filedir.FileDir;
import com.maeng0830.album.common.filedir.FileDirProperties;
import com.maeng0830.album.common.image.DefaultImage;
import com.maeng0830.album.common.image.DefaultImageProperties;
import com.maeng0830.album.support.util.TestFileManager;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.web.filter.CharacterEncodingFilter;

@EnableConfigurationProperties(value = {FileDirProperties.class, DefaultImageProperties.class})
@TestConfiguration
public class ControllerAndDocsTestConfig {

	@Autowired
	private FileDirProperties fileDirProperties;

	@Autowired
	private DefaultImageProperties defaultImageProperties;

	@Bean
	public FileDir fileDir() {
		return new FileDir(fileDirProperties.getTest());
	}

	@Bean
	public DefaultImage defaultImage() {
		return new DefaultImage(defaultImageProperties.getMember(),
				defaultImageProperties.getFeed());
	}

	@Bean
	public TestPrincipalDetailsService testPrincipalDetailsService() {
		return new TestPrincipalDetailsService();
	}

	@Bean
	public TestFileManager testFileManager() {
		return new TestFileManager();
	}
}

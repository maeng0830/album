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
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

@Configuration
@EnableConfigurationProperties(value = {FileDirProperties.class, DefaultImageProperties.class})
public class TestConfig {

	@Autowired
	private FileDirProperties fileDirProperties;

	@Autowired
	private DefaultImageProperties defaultImageProperties;

	@Bean(name = "testFileDir")
	public FileDir testFileDir() {
		return new FileDir(fileDirProperties.getTest());
	}

	@Bean(name = "testDefaultImage")
	public DefaultImage testDefaultImage() {
		return new DefaultImage(defaultImageProperties.getMember(), defaultImageProperties.getFeed());
	}

	@Bean
	public TestPrincipalDetailsService testPrincipalDetailsService() {
		return new TestPrincipalDetailsService();
	}
}

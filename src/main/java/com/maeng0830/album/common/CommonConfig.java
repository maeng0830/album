package com.maeng0830.album.common;

import com.maeng0830.album.common.filedir.FileDir;
import com.maeng0830.album.common.filedir.FileDirProperties;
import com.maeng0830.album.common.image.DefaultImage;
import com.maeng0830.album.common.image.DefaultImageProperties;
import com.maeng0830.album.common.util.AlbumUtil;
import com.querydsl.jpa.impl.JPAQueryFactory;
import java.io.File;
import javax.persistence.EntityManager;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

@EnableConfigurationProperties(value = {FileDirProperties.class, DefaultImageProperties.class})
@EnableJpaAuditing
@RequiredArgsConstructor
@Configuration
public class CommonConfig {

	private final FileDirProperties fileDirProperties;
	private final DefaultImageProperties defaultImageProperties;

	@Bean
	public BCryptPasswordEncoder bCryptPasswordEncoder() {
		return new BCryptPasswordEncoder();
	}

	@Bean
	public AlbumUtil util() {
		return new AlbumUtil();
	}

	@Bean
	public JPAQueryFactory jpaQueryFactory(EntityManager em) {
		return new JPAQueryFactory(em);
	}

	@Profile("prod")
	@Bean(name = "fileDir")
	public FileDir fileDirProd() {
		System.out.println("fileDirProperties.getProd() = " + fileDirProperties.getProd());
		return new FileDir(fileDirProperties.getProd());
	}

	@Profile("dev")
	@Bean(name = "fileDir")
	public FileDir fileDirDev() {

		String relativePath = fileDirProperties.getDev();
		String absolutePath = null;

		String os = System.getProperty("os.name").toLowerCase();

		if (os.contains("win")) {
			absolutePath = new File(relativePath).getAbsolutePath() + "\\";
		} else {
			absolutePath = new File(relativePath).getAbsolutePath() + "/";
		}

		return new FileDir(absolutePath);
	}

	@Profile("test")
	@Bean(name = "fileDir")
	public FileDir fileDirTest() {
		String relativePath = fileDirProperties.getTest();
		String absolutePath = null;

		String os = System.getProperty("os.name").toLowerCase();

		if (os.contains("win")) {
			absolutePath = new File(relativePath).getAbsolutePath() + "\\";
		} else {
			absolutePath = new File(relativePath).getAbsolutePath() + "/";
		}

		return new FileDir(absolutePath);
	}

	@Bean
	public DefaultImage defaultImage() {
		return new DefaultImage(defaultImageProperties.getMember(),
				defaultImageProperties.getFeed());
	}


}

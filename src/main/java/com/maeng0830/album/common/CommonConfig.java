package com.maeng0830.album.common;

import com.amazonaws.services.s3.AmazonS3Client;
import com.maeng0830.album.common.aws.AwsS3Manager;
import com.maeng0830.album.common.filedir.FileDir;
import com.maeng0830.album.common.filedir.FileDirProperties;
import com.maeng0830.album.common.image.DefaultImage;
import com.maeng0830.album.common.image.DefaultImageProperties;
import com.maeng0830.album.common.util.AlbumUtil;
import com.querydsl.jpa.impl.JPAQueryFactory;
import javax.persistence.EntityManager;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.connection.RedisStandaloneConfiguration;
import org.springframework.data.redis.connection.lettuce.LettuceConnectionFactory;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.serializer.StringRedisSerializer;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.session.data.redis.config.annotation.web.http.EnableRedisHttpSession;

@EnableConfigurationProperties(value = {FileDirProperties.class, DefaultImageProperties.class})
@EnableRedisHttpSession(maxInactiveIntervalInSeconds = 300)
@EnableJpaAuditing
@RequiredArgsConstructor
@Configuration
public class CommonConfig {

	@Value("${spring.redis.host}")
	private String redisHost;
	@Value("${spring.redis.port}")
	private String redisPort;
	@Value("${spring.redis.password}")
	private String redisPassword;

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

	@Bean
	public RedisConnectionFactory redisConnectionFactory() {
		RedisStandaloneConfiguration redisStandaloneConfiguration = new RedisStandaloneConfiguration(
				redisHost, Integer.parseInt(redisPort));

		if (redisPassword != null) {
			redisStandaloneConfiguration.setPassword(redisPassword);
		}

		return new LettuceConnectionFactory(redisStandaloneConfiguration);
	}

	@Bean
	public RedisTemplate<?, ?> redisTemplate() {
		RedisTemplate<?, ?> redisTemplate = new RedisTemplate<>();

		redisTemplate.setConnectionFactory(redisConnectionFactory());
		redisTemplate.setKeySerializer(new StringRedisSerializer());
		redisTemplate.setValueSerializer(new StringRedisSerializer());

		return redisTemplate;
	}
}

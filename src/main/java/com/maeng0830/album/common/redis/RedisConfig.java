package com.maeng0830.album.common.redis;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.ObjectMapper.DefaultTyping;
import com.fasterxml.jackson.databind.jsontype.BasicPolymorphicTypeValidator;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import java.time.Duration;
import java.util.HashMap;
import java.util.Map;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.cache.CacheKeyPrefix;
import org.springframework.data.redis.cache.RedisCacheConfiguration;
import org.springframework.data.redis.cache.RedisCacheManager;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.connection.RedisStandaloneConfiguration;
import org.springframework.data.redis.connection.lettuce.LettuceConnectionFactory;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.serializer.GenericJackson2JsonRedisSerializer;
import org.springframework.data.redis.serializer.RedisSerializationContext;
import org.springframework.data.redis.serializer.RedisSerializationContext.SerializationPair;
import org.springframework.data.redis.serializer.StringRedisSerializer;
import org.springframework.session.data.redis.config.ConfigureRedisAction;
import org.springframework.session.data.redis.config.annotation.web.http.EnableRedisHttpSession;

@EnableCaching // 캐시 기능 활성화
@EnableRedisHttpSession(maxInactiveIntervalInSeconds = 300)
@Configuration
public class RedisConfig {
	@Value("${spring.redis.host}")
	private String redisHost;
	@Value("${spring.redis.port}")
	private String redisPort;

	private ObjectMapper redisObjectMapper() {
		// local date time 역직렬화 위한 추가 코드
		ObjectMapper objectMapper = new ObjectMapper();
		objectMapper.registerModule(new JavaTimeModule());

		// LinkedHasmap cannot be cast to class DTO Object 에러 해결을 위한 코드
		BasicPolymorphicTypeValidator polymorphicTypeValidator = BasicPolymorphicTypeValidator.builder()
				.allowIfSubType(Object.class)
				.build();
		objectMapper.activateDefaultTyping(polymorphicTypeValidator, DefaultTyping.NON_FINAL);

		return objectMapper;
	}

	private RedisCacheConfiguration redisCacheDefaultConfig() {
		// Redis Cache 기본 설정
		return RedisCacheConfiguration.defaultCacheConfig()
				.computePrefixWith(CacheKeyPrefix.simple())
				.disableCachingNullValues()
				.entryTtl(Duration.ofSeconds(300))
				.serializeKeysWith(SerializationPair.fromSerializer(new StringRedisSerializer()))
				.serializeValuesWith(
						SerializationPair.fromSerializer(new GenericJackson2JsonRedisSerializer(redisObjectMapper())));
	}

	private Map<String, RedisCacheConfiguration> redisCacheConfigMap() {
		// cacheName에 따른 Redis Cache 설정
		Map<String, RedisCacheConfiguration> redisCacheConfigMap = new HashMap<>();
		redisCacheConfigMap.put("feed", redisCacheDefaultConfig().entryTtl(Duration.ofSeconds(60)));

		return redisCacheConfigMap;
	}

	@Bean
	public RedisConnectionFactory redisConnectionFactory() {
		RedisStandaloneConfiguration redisStandaloneConfiguration = new RedisStandaloneConfiguration(
				redisHost, Integer.parseInt(redisPort));

		return new LettuceConnectionFactory(redisStandaloneConfiguration);
	}

	@Bean
	public RedisTemplate<?, ?> redisTemplate() {
		RedisTemplate<?, ?> redisTemplate = new RedisTemplate<>();

		redisTemplate.setConnectionFactory(redisConnectionFactory());
		redisTemplate.setKeySerializer(new StringRedisSerializer());
		redisTemplate.setValueSerializer(new GenericJackson2JsonRedisSerializer(redisObjectMapper()));

		return redisTemplate;
	}

	@Bean
	public RedisCacheManager redisCacheManager() {
		return RedisCacheManager.RedisCacheManagerBuilder
				.fromConnectionFactory(redisConnectionFactory())
				.cacheDefaults(redisCacheDefaultConfig()) // 기본 캐시 설정
				.withInitialCacheConfigurations(redisCacheConfigMap()) // cacheName에 따른 캐시 설정
				.build();
	}

	@Bean
	public ConfigureRedisAction configureRedisAction() {
		return ConfigureRedisAction.NO_OP;
	}
}

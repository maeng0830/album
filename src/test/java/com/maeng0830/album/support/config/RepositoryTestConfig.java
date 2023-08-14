package com.maeng0830.album.support.config;

import com.maeng0830.album.comment.repository.custom.CommentRepositoryImpl;
import com.maeng0830.album.common.filedir.FileDir;
import com.maeng0830.album.common.filedir.FileDirProperties;
import com.maeng0830.album.common.image.DefaultImage;
import com.maeng0830.album.common.image.DefaultImageProperties;
import com.maeng0830.album.common.util.AlbumUtil;
import com.maeng0830.album.feed.repository.custom.FeedRepositoryImpl;
import com.maeng0830.album.follow.repository.custom.FollowRepositoryImpl;
import com.maeng0830.album.member.repository.custom.MemberRepositoryImpl;
import com.querydsl.jpa.impl.JPAQueryFactory;
import javax.persistence.EntityManager;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;

@EnableConfigurationProperties(value = {FileDirProperties.class, DefaultImageProperties.class})
@TestConfiguration
public class RepositoryTestConfig {

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
	public JPAQueryFactory jpaQueryFactory(EntityManager em) {
		return new JPAQueryFactory(em);
	}

	@Bean
	public AlbumUtil util() {
		return new AlbumUtil();
	}

	@Bean
	public MemberRepositoryImpl MemberRepositoryImpl(EntityManager em) {
		return new MemberRepositoryImpl(jpaQueryFactory(em), util());
	}

	@Bean
	public FollowRepositoryImpl followRepositoryImpl(EntityManager em) {
		return new FollowRepositoryImpl(jpaQueryFactory(em));
	}

	@Bean
	public FeedRepositoryImpl feedRepositoryImpl(EntityManager em) {
		return new FeedRepositoryImpl(jpaQueryFactory(em), util());
	}

	@Bean
	public CommentRepositoryImpl commentRepositoryImpl(EntityManager em) {
		return new CommentRepositoryImpl(jpaQueryFactory(em), util());
	}
}

package com.maeng0830.album;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.PropertySource;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;

@PropertySource("classpath:/env.properties")
@EnableJpaAuditing
@SpringBootApplication
public class AlbumApplication {

	public static void main(String[] args) {
		SpringApplication.run(AlbumApplication.class, args);
	}

}

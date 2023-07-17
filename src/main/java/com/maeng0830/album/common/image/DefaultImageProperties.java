package com.maeng0830.album.common.image;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;

@Getter
@Setter
@ConfigurationProperties("default.image")
public class DefaultImageProperties {

	private String member;
	private String feed;
}

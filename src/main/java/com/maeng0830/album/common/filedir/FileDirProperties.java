package com.maeng0830.album.common.filedir;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;

@Getter
@Setter
@ConfigurationProperties("file.dir")
public class FileDirProperties {

	private String prod;
	private String dev;
	private String test;
}

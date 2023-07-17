package com.maeng0830.album.common.controller;

import com.maeng0830.album.common.filedir.FileDir;
import java.net.MalformedURLException;
import lombok.RequiredArgsConstructor;
import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
public class CommonController {

	private final FileDir fileDir;

	@GetMapping("/images/{storeFileName}")
	public Resource getImage(@PathVariable String storeFileName) throws MalformedURLException {
		return new UrlResource("file:" + fileDir.getDir() + storeFileName);
	}
}

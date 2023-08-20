package com.maeng0830.album.common.service;

import com.maeng0830.album.common.filedir.FileDir;
import java.net.MalformedURLException;
import lombok.RequiredArgsConstructor;
import org.springframework.core.env.Environment;
import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;
import org.springframework.stereotype.Service;

@RequiredArgsConstructor
@Service
public class CommonService {

	private final Environment environment;
	private final FileDir fileDir;

	public Resource getImageUrl(String storeFileName) throws MalformedURLException {

		String[] profiles = environment.getActiveProfiles();

		for (String profile : profiles) {
			if (profile.equals("prod")) {
				return new UrlResource(fileDir.getDir() + storeFileName);
			}
		}

		return new UrlResource("file:" + fileDir.getDir() + storeFileName);
	}
}

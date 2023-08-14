package com.maeng0830.album.support.util;

import java.io.File;

public class TestFileManager {

	private static final String testFileDir = "C:/album/src/test/resources/file/";

	public void deleteTestFile() {
		File folder = new File(testFileDir);

		if (folder.isDirectory() && folder.exists()) {
			File[] files = folder.listFiles();

			for (File f : files) {
				if (!f.getName().equals("testImage.PNG") && !f.getName().equals("prevTestImage.PNG")) {
					f.delete();
				}
			}
		}
	}
}

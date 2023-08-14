package com.maeng0830.album.support;

import com.maeng0830.album.support.config.RepositoryTestConfig;
import com.maeng0830.album.support.util.TestFileManager;
import org.junit.jupiter.api.AfterEach;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.ActiveProfiles;

@Import(RepositoryTestConfig.class)
@ActiveProfiles("test")
@DataJpaTest
public abstract class RepositoryTestSupport {

	@Autowired
	protected TestFileManager testFileManager;

	@AfterEach
	void cleanUp() {
		testFileManager.deleteTestFile();
	}
}

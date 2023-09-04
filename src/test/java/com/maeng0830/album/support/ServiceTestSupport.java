package com.maeng0830.album.support;

import com.maeng0830.album.common.aws.AwsS3Manager;
import com.maeng0830.album.support.config.ServiceTestConfig;
import com.maeng0830.album.support.util.TestFileManager;
import org.junit.jupiter.api.AfterEach;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.annotation.Transactional;

@Import(ServiceTestConfig.class)
@ActiveProfiles("test")
@Transactional
@SpringBootTest
public abstract class ServiceTestSupport {

	@MockBean
	protected AwsS3Manager awsS3Manager;

	@Autowired
	protected TestFileManager testFileManager;

	protected String testOauth2Password = "123456789";

	@AfterEach
	void cleanUp() {
		testFileManager.deleteTestFile();
	}
}

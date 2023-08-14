package com.maeng0830.album.support;

import com.maeng0830.album.common.aws.AwsS3Manager;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.annotation.Transactional;

@ActiveProfiles("test")
@Transactional
@SpringBootTest
public abstract class ServiceTestSupport {

	@MockBean
	protected AwsS3Manager awsS3Manager;
}

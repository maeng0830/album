package com.maeng0830.album.common.aws;

import com.amazonaws.auth.AWSStaticCredentialsProvider;
import com.amazonaws.auth.BasicAWSCredentials;
import com.amazonaws.services.s3.AmazonS3Client;
import com.amazonaws.services.s3.AmazonS3ClientBuilder;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class AwsS3Config {

	@Value("${cloud.aws.credentials.access-key}")
	private String awsAccessKey;
	@Value("${cloud.aws.credentials.secret-key}")
	private String awsSecretKey;
	@Value("${cloud.aws.region.static}")
	private String region;
	@Value("${cloud.aws.s3.bucket}")
	private String s3Bucket;

	@Bean
	public AmazonS3Client amazonS3Client() {
		BasicAWSCredentials basicAWSCredentials = new BasicAWSCredentials(awsAccessKey,
				awsSecretKey);

		AmazonS3Client amazonS3Client = (AmazonS3Client) AmazonS3ClientBuilder.standard()
				.withRegion(region)
				.withCredentials(new AWSStaticCredentialsProvider(basicAWSCredentials))
				.build();
		
		return amazonS3Client;
	}
	
	@Bean
	public AwsS3Manager awsS3Manager() {
		return new AwsS3Manager(amazonS3Client(), s3Bucket);
	}
}

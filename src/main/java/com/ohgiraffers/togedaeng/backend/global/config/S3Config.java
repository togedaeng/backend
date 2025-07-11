package com.ohgiraffers.togedaeng.backend.global.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import com.amazonaws.auth.AWSStaticCredentialsProvider;
import com.amazonaws.auth.BasicAWSCredentials;
import com.amazonaws.auth.DefaultAWSCredentialsProviderChain;
import com.amazonaws.regions.Regions;
import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.AmazonS3ClientBuilder;

@Configuration
public class S3Config {

	@Value("${cloud.aws.region.static:ap-northeast-2}")
	private String region;

	@Value("${cloud.aws.credentials.access-key:}")
	private String accessKey;

	@Value("${cloud.aws.credentials.secret-key:}")
	private String secretKey;

	@Bean
	public AmazonS3 amazonS3() {
		// application.yml에 credentials가 설정되어 있다면 사용, 없다면 기본 체인 사용
		if (!accessKey.isEmpty() && !secretKey.isEmpty()) {
			BasicAWSCredentials credentials = new BasicAWSCredentials(accessKey, secretKey);
			return AmazonS3ClientBuilder.standard()
				.withRegion(Regions.fromName(region))
				.withCredentials(new AWSStaticCredentialsProvider(credentials))
				.build();
		} else {
			// EC2 환경변수나 IAM Role, AWS CLI 설정을 자동으로 사용
			return AmazonS3ClientBuilder.standard()
				.withRegion(Regions.fromName(region))
				.withCredentials(DefaultAWSCredentialsProviderChain.getInstance())
				.build();
		}
	}
}

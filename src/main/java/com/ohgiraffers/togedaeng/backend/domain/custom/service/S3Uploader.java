package com.ohgiraffers.togedaeng.backend.domain.custom.service;

import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.util.UUID;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.model.ObjectMetadata;

import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Service
public class S3Uploader {

	private final Logger log = LoggerFactory.getLogger(this.getClass());
	private final AmazonS3 amazonS3;

	@Value("${cloud.aws.s3.bucket}")
	private String bucket;

	public String upload(MultipartFile file, String dirName) throws IOException {
		String fileName = dirName + "/" + UUID.randomUUID() + "_" + file.getOriginalFilename();
		ObjectMetadata metadata = new ObjectMetadata();
		metadata.setContentType(file.getContentType());
		metadata.setContentLength(file.getSize());

		amazonS3.putObject(bucket, fileName, file.getInputStream(), metadata);
		return amazonS3.getUrl(bucket, fileName).toString();
	}

	public void delete(String fileUrl) {
		if (fileUrl == null || fileUrl.isEmpty()) {
			return;
		}
		try {
			// URL에서 파일 키(경로+이름)를 추출합니다.
			String key = new URL(fileUrl).getPath().substring(1);
			amazonS3.deleteObject(bucket, key);
		} catch (Exception e) {
			log.warn("S3 파일 삭제 실패. URL: {}", fileUrl, e);
		}
	}

}


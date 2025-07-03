// package com.ohgiraffers.togedaeng.backend.domain.dog.service;
//
// import java.io.IOException;
// import java.io.InputStream;
// import java.net.URL;
// import java.util.UUID;
//
// import org.springframework.beans.factory.annotation.Value;
// import org.springframework.stereotype.Service;
// import org.springframework.web.multipart.MultipartFile;
//
// import com.amazonaws.services.s3.AmazonS3;
// import com.amazonaws.services.s3.model.ObjectMetadata;
//
// import lombok.RequiredArgsConstructor;
//
// @RequiredArgsConstructor
// @Service
// public class S3Uploader {
//
// 	private final AmazonS3 amazonS3;
//
// 	@Value("${cloud.aws.s3.bucket}")
// 	private String bucket;
//
// 	public String upload(MultipartFile file, String dirName) throws IOException {
// 		String fileName = dirName + "/" + UUID.randomUUID() + "_" + file.getOriginalFilename();
// 		ObjectMetadata metadata = new ObjectMetadata();
// 		metadata.setContentType(file.getContentType());
// 		metadata.setContentLength(file.getSize());
//
// 		amazonS3.putObject(bucket, fileName, file.getInputStream(), metadata);
// 		return amazonS3.getUrl(bucket, fileName).toString();
// 	}
//
// 	public String uploadFromUrl(String imageUrl, String folderName) {
// 		try (InputStream in = new URL(imageUrl).openStream()) {
// 			String fileName = UUID.randomUUID() + ".png";
// 			String filePath = folderName + "/" + fileName;
//
// 			ObjectMetadata metadata = new ObjectMetadata();
// 			metadata.setContentType("image/png");
// 			amazonS3.putObject(bucket, filePath, in, metadata);
//
// 			return amazonS3.getUrl(bucket, filePath).toString();
// 		} catch (IOException e) {
// 			throw new RuntimeException("S3 업로드 실패", e);
// 		}
// 	}
//
// }
//

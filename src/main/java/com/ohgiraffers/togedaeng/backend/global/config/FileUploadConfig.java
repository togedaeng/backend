package com.ohgiraffers.togedaeng.backend.global.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
public class FileUploadConfig implements WebMvcConfigurer {

  @Value("${file.upload.path}")
  private String uploadPath;

  @Override
  public void addResourceHandlers(ResourceHandlerRegistry registry) {
    // 업로드된 파일들을 /files/** 경로로 접근 가능하도록 설정
    registry.addResourceHandler("/files/**")
        .addResourceLocations("file:" + uploadPath + "/");
  }
}

package com.ohgiraffers.togedaeng.backend.global.config;

import com.google.auth.oauth2.GoogleCredentials;
import com.google.firebase.FirebaseApp;
import com.google.firebase.FirebaseOptions;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Configuration;

import jakarta.annotation.PostConstruct;
import java.io.IOException;
import java.io.InputStream;

@Configuration
@Slf4j
public class FirebaseConfig {

    @PostConstruct
    public void initialize() {
        try {
            if (FirebaseApp.getApps().isEmpty()) {
                log.info("Firebase 초기화 시작");
                
                // 클래스패스에서 JSON 파일 읽기
                InputStream serviceAccount = getClass().getResourceAsStream("/firebase-service-account.json");
                
                if (serviceAccount == null) {
                    log.error("Firebase 서비스 계정 JSON 파일을 찾을 수 없습니다.");
                    throw new RuntimeException("Firebase 서비스 계정 JSON 파일이 없습니다.");
                }
                
                FirebaseOptions options = FirebaseOptions.builder()
                    .setCredentials(GoogleCredentials.fromStream(serviceAccount))
                    .build();
                
                FirebaseApp.initializeApp(options);
                log.info("Firebase 초기화 완료");
            }
        } catch (IOException e) {
            log.error("Firebase 초기화 실패", e);
            throw new RuntimeException("Firebase 초기화에 실패했습니다.", e);
        }
    }
}

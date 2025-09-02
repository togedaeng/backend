package com.ohgiraffers.togedaeng.backend.global.config;

import com.google.auth.oauth2.GoogleCredentials;
import com.google.firebase.FirebaseApp;
import com.google.firebase.FirebaseOptions;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.ClassPathResource;

import jakarta.annotation.PostConstruct;
import java.io.FileInputStream;
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

                // 1. FIREBASE_KEY_PATH 환경 변수를 확인합니다.
                String firebaseKeyPath = System.getenv("FIREBASE_KEY_PATH");
                InputStream serviceAccount;

                if (firebaseKeyPath != null && !firebaseKeyPath.isEmpty()) {
                    // 2. 환경 변수가 있으면 (Render 배포 환경), 해당 파일 경로에서 읽어옵니다.
                    log.info("환경 변수 FIREBASE_KEY_PATH 에서 서비스 계정 파일을 로드합니다: " + firebaseKeyPath);
                    serviceAccount = new FileInputStream(firebaseKeyPath);
                } else {
                    // 3. 환경 변수가 없으면 (로컬 개발 환경), 기존처럼 classpath에서 읽어옵니다.
                    log.info("classpath:firebase-service-account.json 에서 서비스 계정 파일을 로드합니다.");
                    ClassPathResource resource = new ClassPathResource("firebase-service-account.json");
                    if (!resource.exists()) {
                        log.error("classpath에서 firebase-service-account.json 파일을 찾을 수 없습니다.");
                        throw new IOException("classpath:firebase-service-account.json not found");
                    }
                    serviceAccount = resource.getInputStream();
                }

                FirebaseOptions options = FirebaseOptions.builder()
                    .setCredentials(GoogleCredentials.fromStream(serviceAccount))
                    .build();

                FirebaseApp.initializeApp(options);
                log.info("Firebase 초기화 완료");
            }
        } catch (IOException e) {
            log.error("Firebase 초기화 실패: 서비스 계정 파일을 찾을 수 없거나 읽는 중 오류 발생", e);
            throw new RuntimeException("Firebase 초기화에 실패했습니다.", e);
        }
    }
}


package com.ohgiraffers.togedaeng.backend.global.config;

import com.google.auth.oauth2.GoogleCredentials;
import com.google.firebase.FirebaseApp;
import com.google.firebase.FirebaseOptions;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Configuration;

import jakarta.annotation.PostConstruct;
import java.io.FileInputStream; // FileInputStream을 사용하기 위해 import 합니다.
import java.io.IOException;

@Configuration
@Slf4j
public class FirebaseConfig {

    @PostConstruct
    public void initialize() {
        try {
            if (FirebaseApp.getApps().isEmpty()) {
                log.info("Firebase 초기화 시작");

                // --- 수정된 부분 ---
                // Docker 컨테이너 내부의 파일 시스템 경로를 직접 지정합니다.
                // entrypoint.sh 스크립트가 파일을 이 경로로 옮겨줍니다.
                String serviceAccountPath = "/app/firebase-service-account.json";

                FileInputStream serviceAccount = new FileInputStream(serviceAccountPath);

                // --- 여기까지 수정 ---

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

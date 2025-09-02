package com.ohgiraffers.togedaeng.backend.domain.user.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

import com.ohgiraffers.togedaeng.backend.domain.user.model.entity.FcmToken;

public interface FcmTokenRepository extends JpaRepository<FcmToken, Long>{
    // 토큰으로 조회 (중복 체크)
    List<FcmToken> findByUserId(Long userId);

    // 사용자별 토큰 조회 (푸시 알림 전송)
    Optional<FcmToken> findByFcmToken(String fcmToken);

    // 토큰 삭제
    void deleteByFcmToken(String fcmToken);
    
}

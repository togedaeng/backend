package com.ohgiraffers.togedaeng.backend.domain.personality.repository;

import com.ohgiraffers.togedaeng.backend.domain.personality.entity.PersonalityPushMessage;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface PersonalityPushMessageRepository extends JpaRepository<PersonalityPushMessage, Long> {
    
    // 컨텍스트 키로 활성 메시지들 조회
    @Query("SELECT p FROM PersonalityPushMessage p WHERE p.contextKey = :contextKey AND p.isActive = true")
    List<PersonalityPushMessage> findByContextKeyAndIsActiveTrue(@Param("contextKey") String contextKey);
    
    // 강아지 성격 조합 ID와 컨텍스트 키로 특정 메시지 조회 (하나만 나옴)
    @Query("SELECT p FROM PersonalityPushMessage p WHERE p.personalityComboId = :personalityComboId AND p.contextKey = :contextKey AND p.variantNo = 1 AND p.isActive = true")
    Optional<PersonalityPushMessage> findByPersonalityComboIdAndContextKeyAndVariantNoOneAndIsActiveTrue(
        @Param("personalityComboId") Long personalityComboId, 
        @Param("contextKey") String contextKey
    );
}

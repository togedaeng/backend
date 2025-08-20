package com.ohgiraffers.togedaeng.backend.domain.personality.entity;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

@Entity
@Table(
        name = "personality_push_messages",
        indexes = {
                @Index(name = "idx_combo_context", columnList = "personalityComboId, contextKey")
        }
)
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PersonalityPushMessage {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // 성격 조합 ID (FK, 단순 값으로 보관)
    @Column(name = "personality_combo_id", nullable = false)
    private Long personalityComboId;

    // 상황 키 (예: MISS_OWNER, HUNGER, WALK)
    @Column(name = "context_key", nullable = false, length = 40)
    private String contextKey;

    // 버전 번호 (MVP에서는 항상 1)
    @Column(name = "variant_no", nullable = false)
    private Integer variantNo = 1;

    // 메시지 본문 템플릿
    @Column(name = "body_template", nullable = false, columnDefinition = "TEXT")
    private String bodyTemplate;

    // 프롬프트 버전
    @Column(name = "prompt_version", nullable = false)
    private Integer promptVersion = 1;

    // 활성 여부 (boolean 매핑 → DB tinyint)
    @Column(name = "is_active", nullable = false)
    private Boolean isActive = true;

    // 생성 시간
    @Column(name = "created_at", nullable = false, updatable = false,
            columnDefinition = "TIMESTAMP DEFAULT CURRENT_TIMESTAMP")
    private LocalDateTime createdAt;
}

package com.ohgiraffers.togedaeng.backend.domain.user.service;

import java.time.LocalTime;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.Optional;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

// Firebase 관련 import 추가
import com.google.firebase.messaging.FirebaseMessaging;
import com.google.firebase.messaging.FirebaseMessagingException;
import com.google.firebase.messaging.Message;
import com.google.firebase.messaging.Notification;
import com.google.firebase.messaging.MessagingErrorCode;

import com.ohgiraffers.togedaeng.backend.domain.user.model.dto.FcmTokenRequestDto;
import com.ohgiraffers.togedaeng.backend.domain.user.model.dto.FcmTokenResponseDto;
import com.ohgiraffers.togedaeng.backend.domain.user.model.entity.FcmToken;
import com.ohgiraffers.togedaeng.backend.domain.user.model.entity.User;
import com.ohgiraffers.togedaeng.backend.domain.user.model.entity.UserStatus;
import com.ohgiraffers.togedaeng.backend.domain.user.repository.FcmTokenRepository;
import com.ohgiraffers.togedaeng.backend.domain.user.repository.UserRepository;
import com.ohgiraffers.togedaeng.backend.domain.dog.entity.Dog;
import com.ohgiraffers.togedaeng.backend.domain.dog.repository.DogRepository;
import com.ohgiraffers.togedaeng.backend.domain.personality.entity.PersonalityCombination;
import com.ohgiraffers.togedaeng.backend.domain.personality.entity.PersonalityPushMessage;
import com.ohgiraffers.togedaeng.backend.domain.personality.repository.PersonalityCombinationRepository;
import com.ohgiraffers.togedaeng.backend.domain.personality.repository.PersonalityPushMessageRepository;
import com.ohgiraffers.togedaeng.backend.domain.dog.entity.DogOwner;
import com.ohgiraffers.togedaeng.backend.domain.dog.repository.DogOwnerRepository;

@Service
public class FcmService {

    Logger log = LoggerFactory.getLogger(FcmService.class);

    private final FcmTokenRepository fcmTokenRepository;
    private final UserRepository userRepository;
    private final DogRepository dogRepository;
    private final DogOwnerRepository dogOwnerRepository;
    private final PersonalityCombinationRepository personalityCombinationRepository;
    private final PersonalityPushMessageRepository personalityPushMessageRepository;

    public FcmService(FcmTokenRepository fcmTokenRepository, UserRepository userRepository, DogRepository dogRepository, DogOwnerRepository dogOwnerRepository, PersonalityCombinationRepository personalityCombinationRepository, PersonalityPushMessageRepository personalityPushMessageRepository) {
        this.fcmTokenRepository = fcmTokenRepository;
        this.userRepository = userRepository;
        this.dogRepository = dogRepository;
        this.dogOwnerRepository = dogOwnerRepository;
        this.personalityCombinationRepository = personalityCombinationRepository;
        this.personalityPushMessageRepository = personalityPushMessageRepository;
    }

    /**
     * 푸시 알림 토큰 등록
     * @param userId 사용자 ID
     * @param requestDto 토큰 요청 DTO
     * @return 등록 결과 DTO
     */
    public FcmTokenResponseDto registerFcmToken(Long userId, FcmTokenRequestDto requestDto) {

        // 1. 사용자 조회
        User user = userRepository.findById(userId)
            .orElseThrow(() -> new IllegalArgumentException("사용자를 찾을 수 없습니다."));

        // 2. 토큰 중복 체크
        if (fcmTokenRepository.findByFcmToken(requestDto.getFcmToken()).isPresent()) {
            throw new IllegalArgumentException("이미 등록된 토큰입니다.");
        }

        // 3. 토큰 등록
        FcmToken fcmToken = FcmToken.builder()
            .user(user)
            .fcmToken(requestDto.getFcmToken())
            .build();

        fcmTokenRepository.save(fcmToken);

        return new FcmTokenResponseDto(fcmToken.getId(), "토큰 등록 성공", fcmToken.getFcmToken());
    }

    /**
     * 푸시 알림 토큰 삭제
     * @param fcmToken 삭제할 토큰
     */
    public void deleteFcmToken(String fcmToken) {
        // 1. 토큰 조회
        FcmToken fcmTokenEntity = fcmTokenRepository.findByFcmToken(fcmToken)
            .orElseThrow(() -> new IllegalArgumentException("등록되지 않은 토큰입니다."));

        // 2. 토큰 삭제
        fcmTokenRepository.delete(fcmTokenEntity);
    }

    /**
     * 강아지별 개인화 메세지 전송
     * @param userId 사용자 ID
     * @param dog 강아지
     * @param contextKey 알림 컨텍스트 키
     */
    private void sendPersonalizedDogMessage(Long userId, Dog dog, String contextKey) {
        // 1. 강아지 성격 조합 조회
        PersonalityCombination combination = personalityCombinationRepository.findById(dog.getPersonalityComboId())
            .orElseThrow(() -> new IllegalArgumentException("강아지 성격 조합을 찾을 수 없습니다."));

        // 2. 강아지 주인 정보 조회 (call_name을 위해)
        List<DogOwner> dogOwners = dogOwnerRepository.findOwnerIdByDogId(dog.getId());
        if (dogOwners.isEmpty()) {
            throw new IllegalArgumentException("강아지 주인 정보를 찾을 수 없습니다.");
        }

        // 3. 첫번째 주인 정보만 사용 (비즈니스 로직에 따라 수정 필요)
        DogOwner dogOwner = dogOwners.get(0);

        // 4. 해당 컨텍스트의 메시지 템플릿 조회 (Variant_no가 1인 것만)
        PersonalityPushMessage message = personalityPushMessageRepository.findByPersonalityComboIdAndContextKeyAndVariantNoOneAndIsActiveTrue(combination.getId(), contextKey)
            .orElseThrow(() -> new IllegalArgumentException(
                "강아지 성격 조합 " + combination.getId() + "과 컨텍스트 " + contextKey + "에 맞는 메시지를 찾을 수 없습니다."
            ));
    
        // 5. 템플릿에 강아지 정보와 주인 호칭 넣기
        String personalString = message.getBodyTemplate()
            .replace("{{dog_name}}", dog.getName())
            .replace("{{call_name}}", dogOwner.getName());

        // 6. 푸시 알림 전송
        sendPushNotificationToUser(userId,
            dog.getName() + "의 메시지",  // 문자열 연결 수정
            personalString,
            createDogMessageData(dog));
    }

    /**
     * Unity에서 사용할 추가 데이터 생성
     * @param dog 강아지
     * @return 메시지 데이터
     */
    private Map<String, String> createDogMessageData(Dog dog) {
        return Map.of(
            "type", "personality",                    // 메시지 타입 구분
            "dogId", String.valueOf(dog.getId()),     // 강아지 ID
            "dogName", dog.getName(),                 // 강아지 이름
            "timestamp", String.valueOf(System.currentTimeMillis())  // 메시지 생성 시간
        );
    }

    /**
     * 실제 Firebase에 푸시 알림 전송
     * @param userId 사용자 ID
     * @param title 알림 제목
     * @param body 알림 본문
     * @param data 추가 데이터
     */
    private void sendPushNotificationToUser(Long userId, String title, String body, Map<String, String> data) {
        // 1. 사용자의 FCM 토큰들 조회
        List<FcmToken> tokens = fcmTokenRepository.findByUserId(userId);
        
        if (tokens.isEmpty()) {
            log.warn("사용자 {}의 FCM 토큰이 없습니다.", userId);
            return;
        }
        
        // 2. 각 토큰에 대해 메시지 전송
        for (FcmToken token : tokens) {
            try {
                // 3. Firebase 메시지 생성
                Message message = Message.builder()
                    .setToken(token.getFcmToken())
                    .setNotification(Notification.builder()
                        .setTitle(title)
                        .setBody(body)
                        .build())
                    .putAllData(data)
                    .build();
            
                // 4. Firebase에 전송
                String response = FirebaseMessaging.getInstance().send(message);
                log.info("FCM 알림 전송 성공 - userId: {}, response: {}", userId, response);
                
            } catch (FirebaseMessagingException e) {
                log.error("푸시 알림 전송 실패 - userId: {}, token: {}, error: {}",
                    userId, token.getFcmToken(), e.getMessage());

                // 5. 유효하지 않은 토큰 처리
                if (e.getMessagingErrorCode() == MessagingErrorCode.UNREGISTERED) {
                    fcmTokenRepository.deleteByFcmToken(token.getFcmToken());  // fcmToken → token으로 수정
                    log.info("유효하지 않은 토큰 삭제 - userId: {}, token: {}", userId, token.getFcmToken());  // fcmToken → token으로 수정
                }
            }
        }
    }

    /**
     * 모든 사용자에게 개인화된 메시지 전송
     * @param contextKey 알림 컨텍스트 키
     */
    public void sendNotificationToAllUsers(String contextKey) {
        log.info("전체 사용자 대상 푸시 알림 시작 - contextKey: {}", contextKey);
        
        try {
            // 1. 모든 활성 사용자 조회
            List<User> activeUsers = userRepository.findByStatus(UserStatus.ACTIVE);
            
            if (activeUsers.isEmpty()) {
                log.info("활성 사용자가 없습니다.");
                return;
            }
            
            // 2. 각 사용자별 처리
            for (User user : activeUsers) {
                try {
                    // 3. 사용자의 강아지들 조회
                    List<Dog> userDogs = dogRepository.findByUserId(user.getId());
                    
                    // 4. 각 강아지별 개인화된 메시지 전송
                    for (Dog dog : userDogs) {
                        sendPersonalizedDogMessage(user.getId(), dog, contextKey);
                    }
                    
                } catch (Exception e) {
                    log.error("사용자 {}의 메시지 전송 실패: {}", user.getId(), e.getMessage());
                    // 개별 사용자 실패가 전체 프로세스를 중단하지 않도록 계속 진행
                }
            }
            
            log.info("전체 사용자 대상 푸시 알림 완료");
            
        } catch (Exception e) {
            log.error("전체 사용자 대상 푸시 알림 실패", e);
            throw new RuntimeException("푸시 알림 전송 중 오류가 발생했습니다.", e);
        }
    }

    
}

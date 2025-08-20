package com.ohgiraffers.togedaeng.backend.domain.user.model.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class FcmTokenResponseDto {
    private Long id;
    private String fcmToken;
    private String message;
}

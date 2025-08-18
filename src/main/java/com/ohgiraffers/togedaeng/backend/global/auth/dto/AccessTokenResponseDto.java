package com.ohgiraffers.togedaeng.backend.global.auth.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

@Getter
@AllArgsConstructor
@NoArgsConstructor
public class AccessTokenResponseDto {
	private String accessToken;
}

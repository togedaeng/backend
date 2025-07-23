package com.ohgiraffers.togedaeng.backend.global.auth.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class AuthorizationCodeRequest {
	private String code;
	private String redirectUri;
	private String codeVerifier;
}

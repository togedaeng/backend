package com.ohgiraffers.togedaeng.backend.global.auth.service;

import com.ohgiraffers.togedaeng.backend.global.auth.dto.OAuthUserInfo;

public interface OAuthClient {
	String getProvider();

	String getProviderId();

	String getEmail();

	OAuthUserInfo getUserInfo(String code, String redirectUri, String codeVerifier);
}

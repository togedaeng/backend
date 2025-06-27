package com.ohgiraffers.togedaeng.backend.global.auth.service;

import org.springframework.stereotype.Service;
import com.ohgiraffers.togedaeng.backend.global.auth.dto.OAuthUserInfo;

@Service("google")
public class GoogleOAuthClient implements OAuthClient {

	@Override
	public String getProvider() {
		return "google";
	}

	@Override
	public String getProviderId() {
		// 실제 구현에서는 userInfo에서 providerId 추출
		return null;
	}

	@Override
	public String getEmail() {
		// 실제 구현에서는 userInfo에서 email 추출
		return null;
	}

	@Override
	public OAuthUserInfo getUserInfo(String code, String redirectUri) {
		// code, redirectUri로 구글 API에 access_token 요청
		// access_token으로 userInfo 요청
		// userInfo를 OAuthUserInfo 구현체로 반환
		return new OAuthUserInfo() {
			@Override
			public String getProvider() { return "google"; }
			@Override
			public String getProviderId() { return "sampleProviderId"; }
			@Override
			public String getEmail() { return "sample@email.com"; }
		};
	}
}

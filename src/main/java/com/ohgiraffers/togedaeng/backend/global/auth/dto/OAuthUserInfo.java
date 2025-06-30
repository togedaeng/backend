package com.ohgiraffers.togedaeng.backend.global.auth.dto;

public interface OAuthUserInfo {
	String getProvider();

	String getProviderId();

	String getEmail();
}

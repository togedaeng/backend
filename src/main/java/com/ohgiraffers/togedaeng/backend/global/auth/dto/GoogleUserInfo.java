package com.ohgiraffers.togedaeng.backend.global.auth.dto;

import java.util.Map;

public abstract class GoogleUserInfo implements OAuthUserInfo {
	private Map<String, Object> attributes;

	public GoogleUserInfo(Map<String, Object> attributes) {
		this.attributes = attributes;
	}

	@Override
	public String getProviderId() {
		return (String)attributes.get("sub");
	}

	@Override
	public String getEmail() {
		return (String)attributes.get("email");
	}
}

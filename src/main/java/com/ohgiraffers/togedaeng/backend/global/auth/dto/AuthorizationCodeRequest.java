package com.ohgiraffers.togedaeng.backend.global.auth.dto;

public class AuthorizationCodeRequest {
	private String code;
	private String redirectUri;

	public AuthorizationCodeRequest(String code, String redirectUri) {
		this.code = code;
		this.redirectUri = redirectUri;
	}

	public String getCode() {
		return code;
	}

	public void setCode(String code) {
		this.code = code;
	}

	public String getRedirectUri() {
		return redirectUri;
	}

	public void setRedirectUri(String redirectUri) {
		this.redirectUri = redirectUri;
	}
}

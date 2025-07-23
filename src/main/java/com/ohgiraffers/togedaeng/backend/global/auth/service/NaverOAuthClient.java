package com.ohgiraffers.togedaeng.backend.global.auth.service;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestTemplate;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.ohgiraffers.togedaeng.backend.global.auth.dto.OAuthUserInfo;

@Service("naver")
public class NaverOAuthClient implements OAuthClient {

	@Value("${oauth.naver.client-id}")
	private String clientId;

	@Value("${oauth.naver.client-secret}")
	private String clientSecret;

	private final RestTemplate restTemplate = new RestTemplate();
	private final ObjectMapper objectMapper = new ObjectMapper();

	@Override
	public String getProvider() {
		return "naver";
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
	public OAuthUserInfo getUserInfo(String code, String redirectUri, String codeVerifier) {
		return null;
	}

	public OAuthUserInfo getUserInfo(String code, String redirectUri) {
		try {
			// 1. authorization code로 access token 요청
			String accessToken = getAccessToken(code, redirectUri);
			
			// 2. access token으로 사용자 정보 요청
			return getUserInfoFromNaver(accessToken);
		} catch (Exception e) {
			throw new RuntimeException("네이버 OAuth 처리 중 오류 발생", e);
		}
	}

	private String getAccessToken(String code, String redirectUri) {
		HttpHeaders headers = new HttpHeaders();
		headers.setContentType(MediaType.APPLICATION_FORM_URLENCODED);

		MultiValueMap<String, String> params = new LinkedMultiValueMap<>();
		params.add("grant_type", "authorization_code");
		params.add("client_id", clientId);
		params.add("client_secret", clientSecret);
		params.add("code", code);
		params.add("redirect_uri", redirectUri);

		HttpEntity<MultiValueMap<String, String>> request = new HttpEntity<>(params, headers);

		ResponseEntity<String> response = restTemplate.exchange(
			"https://nid.naver.com/oauth2.0/token",
			HttpMethod.POST,
			request,
			String.class
		);

		try {
			JsonNode jsonNode = objectMapper.readTree(response.getBody());
			return jsonNode.get("access_token").asText();
		} catch (Exception e) {
			throw new RuntimeException("Access token 파싱 실패", e);
		}
	}

	private OAuthUserInfo getUserInfoFromNaver(String accessToken) {
		HttpHeaders headers = new HttpHeaders();
		headers.setBearerAuth(accessToken);

		HttpEntity<String> request = new HttpEntity<>(headers);

		ResponseEntity<String> response = restTemplate.exchange(
			"https://openapi.naver.com/v1/nid/me",
			HttpMethod.GET,
			request,
			String.class
		);

		try {
			JsonNode jsonNode = objectMapper.readTree(response.getBody());
			JsonNode responseNode = jsonNode.get("response");
			
			final String providerId = responseNode.get("id").asText();
			final String email = responseNode.get("email").asText();
			
			return new OAuthUserInfo() {
				@Override
				public String getProvider() { return "naver"; }
				@Override
				public String getProviderId() { return providerId; }
				@Override
				public String getEmail() { return email; }
			};
		} catch (Exception e) {
			throw new RuntimeException("네이버 사용자 정보 파싱 실패", e);
		}
	}
} 
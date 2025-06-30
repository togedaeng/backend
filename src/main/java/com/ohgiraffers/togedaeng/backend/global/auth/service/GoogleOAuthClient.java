package com.ohgiraffers.togedaeng.backend.global.auth.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
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

@Service("google")
public class GoogleOAuthClient implements OAuthClient {

	private static final Logger log = LoggerFactory.getLogger(GoogleOAuthClient.class);

	@Value("${oauth.google.client-id}")
	private String clientId;

	@Value("${oauth.google.client-secret}")
	private String clientSecret;

	private final RestTemplate restTemplate = new RestTemplate();
	private final ObjectMapper objectMapper = new ObjectMapper();

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
		log.info("Google OAuth getUserInfo called - code: {}, redirectUri: {}", code, redirectUri);
		log.info("Client ID: {}", clientId);
		log.info("Client Secret: {}", clientSecret != null ? "***" : "NULL");
		
		try {
			// 1. authorization code로 access token 요청
			String accessToken = getAccessToken(code, redirectUri);
			log.info("Access token obtained successfully");
			
			// 2. access token으로 사용자 정보 요청
			return getUserInfoFromGoogle(accessToken);
		} catch (Exception e) {
			log.error("Google OAuth error", e);
			throw new RuntimeException("구글 OAuth 처리 중 오류 발생", e);
		}
	}

	private String getAccessToken(String code, String redirectUri) {
		log.info("Requesting access token from Google OAuth API");
		
		HttpHeaders headers = new HttpHeaders();
		headers.setContentType(MediaType.APPLICATION_FORM_URLENCODED);

		MultiValueMap<String, String> params = new LinkedMultiValueMap<>();
		params.add("grant_type", "authorization_code");
		params.add("client_id", clientId);
		params.add("client_secret", clientSecret);
		params.add("code", code);
		params.add("redirect_uri", redirectUri);

		log.info("Token request params - grant_type: authorization_code, client_id: {}, redirect_uri: {}", clientId, redirectUri);
		log.info("Full request params: {}", params);

		HttpEntity<MultiValueMap<String, String>> request = new HttpEntity<>(params, headers);

		try {
			ResponseEntity<String> response = restTemplate.exchange(
				"https://oauth2.googleapis.com/token",
				HttpMethod.POST,
				request,
				String.class
			);

			log.info("Google OAuth API response status: {}", response.getStatusCode());
			log.info("Google OAuth API response body: {}", response.getBody());

			JsonNode jsonNode = objectMapper.readTree(response.getBody());
			String accessToken = jsonNode.get("access_token").asText();
			log.info("Access token extracted successfully");
			return accessToken;
			
		} catch (Exception e) {
			log.error("Failed to get access token from Google OAuth API", e);
			
			// 에러 응답 본문도 로깅
			if (e instanceof org.springframework.web.client.HttpClientErrorException) {
				org.springframework.web.client.HttpClientErrorException httpEx = 
					(org.springframework.web.client.HttpClientErrorException) e;
				log.error("HTTP Error Response Body: {}", httpEx.getResponseBodyAsString());
			}
			
			throw new RuntimeException("Access token 요청 실패", e);
		}
	}

	private OAuthUserInfo getUserInfoFromGoogle(String accessToken) {
		log.info("Requesting user info from Google API");
		
		HttpHeaders headers = new HttpHeaders();
		headers.setBearerAuth(accessToken);

		HttpEntity<String> request = new HttpEntity<>(headers);

		try {
			ResponseEntity<String> response = restTemplate.exchange(
				"https://www.googleapis.com/oauth2/v2/userinfo",
				HttpMethod.GET,
				request,
				String.class
			);

			log.info("Google User Info API response status: {}", response.getStatusCode());
			log.info("Google User Info API response body: {}", response.getBody());

			JsonNode jsonNode = objectMapper.readTree(response.getBody());
			
			final String providerId = jsonNode.get("id").asText();
			final String email = jsonNode.get("email").asText();
			
			log.info("User info extracted - providerId: {}, email: {}", providerId, email);
			
			return new OAuthUserInfo() {
				@Override
				public String getProvider() { return "google"; }
				@Override
				public String getProviderId() { return providerId; }
				@Override
				public String getEmail() { return email; }
			};
		} catch (Exception e) {
			log.error("Failed to get user info from Google API", e);
			throw new RuntimeException("구글 사용자 정보 파싱 실패", e);
		}
	}
}

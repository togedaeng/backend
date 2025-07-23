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

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.ohgiraffers.togedaeng.backend.global.auth.dto.AuthorizationCodeRequest;
import com.ohgiraffers.togedaeng.backend.global.auth.dto.OAuthUserInfo;

@Service("google")
public class GoogleOAuthClient implements OAuthClient {

	private static final Logger log = LoggerFactory.getLogger(GoogleOAuthClient.class);

	@Value("${oauth.google.web.client-id}")
	private String webClientId;

	@Value("${oauth.google.web.client-secret}")
	private String webClientSecret;

	@Value("${oauth.google.android.client-id}")
	private String androidClientId;

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
	public OAuthUserInfo getUserInfo(String code, String redirectUri, String codeVerifier) {
		log.info("Google OAuth getUserInfo called - code: {}, redirectUri: {}", code, redirectUri);

		boolean isAndroid = redirectUri != null && redirectUri.startsWith("com.googleusercontent.apps");
		log.info("Using {} credentials", isAndroid ? "Android (PKCE)" : "Web (client_secret)");
		log.info("codeVerifier: {}", codeVerifier);

		String accessToken;
		try {
			if (isAndroid) {
				// PKCE 흐름
				accessToken = getAccessTokenWithPKCE(code, redirectUri, androidClientId, codeVerifier);
			} else {
				// client_secret 흐름
				accessToken = getAccessTokenWithSecret(code, redirectUri, webClientId, webClientSecret);
			}

			return getUserInfoFromGoogle(accessToken);
		} catch (Exception e) {
			log.error("Google OAuth error", e);
			throw new RuntimeException("구글 OAuth 처리 중 오류 발생", e);
		}
	}

	private String getAccessTokenWithSecret(String code,
		String redirectUri,
		String clientId,
		String clientSecret) {
		MultiValueMap<String,String> params = new LinkedMultiValueMap<>();
		params.add("grant_type",    "authorization_code");
		params.add("client_id",     clientId);
		params.add("client_secret", clientSecret);
		params.add("code",          code);
		params.add("redirect_uri",  redirectUri);

		return requestToken(params);
	}

	private String getAccessTokenWithPKCE(String code,
		String redirectUri,
		String clientId,
		String codeVerifier) {
		MultiValueMap<String,String> params = new LinkedMultiValueMap<>();
		params.add("grant_type",    "authorization_code");
		params.add("client_id",     clientId);
		params.add("code",          code);
		params.add("redirect_uri",  redirectUri);
		params.add("code_verifier", codeVerifier);

		return requestToken(params);
	}

	private String requestToken(MultiValueMap<String,String> params) {
		HttpHeaders headers = new HttpHeaders();
		headers.setContentType(MediaType.APPLICATION_FORM_URLENCODED);

		HttpEntity<MultiValueMap<String,String>> request = new HttpEntity<>(params, headers);
		ResponseEntity<String> resp = restTemplate.exchange(
			"https://oauth2.googleapis.com/token",
			HttpMethod.POST,
			request,
			String.class
		);

		JsonNode json = null;
		try {
			json = objectMapper.readTree(resp.getBody());
		} catch (JsonProcessingException e) {
			throw new RuntimeException(e);
		}
		return json.get("access_token").asText();
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

package com.ohgiraffers.togedaeng.backend.global.auth.controller;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.ohgiraffers.togedaeng.backend.global.auth.dto.OAuthUserInfo;
import com.ohgiraffers.togedaeng.backend.global.auth.dto.TokenResponseDto;
import com.ohgiraffers.togedaeng.backend.global.auth.service.OAuthService;

import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;

@RestController
@RequestMapping("/oauth/callback")
public class OAuthCallbackController {
	
	Logger log = LoggerFactory.getLogger(OAuthCallbackController.class);
	
	private final OAuthService oAuthService;
	
	@Value("${frontend.url}")
	private String frontendUrl;
	
	@Autowired
	public OAuthCallbackController(OAuthService oAuthService) {
		this.oAuthService = oAuthService;
	}
	
	@GetMapping("/{provider}")
	public void handleOAuthCallback(
			@PathVariable("provider") String provider,
			@RequestParam("code") String code,
			@RequestParam(value = "error", required = false) String error,
			@RequestParam(value = "state", required = false) String state,
			HttpServletResponse response) throws IOException {
		
		log.info("OAuth callback received - provider: {}, code: {}", provider, code);
		
		if (error != null) {
			log.error("OAuth error: {}", error);
			response.sendRedirect(frontendUrl + "/login?error=oauth_failed");
			return;
		}
		
		try {
			// authorization code로 사용자 정보 가져오기
			String redirectUri = "http://localhost:8080/oauth/callback/" + provider;
			OAuthUserInfo userInfo = oAuthService.getUserInfo(provider, code, redirectUri);
			
			// 기존 회원인지 확인
			boolean isRegistered = oAuthService.isUserRegistered(userInfo.getProviderId(), provider);
			
			if (isRegistered) {
				// 기존 회원이면 JWT 발급 후 프론트엔드로 리디렉트
				TokenResponseDto token = oAuthService.login(userInfo.getProviderId(), provider);
				String successUrl = String.format(
					"%s/oauth/success?accessToken=%s&refreshToken=%s",
					frontendUrl,
					token.getAccessToken(),
					token.getRefreshToken()
				);
				response.sendRedirect(successUrl);
			} else {
				// 신규 회원은 추가정보 입력 페이지로 리디렉트
				String signupUrl = String.format(
					"%s/signup?email=%s&provider=%s&providerId=%s",
					frontendUrl,
					userInfo.getEmail(),
					userInfo.getProvider(),
					userInfo.getProviderId()
				);
				response.sendRedirect(signupUrl);
			}
			
		} catch (Exception e) {
			log.error("OAuth callback processing error", e);
			response.sendRedirect(frontendUrl + "/login?error=processing_failed");
		}
	}
} 
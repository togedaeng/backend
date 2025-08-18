package com.ohgiraffers.togedaeng.backend.global.auth.service;

import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.ohgiraffers.togedaeng.backend.domain.user.model.dto.UserResponseDto;
import com.ohgiraffers.togedaeng.backend.domain.user.model.dto.UserInfoRequestDto;
import com.ohgiraffers.togedaeng.backend.domain.user.model.entity.User;
import com.ohgiraffers.togedaeng.backend.domain.user.repository.UserRepository;
import com.ohgiraffers.togedaeng.backend.domain.user.service.UserService;
import com.ohgiraffers.togedaeng.backend.global.auth.dto.OAuthUserInfo;
import com.ohgiraffers.togedaeng.backend.global.auth.dto.TokenResponseDto;

import jakarta.transaction.Transactional;

@Service
public class OAuthService {

	private final Map<String, OAuthClient> oauthClients;
	private final UserService userService;
	private final JwtProvider jwtProvider;
	private final UserRepository userRepository;
	private final GoogleOAuthClient googleOAuthClient;
	private final NaverOAuthClient naverOAuthClient;

	@Autowired
	public OAuthService(Map<String, OAuthClient> oauthClients, UserService userService, JwtProvider jwtProvider,
		UserRepository userRepository, GoogleOAuthClient googleOAuthClient, NaverOAuthClient naverOAuthClient) {
		this.oauthClients = oauthClients;
		this.userService = userService;
		this.jwtProvider = jwtProvider;
		this.userRepository = userRepository;
		this.googleOAuthClient = googleOAuthClient;
		this.naverOAuthClient = naverOAuthClient;
	}

	// 소셜로그인 시도한 클라이언트의 플랫폼, 코드, uri 가져옴
	public OAuthUserInfo getUserInfo(String provider, String code, String redirectUri, String codeVerifier) {
		if ("google".equalsIgnoreCase(provider)) {
			return googleOAuthClient.getUserInfo(code, redirectUri, codeVerifier);
		} else if ("naver".equalsIgnoreCase(provider)) {
			return naverOAuthClient.getUserInfo(code, redirectUri, codeVerifier);
		} else {
			throw new IllegalArgumentException("지원하지 않는 provider: " + provider);
		}
	}

	// 기존회원인지 확인
	public boolean isUserRegistered(String providerId, String provider) {
		return userRepository.findByProviderAndProviderId(provider, providerId).isPresent();
	}

	public TokenResponseDto login(String providerId, String provider) {
		User user = userRepository.findByProviderAndProviderId(provider, providerId)
			.orElseThrow(() -> new RuntimeException("사용자 없음"));
		// JWT 토큰 발급
		return jwtProvider.issue(user);
	}

	@Transactional
	public TokenResponseDto createUser(UserInfoRequestDto dto) {
		try {
			// 회원 생성
			userService.createUser(dto);
			// provider, providerId로 다시 조회하여 토큰 발급
			User user = userRepository.findByProviderAndProviderId(dto.getProvider(), dto.getProviderId())
				.orElseThrow(() -> new RuntimeException("회원 생성 실패"));
			// JWT 토큰 발급
			return jwtProvider.issue(user);
		} catch (Exception e) {
			e.printStackTrace();
			return null;
		}
	}
}

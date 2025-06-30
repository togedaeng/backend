package com.ohgiraffers.togedaeng.backend.global.auth.controller;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.ohgiraffers.togedaeng.backend.domain.user.model.dto.UserInfoRequestDto;
import com.ohgiraffers.togedaeng.backend.domain.user.model.entity.User;
import com.ohgiraffers.togedaeng.backend.global.auth.dto.AuthorizationCodeRequest;
import com.ohgiraffers.togedaeng.backend.global.auth.dto.AuthResponseDto;
import com.ohgiraffers.togedaeng.backend.global.auth.dto.OAuthUserInfo;
import com.ohgiraffers.togedaeng.backend.global.auth.dto.RefreshTokenRequest;
import com.ohgiraffers.togedaeng.backend.global.auth.dto.TokenResponseDto;
import com.ohgiraffers.togedaeng.backend.global.auth.service.OAuthService;
import com.ohgiraffers.togedaeng.backend.global.auth.service.JwtProvider;
import com.ohgiraffers.togedaeng.backend.domain.user.repository.UserRepository;

// 프론트에서 로그인 후 받은 인가토큰을 넘겨받음
@RestController
@RequestMapping("/auth")
public class AuthController {
	Logger log = LoggerFactory.getLogger(AuthController.class);

	private final OAuthService oAuthService;
	private final JwtProvider jwtProvider;
	private final UserRepository userRepository;
	
	@Value("${frontend.url}")
	private String frontendUrl;

	@Autowired
	public AuthController(OAuthService oAuthService, JwtProvider jwtProvider, UserRepository userRepository) {
		this.oAuthService = oAuthService;
		this.jwtProvider = jwtProvider;
		this.userRepository = userRepository;
	}

	/**
	 * 📍 OAuth 로그인 처리 (POST 방식 - 기존 API 유지)
	 * 1. authorization code로 access_token 요청
	 * 2. access_token으로 사용자 정보 요청
	 * 3. 기존 회원이면 JWT 발급, 신규 회원이면 추가 정보 요청
	 */
	@PostMapping("/oauth/{provider}")
	public ResponseEntity<?> socialLogin(@PathVariable("provider") String provider, 
		@RequestBody AuthorizationCodeRequest request) {

		log.info("OAuth login request - provider: {}, code: {}", provider, request.getCode());

		try {
			// 1. authorization code로 사용자 정보 가져오기
			OAuthUserInfo userInfo = oAuthService.getUserInfo(provider, request.getCode(), request.getRedirectUri());
			
			// 2. 기존 회원인지 확인
			boolean isRegistered = oAuthService.isUserRegistered(userInfo.getProviderId(), provider);

			if (isRegistered) {
				// 기존 회원이면 JWT 발급
				TokenResponseDto token = oAuthService.login(userInfo.getProviderId(), provider);
				log.info("Existing user login successful - providerId: {}", userInfo.getProviderId());
				return ResponseEntity.ok(token);
			} else {
				// 신규 회원은 추가정보 입력하게
				log.info("New user requires additional info - email: {}, provider: {}", userInfo.getEmail(), provider);
				return ResponseEntity.status(HttpStatus.ACCEPTED).body(userInfo);
			}
		} catch (Exception e) {
			log.error("OAuth login error", e);
			return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
				.body("로그인 처리 중 오류가 발생했습니다.");
		}
	}

	/**
	 * 📍 소셜 로그인 후 회원 정보 등록
	 * @param userInfoRequestDto 회원 정보 등록 DTO
	 * @return AuthResponseDto (User + Token)
	 */
	@PostMapping("/create")
	public ResponseEntity<AuthResponseDto> createUser(@RequestBody UserInfoRequestDto userInfoRequestDto) {
		log.info("Create user request: {}", userInfoRequestDto);
		
		try {
			TokenResponseDto token = oAuthService.createUser(userInfoRequestDto);
			
			// 생성된 사용자 정보 조회
			User user = userRepository.findByProviderAndProviderId(
				userInfoRequestDto.getProvider(), 
				userInfoRequestDto.getProviderId()
			).orElseThrow(() -> new RuntimeException("회원 생성 후 조회 실패"));
			
			AuthResponseDto response = new AuthResponseDto(user, token);
			log.info("User created successfully - userId: {}", user.getId());
			
			return new ResponseEntity<>(response, HttpStatus.CREATED);
		} catch (Exception e) {
			log.error("User creation error", e);
			throw new RuntimeException("회원가입 중 오류가 발생했습니다.", e);
		}
	}

	/**
	 * 📍 JWT 토큰 재발급
	 * @param refreshTokenRequest refresh token 요청 DTO
	 * @return 새로운 access token
	 */
	@PostMapping("/refresh")
	public ResponseEntity<TokenResponseDto> refreshToken(@RequestBody RefreshTokenRequest refreshTokenRequest) {
		log.info("Token refresh request");
		
		try {
			String refreshToken = refreshTokenRequest.getRefreshToken();
			
			// refresh token에서 사용자 정보 추출
			Long userId = jwtProvider.getUserId(refreshToken);
			User user = userRepository.findById(userId)
				.orElseThrow(() -> new RuntimeException("사용자를 찾을 수 없습니다."));
			
			// 새로운 access token 발급
			String newAccessToken = jwtProvider.reissueAccessToken(refreshToken, user);
			TokenResponseDto tokenResponse = new TokenResponseDto(newAccessToken, refreshToken);
			
			return ResponseEntity.ok(tokenResponse);
		} catch (Exception e) {
			log.error("Token refresh error", e);
			return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
		}
	}
}

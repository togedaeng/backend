package com.ohgiraffers.togedaeng.backend.global.auth.controller;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.ohgiraffers.togedaeng.backend.domain.user.model.dto.UserInfoRequestDto;
import com.ohgiraffers.togedaeng.backend.domain.user.model.dto.UserResponseDto;
import com.ohgiraffers.togedaeng.backend.domain.user.model.entity.User;
import com.ohgiraffers.togedaeng.backend.global.auth.dto.AuthorizationCodeRequest;
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

	@Autowired
	public AuthController(OAuthService oAuthService, JwtProvider jwtProvider, UserRepository userRepository) {
		this.oAuthService = oAuthService;
		this.jwtProvider = jwtProvider;
		this.userRepository = userRepository;
	}

	@PostMapping("/oauth/{provider}")
	public ResponseEntity<?> socialLogin(@PathVariable("provider") String provider, @RequestBody
	AuthorizationCodeRequest request) {

		OAuthUserInfo userInfo = oAuthService.getUserInfo(provider, request.getCode(), request.getRedirectUri());

		boolean isRegistered = oAuthService.isUserRegistered(userInfo.getProviderId(), provider);

		if (isRegistered) {
			//기존회원이면 JWT 발급
			TokenResponseDto token = oAuthService.login(userInfo.getProviderId(), provider);
			return ResponseEntity.ok(token);
		} else {
			// 신규회원은 추가정보 입력하게
			return ResponseEntity.status(HttpStatus.ACCEPTED).body(userInfo);
		}
	}

	/**
	 * 📍 소셜 로그인 후 회원 정보 등록
	 * @param userInfoRequestDto 회원 정보 등록 DTO
	 * @return JWT 토큰 정보
	 */
	@PostMapping("/create")
	public ResponseEntity<TokenResponseDto> createUser(@RequestBody UserInfoRequestDto userInfoRequestDto) {
		log.info("Create user request: {}", userInfoRequestDto);
		TokenResponseDto token = oAuthService.createUser(userInfoRequestDto);
		return new ResponseEntity<>(token, HttpStatus.CREATED);
	}

	/**
	 * 📍 JWT 토큰 재발급
	 * @param refreshTokenRequest refresh token 요청 DTO
	 * @return 새로운 access token
	 */
	@PostMapping("/refresh")
	public ResponseEntity<TokenResponseDto> refreshToken(@RequestBody RefreshTokenRequest refreshTokenRequest) {
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
			log.error("Token refresh failed: {}", e.getMessage());
			return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
		}
	}

}

package com.ohgiraffers.togedaeng.backend.global.auth.service;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import com.ohgiraffers.togedaeng.backend.domain.user.model.entity.User;
import com.ohgiraffers.togedaeng.backend.global.auth.dto.TokenResponseDto;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.security.Keys;

@Component
public class JwtProvider {

	@Value("${jwt.secret:defaultSecretKey}")
	private String secretKey;

	@Value("${jwt.access-token-validity:3600000}") // 1시간
	private long accessTokenValidity;

	@Value("${jwt.refresh-token-validity:2592000000}") // 30일
	private long refreshTokenValidity;

	/**
	 * Access Token과 Refresh Token 발급
	 */
	public TokenResponseDto issue(User user) {
		String accessToken = createAccessToken(user);
		String refreshToken = createRefreshToken(user);
		return new TokenResponseDto(accessToken, refreshToken);
	}

	/**
	 * Access Token 생성
	 */
	public String createAccessToken(User user) {
		Map<String, Object> claims = new HashMap<>();
		claims.put("userId", user.getId());
		claims.put("email", user.getEmail());
		claims.put("provider", user.getProvider());
		claims.put("providerId", user.getProviderId());
		claims.put("role", user.getRole());

		return createToken(claims, user.getEmail(), accessTokenValidity);
	}

	/**
	 * Refresh Token 생성
	 */
	public String createRefreshToken(User user) {
		Map<String, Object> claims = new HashMap<>();
		claims.put("userId", user.getId());
		claims.put("type", "refresh");

		return createToken(claims, user.getEmail(), refreshTokenValidity);
	}

	/**
	 * JWT 토큰 생성
	 */
	private String createToken(Map<String, Object> claims, String subject, long validity) {
		return Jwts.builder()
			.setClaims(claims)
			.setSubject(subject)
			.setIssuedAt(new Date(System.currentTimeMillis()))
			.setExpiration(new Date(System.currentTimeMillis() + validity))
			.signWith(Keys.hmacShaKeyFor(secretKey.getBytes()), SignatureAlgorithm.HS256)
			.compact();
	}

	/**
	 * 토큰에서 Claims 추출
	 */
	public Claims getClaims(String token) {
		return Jwts.parser()
			.verifyWith(Keys.hmacShaKeyFor(secretKey.getBytes()))
			.build()
			.parseSignedClaims(token)
			.getPayload();
	}

	/**
	 * 토큰 유효성 검증
	 */
	public boolean validateToken(String token) {
		try {
			Jwts.parser()
				.verifyWith(Keys.hmacShaKeyFor(secretKey.getBytes()))
				.build()
				.parseSignedClaims(token);
			return true;
		} catch (Exception e) {
			return false;
		}
	}

	/**
	 * 토큰에서 사용자 ID 추출
	 */
	public Long getUserId(String token) {
		Claims claims = getClaims(token);
		return claims.get("userId", Long.class);
	}

	/**
	 * 토큰에서 이메일 추출
	 */
	public String getEmail(String token) {
		Claims claims = getClaims(token);
		return claims.getSubject();
	}

	/**
	 * Refresh Token으로 Access Token 재발급
	 */
	public String reissueAccessToken(String refreshToken, User user) {
		if (validateToken(refreshToken)) {
			Claims claims = getClaims(refreshToken);
			String tokenType = claims.get("type", String.class);

			if ("refresh".equals(tokenType)) {
				return createAccessToken(user);
			}
		}
		throw new RuntimeException("Invalid refresh token");
	}
}

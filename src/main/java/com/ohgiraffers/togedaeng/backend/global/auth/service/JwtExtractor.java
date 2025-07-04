package com.ohgiraffers.togedaeng.backend.global.auth.service;

import org.springframework.stereotype.Component;

import jakarta.servlet.http.HttpServletRequest;

@Component
public class JwtExtractor {

	private final JwtProvider jwtProvider;

	public JwtExtractor(JwtProvider jwtProvider) {
		this.jwtProvider = jwtProvider;
	}

	public Long extractUserId(HttpServletRequest request) {
		String authHeader = request.getHeader("Authorization");
		if (authHeader == null || !authHeader.startsWith("Bearer ")) {
			throw new RuntimeException("Missing or invalid Authorization header");
		}

		String token = authHeader.substring(7); // "Bearer " 제거
		return jwtProvider.getUserId(token);
	}
}

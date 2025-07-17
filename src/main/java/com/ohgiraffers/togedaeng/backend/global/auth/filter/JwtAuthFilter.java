package com.ohgiraffers.togedaeng.backend.global.auth.filter;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import com.ohgiraffers.togedaeng.backend.global.auth.service.JwtProvider;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;

@Component
@RequiredArgsConstructor
public class JwtAuthFilter extends OncePerRequestFilter {

    private final JwtProvider jwtProvider;
    private static final Logger log = LoggerFactory.getLogger(JwtAuthFilter.class);

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
            throws ServletException, IOException {
        
        String requestURI = request.getRequestURI();
        log.info("JwtAuthFilter processing request: {}", requestURI);
        
        String token = extractToken(request);
        
        if (token != null) {
            log.info("Token found, validating...");
            if (jwtProvider.validateToken(token)) {
                try {
                    Long userId = jwtProvider.getUserId(token);
                    String email = jwtProvider.getEmail(token);
                    String role = jwtProvider.getClaims(token).get("role", String.class);
                    
                    log.info("Token is valid for userId: {}, email: {}, role: {}", userId, email, role);

                    SimpleGrantedAuthority authority = new SimpleGrantedAuthority("ROLE_" + role);
                    
                    // Spring Security Authentication 객체 생성
                    UsernamePasswordAuthenticationToken authentication = 
                        new UsernamePasswordAuthenticationToken(userId, null, List.of(authority));
                    
                    // SecurityContext에 인증 정보 설정
                    SecurityContextHolder.getContext().setAuthentication(authentication);
                    log.info("Authentication set in SecurityContext");
                    
                } catch (Exception e) {
                    log.error("Error processing token", e);
                    // 토큰이 유효하지 않은 경우 인증 정보 제거
                    SecurityContextHolder.clearContext();
                }
            } else {
                log.warn("Token validation failed");
                SecurityContextHolder.clearContext();
            }
        } else {
            log.info("No token found in request");
        }
        
        filterChain.doFilter(request, response);
    }

    private String extractToken(HttpServletRequest request) {
        String bearerToken = request.getHeader("Authorization");
        if (bearerToken != null && bearerToken.startsWith("Bearer ")) {
            return bearerToken.substring(7);
        }
        return null;
    }
}

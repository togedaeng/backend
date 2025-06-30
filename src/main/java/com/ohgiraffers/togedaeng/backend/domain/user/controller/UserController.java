package com.ohgiraffers.togedaeng.backend.domain.user.controller;

import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.ohgiraffers.togedaeng.backend.domain.user.model.dto.DeleteUserResponseDto;
import com.ohgiraffers.togedaeng.backend.domain.user.model.dto.UserNicknameUpdateDto;
import com.ohgiraffers.togedaeng.backend.domain.user.model.dto.UserResponseDto;
import com.ohgiraffers.togedaeng.backend.domain.user.model.entity.User;
import com.ohgiraffers.togedaeng.backend.domain.user.repository.UserRepository;
import com.ohgiraffers.togedaeng.backend.domain.user.service.UserService;
import com.ohgiraffers.togedaeng.backend.global.auth.service.JwtProvider;

import jakarta.servlet.http.HttpServletRequest;

@RestController
@RequestMapping("/user")
public class UserController {
	Logger log = LoggerFactory.getLogger(UserController.class);

	private final UserService userService;
	private final JwtProvider jwtProvider;
	private final UserRepository userRepository;

	@Autowired
	public UserController(UserService userService, JwtProvider jwtProvider, UserRepository userRepository) {
		this.userService = userService;
		this.jwtProvider = jwtProvider;
		this.userRepository = userRepository;
	}

	/**
	 * 📍 현재 로그인한 사용자 정보 조회
	 * @param request HTTP 요청 (JWT 토큰 추출용)
	 * @return 현재 로그인한 사용자 정보
	 */
	@GetMapping("/me")
	public ResponseEntity<UserResponseDto> getCurrentUser(HttpServletRequest request) {
		log.info("GET /user/me request received");
		
		try {
			// Authorization 헤더에서 Bearer 토큰 추출
			String authHeader = request.getHeader("Authorization");
			log.info("Authorization header: {}", authHeader);
			
			if (authHeader == null || !authHeader.startsWith("Bearer ")) {
				log.warn("Invalid or missing Authorization header");
				return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
			}
			
			String token = authHeader.substring(7); // "Bearer " 제거
			log.info("Extracted token: {}", token.substring(0, Math.min(token.length(), 20)) + "...");
			
			// JWT 토큰에서 사용자 ID 추출
			Long userId = jwtProvider.getUserId(token);
			log.info("Extracted userId: {}", userId);
			
			// 사용자 정보 조회
			User user = userRepository.findById(userId)
				.orElseThrow(() -> new RuntimeException("사용자를 찾을 수 없습니다."));
			
			UserResponseDto userResponse = new UserResponseDto(
				user.getId(),
				user.getNickname(),
				user.getGender(),
				user.getBirth(),
				user.getEmail(),
				user.getProvider(),
				user.getStatus(),
				user.getCreatedAt()
			);
			
			log.info("Current user info retrieved - userId: {}", userId);
			return ResponseEntity.ok(userResponse);
			
		} catch (Exception e) {
			log.error("Error getting current user", e);
			return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
		}
	}

	/**
	 * 📍 회원 전체 조회
	 * @return 모든 회원 리스트
	 */
	@GetMapping
	public ResponseEntity<List<UserResponseDto>> getAllUsers() {
		log.info("Get all users");
		List<UserResponseDto> users = userService.getAllUsers();
		return new ResponseEntity<>(users, HttpStatus.OK);
	}

	/**
	 * 📍 회원 단일 조회
	 * @param id 회원 id
	 * @return 회원 정보
	 */
	@GetMapping("/{id}")
	public ResponseEntity<UserResponseDto> getUserById(@PathVariable("id") Long id) {
		log.info("Get user by id: {}", id);
		UserResponseDto user = userService.getUserById(id);
		return new ResponseEntity<>(user, HttpStatus.OK);
	}

	/**
	 * 📍 회원 닉네임 수정
	 * @param id 회원 id
	 * @param userNicknameUpdateDto 수정할 닉네임
	 * @return 수정된 회원 닉네임 정보 (id, 수정된 닉네임, 수정 시각)
	 */
	@PatchMapping("/{id}/nickname")
	public ResponseEntity<UserResponseDto> updateUserNickname(@PathVariable("id") Long id,
		@RequestBody UserNicknameUpdateDto userNicknameUpdateDto) {
		log.info("Update user nickname: {}", userNicknameUpdateDto.getNickname());
		UserResponseDto user = userService.updateUserNickname(id, userNicknameUpdateDto);
		return new ResponseEntity<>(user, HttpStatus.OK);
	}

	/**
	 * 📍 회원 삭제
	 * 상태 INACTIVE로 변경
	 * @param id 회원 id
	 * @return 삭제된 회원 정보 (id, 닉네임, 상태(INACTIVE), 삭제일자)
	 */
	@PatchMapping("/{id}/status")
	public ResponseEntity<DeleteUserResponseDto> deleteUser(@PathVariable("id") Long id) {
		log.info("Delete user: {}", id);
		DeleteUserResponseDto user = userService.deleteUser(id);
		return new ResponseEntity<>(user, HttpStatus.OK);
	}
}

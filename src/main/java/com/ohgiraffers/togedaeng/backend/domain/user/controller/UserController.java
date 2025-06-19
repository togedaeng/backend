package com.ohgiraffers.togedaeng.backend.domain.user.controller;

import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.ohgiraffers.togedaeng.backend.domain.user.model.dto.DeleteUserResponseDto;
import com.ohgiraffers.togedaeng.backend.domain.user.model.dto.UserInfoRequestDto;
import com.ohgiraffers.togedaeng.backend.domain.user.model.dto.UserNicknameUpdateDto;
import com.ohgiraffers.togedaeng.backend.domain.user.model.dto.UserResponseDto;
import com.ohgiraffers.togedaeng.backend.domain.user.service.UserService;

@RestController
@RequestMapping("/api/user")
public class UserController {
	Logger log = LoggerFactory.getLogger(UserController.class);

	private final UserService userService;

	public UserController(UserService userService) {
		this.userService = userService;
	}

	/**
	 * 📍 소셜 로그인 후 회원 정보 등록
	 * @param userInfoRequestDto 회원 정보 등록 DTO
	 * @return 등록된 회원 정보
	 */
	@PostMapping("/create")
	public ResponseEntity<UserResponseDto> createUser(@RequestBody UserInfoRequestDto userInfoRequestDto) {
		log.info("Create user request: {}", userInfoRequestDto);
		UserResponseDto userResponseDto = userService.createUser(userInfoRequestDto);
		return new ResponseEntity<>(userResponseDto, HttpStatus.CREATED);
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

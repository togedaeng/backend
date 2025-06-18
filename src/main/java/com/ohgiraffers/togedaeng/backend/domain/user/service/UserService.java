package com.ohgiraffers.togedaeng.backend.domain.user.service;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import com.ohgiraffers.togedaeng.backend.domain.dog.entity.Status;
import com.ohgiraffers.togedaeng.backend.domain.user.model.dto.DeleteUserResponseDto;
import com.ohgiraffers.togedaeng.backend.domain.user.model.dto.UserInfoRequestDto;
import com.ohgiraffers.togedaeng.backend.domain.user.model.dto.UserNicknameUpdateDto;
import com.ohgiraffers.togedaeng.backend.domain.user.model.dto.UserResponseDto;
import com.ohgiraffers.togedaeng.backend.domain.user.model.entity.User;
import com.ohgiraffers.togedaeng.backend.domain.user.repository.UserRepository;

@Service
public class UserService {

	Logger log = LoggerFactory.getLogger(UserService.class);

	private final UserRepository userRepository;

	public UserService(UserRepository userRepository) {
		this.userRepository = userRepository;
	}

	/**
	 * 📍 소셜 로그인 후 회원 정보 등록
	 * @param dto 회원 정보 등록 DTO
	 * @return 등록된 회원 DTO 변환
	 */
	public UserResponseDto createUser(UserInfoRequestDto dto) {
		try {
			User user = User.builder()
				.nickname(dto.getNickname())
				.gender(dto.getGender())
				.birth(dto.getBirth())
				.email("temp@email.com") // TODO: 소셜 로그인에서 받아온 이메일로 수정
				.status(Status.ACTIVE)
				.createdAt(LocalDateTime.now())
				.build();

			User savedUser = userRepository.save(user);
			log.info("Creating new user: {}", dto.getNickname());

			return new UserResponseDto(
				savedUser.getId(),
				savedUser.getNickname(),
				savedUser.getGender(),
				savedUser.getBirth(),
				savedUser.getEmail(),
				savedUser.getCreatedAt()
			);
		} catch (Exception e) {
			e.printStackTrace();
			return null;
		}
	}

	/**
	 * 📍 회원 전체 조회
	 * @return 모든 회원 리스트
	 */
	public List<UserResponseDto> getAllUsers() {
		List<User> users = userRepository.findAll();
		List<UserResponseDto> userResponseDtos = new ArrayList<>();

		for (User user : users) {
			userResponseDtos.add(new UserResponseDto(
				user.getId(),
				user.getNickname(),
				user.getGender(),
				user.getBirth(),
				user.getEmail(),
				user.getCreatedAt()
			));
		}

		log.info("Get all users: {}", userResponseDtos);

		return userResponseDtos;
	}

	/**
	 * 📍 회원 단일 조회
	 * @param id 회원 id
	 * @return 회원 정보 DTO 변환
	 */
	public UserResponseDto getUserById(Long id) {
		User user = userRepository.findById(id).orElse(null);
		log.info("Get user by id: {}", id);

		if (user == null) {
			return null;
		}

		return new UserResponseDto(
			user.getId(),
			user.getNickname(),
			user.getGender(),
			user.getBirth(),
			user.getEmail(),
			user.getCreatedAt()
		);
	}

	/**
	 * 📍 회원 닉네임 수정
	 * @param id 회원 id
	 * @param dto 수정할 닉네임
	 * @return 수정된 회원 정보
	 */
	public UserResponseDto updateUserNickname(Long id, UserNicknameUpdateDto dto) {
		User user = userRepository.findById(id).orElseThrow(() ->
			new IllegalArgumentException("User not found"));

		log.info("Update user nickname: {}", dto.getNickname());

		user.setNickname(dto.getNickname());
		user.setUpdatedAt(LocalDateTime.now());

		User updatedUser = userRepository.save(user);

		return new UserResponseDto(
			updatedUser.getId(),
			updatedUser.getNickname(),
			updatedUser.getGender(),
			updatedUser.getBirth(),
			updatedUser.getEmail(),
			updatedUser.getCreatedAt()
		);
	}

	/**
	 * 📍 회원 삭제
	 * @param id 회원 id
	 * @return 삭제된 회원 정보 (id, 닉네임, 상태(INACTIVE), 삭제일자)
	 */
	public DeleteUserResponseDto deleteUser(Long id) {
		// 유효성 검증: ID가 유효한지 확인
		if (id == null) {
			throw new IllegalArgumentException("User ID cannot be null");
		}

		User user = userRepository.findById(id).orElseThrow(() ->
			new IllegalArgumentException("User not found"));

		// 유효성 검증: 이미 INACTIVE 상태인지 확인
		if (user.getStatus() == Status.INACTIVE) {
			throw new IllegalArgumentException("User is already inactive");
		}

		log.info("Delete user: {}", id);

		user.setStatus(Status.INACTIVE);
		user.setDeletedAt(LocalDateTime.now());

		User updatedUser = userRepository.save(user);

		return new DeleteUserResponseDto(
			updatedUser.getId(),
			updatedUser.getNickname(),
			updatedUser.getStatus(),
			updatedUser.getDeletedAt()
		);
	}
}

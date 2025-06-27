package com.ohgiraffers.togedaeng.backend.domain.user.service;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import com.ohgiraffers.togedaeng.backend.domain.dog.entity.Status;
import com.ohgiraffers.togedaeng.backend.domain.user.model.dto.DeleteUserResponseDto;
import com.ohgiraffers.togedaeng.backend.domain.user.model.dto.UserNicknameUpdateDto;
import com.ohgiraffers.togedaeng.backend.domain.user.model.dto.UserResponseDto;
import com.ohgiraffers.togedaeng.backend.domain.user.model.entity.User;
import com.ohgiraffers.togedaeng.backend.domain.user.repository.UserRepository;
import com.ohgiraffers.togedaeng.backend.domain.user.model.dto.UserInfoRequestDto;

import jakarta.transaction.Transactional;

@Service
public class UserService {

	Logger log = LoggerFactory.getLogger(UserService.class);

	private final UserRepository userRepository;

	public UserService(UserRepository userRepository) {
		this.userRepository = userRepository;
	}

	/**
	 * 📍 소셜 로그인 후 회원 정보 등록
	 * @param dto 소셜 로그인 이후 받은 회원 정보
	 * @return 등록된 회원 DTO 변환
	 */
	@Transactional
	public UserResponseDto createUser(UserInfoRequestDto dto) {
		// provider, providerId, email로 중복 회원 체크
		if (userRepository.findByProviderAndProviderId(dto.getProvider(), dto.getProviderId()).isPresent()) {
			throw new IllegalArgumentException("이미 가입된 회원입니다.");
		}
		try {
			User user = User.builder()
				.nickname(dto.getNickname())
				.gender(dto.getGender())
				.birth(dto.getBirth())
				.email(dto.getEmail())
				.provider(dto.getProvider())
				.providerId(dto.getProviderId())
				.status(Status.ACTIVE)
				.createdAt(LocalDateTime.now())
				.build();

			User savedUser = userRepository.save(user);
			log.info("생성된 사용자의 닉네임: {}", dto.getNickname());

			return new UserResponseDto(
				savedUser.getId(),
				savedUser.getNickname(),
				savedUser.getGender(),
				savedUser.getBirth(),
				savedUser.getEmail(),
				savedUser.getStatus(),
				savedUser.getCreatedAt()
			);
		} catch (Exception e) {
			e.printStackTrace();
			return null;
		}
	}

	/**
	 * 📍 회원 전체 조회(관리자용)
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
				user.getStatus(),
				user.getCreatedAt()
			));
		}

		log.info("모든 사용자 조회: {}", userResponseDtos);

		return userResponseDtos;
	}

	/**
	 * 📍 회원 단일 조회(관리자용)
	 * @param id 회원 id
	 * @return 회원 정보 DTO 변환
	 */
	public UserResponseDto getUserById(Long id) {
		User user = userRepository.findById(id).orElse(null);
		log.info("조회할 사용자 ID: {}", id);

		if (user == null) {
			return null;
		}

		return new UserResponseDto(
			user.getId(),
			user.getNickname(),
			user.getGender(),
			user.getBirth(),
			user.getEmail(),
			user.getStatus(),
			user.getCreatedAt()
		);
	}

	/**
	 * 📍 회원 닉네임 수정
	 * @param id 회원 id
	 * @param dto 수정할 닉네임
	 * @return 수정된 회원 정보
	 */
	@Transactional
	public UserResponseDto updateUserNickname(Long id, UserNicknameUpdateDto dto) {
		User user = userRepository.findById(id).orElseThrow(() ->
			new IllegalArgumentException("사용자를 찾을 수 없습니다."));

		log.info("수정된 사용자 닉네임: {}", dto.getNickname());

		user.setNickname(dto.getNickname());
		user.setUpdatedAt(LocalDateTime.now());

		User updatedUser = userRepository.save(user);

		return new UserResponseDto(
			updatedUser.getId(),
			updatedUser.getNickname(),
			updatedUser.getGender(),
			updatedUser.getBirth(),
			updatedUser.getEmail(),
			updatedUser.getStatus(),
			updatedUser.getCreatedAt()
		);
	}

	/**
	 * 📍 회원 삭제
	 * @param id 회원 id
	 * @return 삭제된 회원 정보 (id, 닉네임, 상태(INACTIVE), 삭제일자)
	 */
	@Transactional
	public DeleteUserResponseDto deleteUser(Long id) {
		// 유효성 검증: ID가 유효한지 확인
		if (id == null) {
			throw new IllegalArgumentException("요청할 사용자 정보가 없습니다.");
		}

		User user = userRepository.findById(id).orElseThrow(() ->
			new IllegalArgumentException("사용자를 찾을 수 없습니다."));

		// 유효성 검증: 이미 INACTIVE 상태인지 확인
		// 이미 INACTIVE 일 때는 삭제 못하게 하고, 그래도 같은 요청이 들어왔을시에는 익셉션처리를 해줘야 함
		if (user.getStatus() == Status.INACTIVE) {
			throw new IllegalArgumentException("이미 비활성화 된 회원입니다.");
		}

		log.info("탈퇴/차단된 사용자: {}", id);

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

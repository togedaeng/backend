package com.ohgiraffers.togedaeng.backend.domain.dog.service;

import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.ohgiraffers.togedaeng.backend.domain.dog.dto.response.DogOwnerResponseDto;
import com.ohgiraffers.togedaeng.backend.domain.dog.entity.DogOwner;
import com.ohgiraffers.togedaeng.backend.domain.dog.repository.DogOwnerRepository;
import com.ohgiraffers.togedaeng.backend.domain.user.model.entity.User;
import com.ohgiraffers.togedaeng.backend.domain.user.repository.UserRepository;

@Service
public class DogOwnerService {
	private Logger log = LoggerFactory.getLogger(DogOwnerService.class);
	private DogOwnerRepository dogOwnerRepository;
	private UserRepository userRepository;

	@Autowired
	public DogOwnerService(DogOwnerRepository dogOwnerRepository, UserRepository userRepository) {
		this.dogOwnerRepository = dogOwnerRepository;
		this.userRepository = userRepository;
	}

	public List<DogOwnerResponseDto> getDogOwners(Long dogId) {
		try {
			List<DogOwner> dogOwners = dogOwnerRepository.findOwnerIdByDogId(dogId);

			return dogOwners.stream().map(dogOwner -> {
				String userNickname = null;
				User user = userRepository.findById(dogOwner.getUserId()).orElse(null);
				userNickname = (user != null) ? user.getNickname() : null;

				return new DogOwnerResponseDto(
					dogOwner.getDogId(),
					userNickname,
					dogOwner.getDogId(),
					dogOwner.getName(),
					dogOwner.getCreatedAt(),
					dogOwner.getUpdatedAt()
				);
			}).toList();
		} catch (IllegalArgumentException e) {
			log.warn("⚠️ 커스텀 요청 목록 조회 실패 (dogId: {}) - {}", dogId, e.getMessage());
			throw e;
		} catch (Exception e) {
			log.error("❌ 커스텀 요청 목록 조회 중 서버 오류 (dogId: {})", dogId, e);
			throw e;
		}
	}
}

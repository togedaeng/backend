package com.ohgiraffers.togedaeng.backend.domain.dog.service;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import com.ohgiraffers.togedaeng.backend.domain.dog.dto.request.DogRequestDto;
import com.ohgiraffers.togedaeng.backend.domain.dog.dto.response.DogResponseDto;
import com.ohgiraffers.togedaeng.backend.domain.dog.entity.Dog;
import com.ohgiraffers.togedaeng.backend.domain.dog.repository.DogRepository;

@Service
public class DogService {

	Logger log = LoggerFactory.getLogger(DogService.class);

	private final DogRepository dogRepository;

	public DogService(DogRepository dogRepository) {
		this.dogRepository = dogRepository;
	}

	// 기본 강아지 등록
	public DogResponseDto createDog(DogRequestDto dto) {

		// 유저 아이디로 유저 정보 찾기

		try {
			Dog dog = Dog.builder()
				.userId(dto.getUserId())
				.personalityCombinationId(dto.getPersonalityCombinationId())
				.name(dto.getName())
				.gender(dto.getGender())
				.birth(LocalDate.now())
				.type(dto.getType())
				.callName(dto.getCallName())
				.status(dto.getStatus())
				.createdAt(LocalDateTime.now())
				.build();

			Dog savedDog = dogRepository.save(dog);
			log.info("Creating new dog: {}", dto.getName());

			return new DogResponseDto(
				savedDog.getId(),
				savedDog.getUserId(),
				savedDog.getName(),
				savedDog.getGender(),
				savedDog.getBirth(),
				savedDog.getType(),
				savedDog.getCallName(),
				savedDog.getStatus(),
				savedDog.getCreatedAt(),
				savedDog.getUpdatedAt(),
				savedDog.getDeletedAt()
			);
		} catch (Exception e) {
			e.printStackTrace();
			return null;
		}
	}

	// 기본 강아지 전체 조회
	public List<DogResponseDto> getAllDogs() {
		List<Dog> dogs = dogRepository.findAll();
		List<DogResponseDto> dogResponseDtos = new ArrayList<>();

		for (Dog dog : dogs) {
			dogResponseDtos.add(new DogResponseDto(
				dog.getId(),
				dog.getUserId(),
				dog.getName(),
				dog.getGender(),
				dog.getBirth(),
				dog.getType(),
				dog.getCallName(),
				dog.getStatus(),
				dog.getCreatedAt(),
				dog.getUpdatedAt(),
				dog.getDeletedAt()
			));
		}

		log.info("Get all dogs: {}", dogResponseDtos);

		return dogResponseDtos;
	}

	// 기본 강아지 단일 조회
	public DogResponseDto getDogById(Long id) {
		Dog dog = dogRepository.findById(id).orElse(null);
		log.info("Get dog by id: {}", id);
		
		return new DogResponseDto(
			dog.getId(),
			dog.getUserId(),
			dog.getName(),
			dog.getGender(),
			dog.getBirth(),
			dog.getType(),
			dog.getCallName(),
			dog.getStatus(),
			dog.getCreatedAt(),
			dog.getUpdatedAt(),
			dog.getDeletedAt()
		);

	}

	// 기본 강아지 이름 수정

	// 기본 강아지 애칭 수정
}

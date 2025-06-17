package com.ohgiraffers.togedaeng.backend.domain.dog.service;

import java.util.ArrayList;
import java.util.List;

import org.springframework.stereotype.Service;

import com.ohgiraffers.togedaeng.backend.domain.dog.dto.response.DogResponseDto;
import com.ohgiraffers.togedaeng.backend.domain.dog.entity.Dog;
import com.ohgiraffers.togedaeng.backend.domain.dog.repository.DogRepository;

@Service
public class DogService {

	private final DogRepository dogRepository;

	public DogService(DogRepository dogRepository) {
		this.dogRepository = dogRepository;
	}

	// 기본 강아지 등록

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

		return dogResponseDtos;
	}

	// 기본 강아지 단일 조회

	// 기본 강아지 이름 수정

	// 기본 강아지 애칭 수정
}

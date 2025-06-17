package com.ohgiraffers.togedaeng.backend.domain.dog.service;

import org.springframework.stereotype.Service;

import com.ohgiraffers.togedaeng.backend.domain.dog.repository.DogRepository;

@Service
public class DogService {

	private final DogRepository dogRepository;

	public DogService(DogRepository dogRepository) {
		this.dogRepository = dogRepository;
	}

	// 기본 강아지 등록

	// 기본 강아지 전체 조회

	// 기본 강아지 단일 조회

	// 기본 강아지 이름 수정

	// 기본 강아지 애칭 수정
}

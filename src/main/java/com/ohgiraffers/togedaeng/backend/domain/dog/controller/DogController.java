package com.ohgiraffers.togedaeng.backend.domain.dog.controller;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.ohgiraffers.togedaeng.backend.domain.dog.service.DogService;

@RestController
@RequestMapping("/api/dog")
public class DogController {

	private final DogService dogService;

	public DogController(DogService dogService) {
		this.dogService = dogService;
	}

	// 기본 강아지 등록

	// 기본 강아지 전체 조회

	// 기본 강아지 단일 조회

	// 기본 강아지 이름 수정

	// 기본 강아지 애칭 수정
}

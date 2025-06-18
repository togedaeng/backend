package com.ohgiraffers.togedaeng.backend.domain.dog.controller;

import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.ohgiraffers.togedaeng.backend.domain.dog.dto.request.DogRequestDto;
import com.ohgiraffers.togedaeng.backend.domain.dog.dto.response.DogResponseDto;
import com.ohgiraffers.togedaeng.backend.domain.dog.service.DogService;

@RestController
@RequestMapping("/api/dog")
public class DogController {
	Logger log = LoggerFactory.getLogger(DogController.class);

	private final DogService dogService;

	public DogController(DogService dogService) {
		this.dogService = dogService;
	}

	// 기본 강아지 등록 (커스텀 강아지 등록은 커스텀에다가 따로 추가하기)
	@PostMapping("/create")
	public ResponseEntity<DogResponseDto> createDog(@RequestBody DogRequestDto dogRequestDto) {
		log.info("Create dog request: {}", dogRequestDto);
		DogResponseDto dogResponseDto = dogService.createDog(dogRequestDto);
		return new ResponseEntity<>(dogResponseDto, HttpStatus.CREATED);
	}

	// 기본 강아지 전체 조회
	@GetMapping("/all")
	public ResponseEntity<List<DogResponseDto>> getAllDogs() {
		log.info("Get all dogs");
		List<DogResponseDto> dogs = dogService.getAllDogs();
		return new ResponseEntity<>(dogs, HttpStatus.OK);
	}

	// 기본 강아지 단일 조회
	@GetMapping("/{id}")
	public ResponseEntity<DogResponseDto> getDogById(@PathVariable("id") Long id) {
		log.info("Get dog by id: {}", id);
		DogResponseDto dog = dogService.getDogById(id);
		return new ResponseEntity<>(dog, HttpStatus.OK);
	}

	// 기본 강아지 이름 수정

	// 기본 강아지 애칭 수정

	// 강아지 삭제 - INACTIVE로 상태 변경 후 삭제 날짜 갱신
}

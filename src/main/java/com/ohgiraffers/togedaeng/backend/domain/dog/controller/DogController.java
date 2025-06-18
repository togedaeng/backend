package com.ohgiraffers.togedaeng.backend.domain.dog.controller;

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

import com.ohgiraffers.togedaeng.backend.domain.dog.dto.request.DeleteDogRequestDto;
import com.ohgiraffers.togedaeng.backend.domain.dog.dto.request.DogRequestDto;
import com.ohgiraffers.togedaeng.backend.domain.dog.dto.request.UpdateDogNameRequestDto;
import com.ohgiraffers.togedaeng.backend.domain.dog.dto.response.DeleteDogResponseDto;
import com.ohgiraffers.togedaeng.backend.domain.dog.dto.response.DogResponseDto;
import com.ohgiraffers.togedaeng.backend.domain.dog.dto.response.UpdateDogNameResponseDto;
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

	/**
	 * 📍 기본 강아지 등록
	 * @param dogRequestDto 강아지 등록 DTO
	 * @return 등록된 강아지 정보
	 */
	@PostMapping("/create")
	public ResponseEntity<DogResponseDto> createDog(@RequestBody DogRequestDto dogRequestDto) {
		log.info("Create dog request: {}", dogRequestDto);
		DogResponseDto dogResponseDto = dogService.createDog(dogRequestDto);
		return new ResponseEntity<>(dogResponseDto, HttpStatus.CREATED);
	}

	/**
	 * 📍 강아지 전체 조회
	 * @return 모든 강아지 리스트
	 */
	@GetMapping
	public ResponseEntity<List<DogResponseDto>> getAllDogs() {
		log.info("Get all dogs");
		List<DogResponseDto> dogs = dogService.getAllDogs();
		return new ResponseEntity<>(dogs, HttpStatus.OK);
	}

	/**
	 * 📍 강아지 단일 조회
	 * @param id 강아지 id
	 * @return 강아지 정보
	 */
	@GetMapping("/{id}")
	public ResponseEntity<DogResponseDto> getDogById(@PathVariable("id") Long id) {
		log.info("Get dog by id: {}", id);
		DogResponseDto dog = dogService.getDogById(id);
		return new ResponseEntity<>(dog, HttpStatus.OK);
	}

	// 강아지 이름 수정
	@PatchMapping("/{id}/name")
	public ResponseEntity<UpdateDogNameResponseDto> updateDogName(@PathVariable("id") Long id,
		@RequestBody UpdateDogNameRequestDto updateDogNameRequestDto) {
		log.info("Update dog name: {}", updateDogNameRequestDto.getNewName());
		UpdateDogNameResponseDto dog = dogService.updateDogName(id, updateDogNameRequestDto);
		return new ResponseEntity<>(dog, HttpStatus.OK);
	}

	// 강아지 (나를 부르는)애칭 수정

	// 강아지 삭제 - INACTIVE로 상태 변경 후 삭제 날짜 갱신
	@PatchMapping("/{id}/status")
	public ResponseEntity<DeleteDogResponseDto> deleteDog(@RequestBody DeleteDogRequestDto deleteDogRequestDto) {
		log.info("Delete dog: {}", deleteDogRequestDto);

	}
}

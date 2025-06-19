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
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.ohgiraffers.togedaeng.backend.domain.dog.dto.request.CreateDogRequestDto;
import com.ohgiraffers.togedaeng.backend.domain.dog.dto.request.DeleteDogRequestDto;
import com.ohgiraffers.togedaeng.backend.domain.dog.dto.request.UpdateDogCallNameRequestDto;
import com.ohgiraffers.togedaeng.backend.domain.dog.dto.request.UpdateDogNameRequestDto;
import com.ohgiraffers.togedaeng.backend.domain.dog.dto.request.UpdateDogPersonalityRequestDto;
import com.ohgiraffers.togedaeng.backend.domain.dog.dto.response.CreateDogResponseDto;
import com.ohgiraffers.togedaeng.backend.domain.dog.dto.response.DeleteDogResponseDto;
import com.ohgiraffers.togedaeng.backend.domain.dog.dto.response.DogResponseDto;
import com.ohgiraffers.togedaeng.backend.domain.dog.dto.response.UpdateDogCallNameResponseDto;
import com.ohgiraffers.togedaeng.backend.domain.dog.dto.response.UpdateDogIsMainResponseDto;
import com.ohgiraffers.togedaeng.backend.domain.dog.dto.response.UpdateDogNameResponseDto;
import com.ohgiraffers.togedaeng.backend.domain.dog.dto.response.UpdateDogPersonalityResponseDto;
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
	public ResponseEntity<CreateDogResponseDto> createDog(@RequestBody CreateDogRequestDto dogRequestDto) {
		log.info("Create dog request: {}", dogRequestDto);
		CreateDogResponseDto createDogResponseDto = dogService.createDog(dogRequestDto);
		return new ResponseEntity<>(createDogResponseDto, HttpStatus.CREATED);
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

	/**
	 * 📍 강아지 이름 수정
	 * @param id 강아지 id
	 * @param updateDogNameRequestDto 강아지 id, 수정할 이름
	 * @return 수정된 강아지 이름 정보 (id, 수정된 이름, 수정 시각)
	 */
	@PatchMapping("/{id}/name")
	public ResponseEntity<UpdateDogNameResponseDto> updateDogName(@PathVariable("id") Long id,
		@RequestBody UpdateDogNameRequestDto updateDogNameRequestDto) {
		log.info("Update dog name: {}", updateDogNameRequestDto.getNewName());
		UpdateDogNameResponseDto dog = dogService.updateDogName(id, updateDogNameRequestDto);
		return new ResponseEntity<>(dog, HttpStatus.OK);
	}

	/**
	 * 📍 주인 애칭 수정
	 * @param id 강아지 id
	 * @param updateDogCallNameRequestDto 강아지 id, 수정할 주인 애칭
	 * @return 수정된 강아지 애칭 정보 (id, 수정된 애칭, 수정 시각)
	 */
	@PatchMapping("/{id}/call-name")
	public ResponseEntity<UpdateDogCallNameResponseDto> updateDogCallName(@PathVariable("id") Long id,
		@RequestBody UpdateDogCallNameRequestDto updateDogCallNameRequestDto) {
		log.info("Update call name: {}", updateDogCallNameRequestDto.getNewCallName());
		UpdateDogCallNameResponseDto dog = dogService.updateDogCallName(id, updateDogCallNameRequestDto);
		return new ResponseEntity<>(dog, HttpStatus.OK);
	}

	/**
	 * 📍 강아지 성격 수정
	 * @param id 강아지 id
	 * @param updateDogPersonalityRequestDto 강아지 id, 바꿀 성격 id 1, 바꿀 성격 id 2
	 * @return 수정된 강아지 성격 정보 (id, 성격 조합 id, 바뀐 성격 이름, 수정 일자)
	 */
	@PatchMapping("/{id}/personality")
	public ResponseEntity<UpdateDogPersonalityResponseDto> updateDogPersonality(@PathVariable("id") Long id,
		@RequestBody UpdateDogPersonalityRequestDto updateDogPersonalityRequestDto) {
		log.info("Update dog personality: {}", updateDogPersonalityRequestDto);
		UpdateDogPersonalityResponseDto dog = dogService.updateDogPersonality(id, updateDogPersonalityRequestDto);
		return new ResponseEntity<>(dog, HttpStatus.OK);

	}

	/**
	 * 📍 대표 반려견 설정
	 * @param id 강아지 id
	 * @param userId 유저 id (로그인한 사용자 아이디로 추후 수정 예정)
	 * @return 대표 강아지 정보 (id, 메인 강아지 여부)
	 */
	@PatchMapping("/{id}/main-dog")
	public ResponseEntity<UpdateDogIsMainResponseDto> updateDogIsMain(@PathVariable("id") Long id,
		@RequestParam Long userId) {
		log.info("Update dog main dog: {}", id);
		UpdateDogIsMainResponseDto dog = dogService.updateDogIsMain(id, userId);
		return new ResponseEntity<>(dog, HttpStatus.OK);
	}

	/**
	 * 📍 강아지 삭제
	 * 상태 INACTIVE로 변경
	 * @param id 강아지 id
	 * @param deleteDogRequestDto 강아지 id
	 * @return 삭제된 강아지 정보 (id, 이름, 상태(INACTIVE), 삭제일자)
	 */
	@PatchMapping("/{id}/status")
	public ResponseEntity<DeleteDogResponseDto> deleteDog(@PathVariable("id") Long id,
		@RequestBody DeleteDogRequestDto deleteDogRequestDto) {
		log.info("Delete dog: {}", deleteDogRequestDto);
		DeleteDogResponseDto dog = dogService.deleteDog(id, deleteDogRequestDto);
		return new ResponseEntity<>(dog, HttpStatus.OK);
	}
}

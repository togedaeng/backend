package com.ohgiraffers.togedaeng.backend.domain.dog.controller;

import java.io.IOException;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import com.ohgiraffers.togedaeng.backend.domain.dog.dto.request.CreateDogRequestDto;
import com.ohgiraffers.togedaeng.backend.domain.dog.dto.request.UpdateDogCallNameRequestDto;
import com.ohgiraffers.togedaeng.backend.domain.dog.dto.request.UpdateDogNameRequestDto;
import com.ohgiraffers.togedaeng.backend.domain.dog.dto.request.UpdateDogPersonalityRequestDto;
import com.ohgiraffers.togedaeng.backend.domain.dog.dto.request.UpdateDogStatusActiveRequestDto;
import com.ohgiraffers.togedaeng.backend.domain.dog.dto.request.UpdateDogStatusRequestDto;
import com.ohgiraffers.togedaeng.backend.domain.dog.dto.response.CreateDogResponseDto;
import com.ohgiraffers.togedaeng.backend.domain.dog.dto.response.DogResponseDto;
import com.ohgiraffers.togedaeng.backend.domain.dog.dto.response.UpdateDogCallNameResponseDto;
import com.ohgiraffers.togedaeng.backend.domain.dog.dto.response.UpdateDogNameResponseDto;
import com.ohgiraffers.togedaeng.backend.domain.dog.dto.response.UpdateDogPersonalityResponseDto;
import com.ohgiraffers.togedaeng.backend.domain.dog.dto.response.UpdateDogStatusActiveResponseDto;
import com.ohgiraffers.togedaeng.backend.domain.dog.dto.response.UpdateDogStatusResponseDto;
import com.ohgiraffers.togedaeng.backend.domain.dog.service.DogService;
import com.ohgiraffers.togedaeng.backend.domain.notification.service.SlackNotificationService;
import com.ohgiraffers.togedaeng.backend.global.auth.service.JwtExtractor;

import jakarta.servlet.http.HttpServletRequest;

@RestController
@RequestMapping("/api/dog")
public class DogController {
	Logger log = LoggerFactory.getLogger(DogController.class);

	private final DogService dogService;
	private final SlackNotificationService slackNotificationService;
	private final JwtExtractor jwtExtractor;

	public DogController(DogService dogService, SlackNotificationService slackNotificationService,
		JwtExtractor jwtExtractor) {
		this.dogService = dogService;
		this.slackNotificationService = slackNotificationService;
		this.jwtExtractor = jwtExtractor;
	}

	/**
	 * 📍 강아지 등록 (커스텀 요청)
	 * @param dogRequestDto 강아지 등록 DTO
	 * @return 등록된 강아지 정보
	 */
	@PostMapping(value = "/create", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
	public ResponseEntity<CreateDogResponseDto> createDog(@RequestPart("dog") CreateDogRequestDto dogRequestDto,
		@RequestPart("mainImage") MultipartFile mainImage,
		@RequestPart("subImages") List<MultipartFile> subImages,
		HttpServletRequest request) {
		log.info("Create dog request: {}", dogRequestDto);
		Long userId = jwtExtractor.extractUserId(request);
		dogRequestDto.setUserId(userId);

		CreateDogResponseDto createDogResponseDto = dogService.createDog(dogRequestDto, mainImage, subImages);
		// DB에 강아지 등록이 되었을 경우 슬랙에 알림 전송
		slackNotificationService.sendSlackNotification(createDogResponseDto);
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
	 * 📍 요청 뱓은 강아지 전체 조회
	 * @return 요청 상태의 강아지 리스트
	 */
	@GetMapping("/requested")
	public ResponseEntity<List<DogResponseDto>> getRequestedDogs() {
		log.info("Get all waiting dogs");
		List<DogResponseDto> dogs = dogService.getRequestedDogs();
		return new ResponseEntity<>(dogs, HttpStatus.OK);
	}

	/**
	 * 📍 강아지 상태 변경 (REJECTED, HOLD, INACTIVE)
	 * @param id 강아지 ID
	 * @param updateDogStatusRequestDto 유저 ID, 수정할 상태
	 * @return 수정된 강아지 정보 (강아지 ID, 강아지 상태, 수정 시각)
	 */
	@PatchMapping("/{id}/status")
	public ResponseEntity<UpdateDogStatusResponseDto> updateDogStatus(@PathVariable("id") Long id,
		@RequestBody UpdateDogStatusRequestDto updateDogStatusRequestDto,
		HttpServletRequest request) {
		log.info("Update dog status: {}", updateDogStatusRequestDto);
		Long userId = jwtExtractor.extractUserId(request);
		updateDogStatusRequestDto.setUserId(userId);

		UpdateDogStatusResponseDto dog = dogService.updateDogStatus(id, updateDogStatusRequestDto);
		return new ResponseEntity<>(dog, HttpStatus.OK);
	}

	// 강아지 렌더링 완료 및 상태 변경
	@PatchMapping("/{id}/status/active")
	public ResponseEntity<UpdateDogStatusActiveResponseDto> updateDogStatusActive(@PathVariable("id") Long id,
		@ModelAttribute UpdateDogStatusActiveRequestDto updateDogStatusActiveRequestDto,
		HttpServletRequest request) throws IOException {

		log.info("Update dog status Active: {}", updateDogStatusActiveRequestDto);
		Long userId = jwtExtractor.extractUserId(request);
		updateDogStatusActiveRequestDto.setUserId(userId);

		UpdateDogStatusActiveResponseDto dog = dogService.updateDogStatusActive(id, updateDogStatusActiveRequestDto);
		return new ResponseEntity<>(dog, HttpStatus.OK);
	}

	/**
	 * 📍 강아지 단일 조회
	 * @param id 강아지 ID
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
	 * @param id 강아지 ID
	 * @param updateDogNameRequestDto 유저 ID, 수정할 이름
	 * @return 수정된 강아지 이름 정보 (강아지 ID, 수정된 이름, 수정 시각)
	 */
	@PatchMapping("/{id}/name")
	public ResponseEntity<UpdateDogNameResponseDto> updateDogName(@PathVariable("id") Long id,
		@RequestBody UpdateDogNameRequestDto updateDogNameRequestDto,
		HttpServletRequest request) {
		log.info("Update dog name: {}", updateDogNameRequestDto.getNewName());
		Long userId = jwtExtractor.extractUserId(request);
		updateDogNameRequestDto.setUserId(userId);

		UpdateDogNameResponseDto dog = dogService.updateDogName(id, updateDogNameRequestDto);
		return new ResponseEntity<>(dog, HttpStatus.OK);
	}

	/**
	 * 📍 주인 애칭 수정
	 * @param id 강아지 ID
	 * @param updateDogCallNameRequestDto 유저 ID, 수정할 주인 애칭
	 * @return 수정된 강아지 애칭 정보 (강아지 ID, 수정된 애칭, 수정 시각)
	 */
	@PatchMapping("/{id}/call-name")
	public ResponseEntity<UpdateDogCallNameResponseDto> updateDogCallName(@PathVariable("id") Long id,
		@RequestBody UpdateDogCallNameRequestDto updateDogCallNameRequestDto,
		HttpServletRequest request) {
		log.info("Update call name: {}", updateDogCallNameRequestDto.getNewCallName());
		Long userId = jwtExtractor.extractUserId(request);
		updateDogCallNameRequestDto.setUserId(userId);

		UpdateDogCallNameResponseDto dog = dogService.updateDogCallName(id, updateDogCallNameRequestDto);
		return new ResponseEntity<>(dog, HttpStatus.OK);
	}

	/**
	 * 📍 강아지 성격 수정 (X)
	 * @param id 강아지 id
	 * @param updateDogPersonalityRequestDto 강아지 ID, 바꿀 성격 ID 1, 바꿀 성격 ID 2
	 * @return 수정된 강아지 성격 정보 (강아지 ID, 성격 조합 ID, 바뀐 성격 이름, 수정 일자)
	 */
	@PatchMapping("/{id}/personality")
	public ResponseEntity<UpdateDogPersonalityResponseDto> updateDogPersonality(@PathVariable("id") Long id,
		@RequestBody UpdateDogPersonalityRequestDto updateDogPersonalityRequestDto,
		HttpServletRequest request) {
		log.info("Update dog personality: {}", updateDogPersonalityRequestDto);
		Long userId = jwtExtractor.extractUserId(request);
		updateDogPersonalityRequestDto.setUserId(userId);

		UpdateDogPersonalityResponseDto dog = dogService.updateDogPersonality(id, updateDogPersonalityRequestDto);
		return new ResponseEntity<>(dog, HttpStatus.OK);

	}
}

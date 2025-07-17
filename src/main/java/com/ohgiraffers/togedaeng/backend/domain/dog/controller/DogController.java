package com.ohgiraffers.togedaeng.backend.domain.dog.controller;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.multipart.MultipartFile;

import com.ohgiraffers.togedaeng.backend.domain.dog.dto.request.CreateDogRequestDto;
import com.ohgiraffers.togedaeng.backend.domain.dog.dto.request.UpdateDogCallNameRequestDto;
import com.ohgiraffers.togedaeng.backend.domain.dog.dto.request.UpdateDogNameRequestDto;
import com.ohgiraffers.togedaeng.backend.domain.dog.dto.response.CreateDogResponseDto;
import com.ohgiraffers.togedaeng.backend.domain.dog.dto.response.UpdateDogCallNameResponseDto;
import com.ohgiraffers.togedaeng.backend.domain.dog.dto.response.UpdateDogNameResponseDto;
import com.ohgiraffers.togedaeng.backend.domain.dog.dto.response.DogListResponseDto;
import com.ohgiraffers.togedaeng.backend.domain.dog.dto.response.DogDetailResponseDto;
import com.ohgiraffers.togedaeng.backend.domain.dog.service.DogService;
import com.ohgiraffers.togedaeng.backend.domain.custom.service.CustomService;
import com.ohgiraffers.togedaeng.backend.domain.notification.service.SlackNotificationService;
import com.ohgiraffers.togedaeng.backend.global.auth.service.JwtExtractor;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import java.util.List;

@RestController
@RequestMapping("/api/dog")
@RequiredArgsConstructor
public class DogController {

	private static final Logger log = LoggerFactory.getLogger(DogController.class);

	private final DogService dogService;
	private final CustomService customService;
	private final JwtExtractor jwtExtractor;
	private final SlackNotificationService slackNotificationService;

	/**
	 * 📍 강아지 등록 및 커스텀 메인 이미지 업로드 API
	 * - 사용자 인증 정보를 통해 userId 추출
	 * - 강아지를 등록하고, 메인 이미지와 함께 커스텀 요청 생성
	 *
	 * 요청 방식: multipart/form-data
	 * 요청 경로: POST /api/dogs/create-main
	 *
	 * @param createDogRequestDto 강아지 등록 요청 DTO (메인 이미지 포함)
	 * @param request             HttpServletRequest (JWT 토큰에서 userId 추출용)
	 * @return 생성된 커스텀 요청 ID (customId)
	 */
	@PostMapping("/create")
	public ResponseEntity<Long> createDogMain(
		@ModelAttribute @Valid CreateDogRequestDto createDogRequestDto,
		HttpServletRequest request) {
		log.info("🐶 [강아지 등록 및 메인 업로드] POST /api/dogs/create-main 요청 수신");

		try {
			Long userId = jwtExtractor.extractUserId(request);
			log.debug("➡️  userId 추출 완료: {}", userId);

			CreateDogResponseDto responseDto = dogService.createDogInfo(createDogRequestDto, userId);
			log.debug("✅ 강아지 저장 완료 - dogId: {}", responseDto.getId());

			Long customId = customService.uploadMainImage(responseDto.getId(), createDogRequestDto.getMainImage());
			log.info("📦 커스텀 메인 이미지 업로드 완료 - customId: {}", customId);

			// ✅ Slack 알림 전송 (커스텀 요청시)
			slackNotificationService.sendSlackNotification(responseDto);

			return ResponseEntity.ok(customId);

		} catch (Exception e) {
			log.error("❌ 강아지 등록 및 메인 업로드 중 오류 발생: {}", e.getMessage(), e);
			return ResponseEntity.status(500).build();
		}
	}

	/**
	 * 📍 커스텀 요청의 서브 이미지 업로드 API
	 * - 기존 생성된 커스텀 요청 ID를 통해 서브 이미지 추가 업로드
	 *
	 * 요청 방식: multipart/form-data
	 * 요청 경로: POST /api/dogs/{customId}/upload-sub
	 *
	 * @param customId 커스텀 요청 ID
	 * @param subImages 서브 이미지 목록 (최대 3장)
	 * @return 업로드 성공 시 200 OK, 실패 시 500 서버 에러
	 */
	@PostMapping("/{customId}/sub-image")
	public ResponseEntity<Void> uploadSubImages(
		@PathVariable Long customId,
		@RequestParam("subImages") List<MultipartFile> subImages) {
		log.info("📦 [서브 이미지 업로드] POST /api/dogs/{}/upload-sub 요청 수신", customId);

		try {
			customService.uploadSubImages(customId, subImages);
			log.info("✅ 서브 이미지 업로드 완료 - customId: {}", customId);
			return ResponseEntity.ok().build();

		} catch (Exception e) {
			log.error("❌ 서브 이미지 업로드 중 오류 발생: {}", e.getMessage(), e);
			return ResponseEntity.status(500).build();
		}
	}

	/**
	 * 📍 강아지 전체 조회 API (페이지네이션)
	 * - 모든 강아지 정보를 페이지네이션으로 반환한다.
	 * - 기본 페이지 크기는 8개이며, 사용자가 지정할 수 있다.
	 *
	 * - 요청 방식: GET
	 * - 요청 경로: /api/dog
	 *
	 * @param page 페이지 번호 (기본값: 0)
	 * @param size 페이지 크기 (기본값: 8)
	 * @return 페이지네이션된 강아지 리스트 (DogListResponseDto)
	 */
	@GetMapping
	public ResponseEntity<List<DogListResponseDto>> getAllDogs(
			@RequestParam(defaultValue = "0") int page,
			@RequestParam(defaultValue = "8") int size) {
		log.info("🔍 강아지 전체 조회 요청 - page: {}, size: {}", page, size);

		try {
			List<DogListResponseDto> result = dogService.getAllDogs(page, size);
			log.info("✅ 강아지 전체 조회 성공 - count: {}", result.size());
			return ResponseEntity.ok(result);
		} catch (IllegalArgumentException e) {
			log.warn("⚠️ 강아지 전체 조회 실패 - {}", e.getMessage());
			return ResponseEntity.badRequest().build();
		} catch (Exception e) {
			log.error("❌ 강아지 전체 조회 중 서버 오류", e);
			return ResponseEntity.status(500).build();
		}
	}

	/**
	 * 📍 강아지 단일 상세 조회 API
	 * - 특정 강아지의 상세 정보를 반환한다.
	 *
	 * - 요청 방식: GET
	 * - 요청 경로: /api/dog/{id}
	 *
	 * @param dogId 조회할 강아지 ID (PathVariable)
	 * @return 강아지 상세 정보 (DogDetailResponseDto)
	 */
	@GetMapping("/{id}")
	public ResponseEntity<DogDetailResponseDto> getDogById(@PathVariable("id") Long dogId) {
		log.info("🔍 강아지 단일 상세 조회 요청 - dogId: {}", dogId);

		try {
			DogDetailResponseDto result = dogService.getDogById(dogId);
			log.info("✅ 강아지 단일 상세 조회 성공 - dogId: {}", dogId);
			return ResponseEntity.ok(result);
		} catch (IllegalArgumentException e) {
			log.warn("⚠️ 강아지 단일 상세 조회 실패 - {}", e.getMessage());
			return ResponseEntity.badRequest().build();
		} catch (Exception e) {
			log.error("❌ 강아지 단일 상세 조회 중 서버 오류 - dogId: {}", dogId, e);
			return ResponseEntity.status(500).build();
		}
	}

	/**
	 * 📍 강아지 이름 수정 API
	 * - 사용자 인증 정보를 통해 userId 추출 후 권한 검증
	 * - 본인 소유의 강아지인 경우에만 이름 수정 가능
	 * - 수정 성공 시 수정된 이름과 강아지 ID를 반환
	 *
	 * 요청 방식: PATCH
	 * 요청 경로: /api/dog/{id}/name
	 *
	 * @param id         경로 변수로 전달받는 강아지 ID
	 * @param requestDto 강아지 이름 수정 요청 DTO (newName 포함)
	 * @param request    HttpServletRequest (JWT 토큰에서 userId 추출용)
	 * @return 수정 성공 시 200 OK와 UpdateDogNameResponseDto 반환,
	 *         요청 데이터 오류 시 400 Bad Request,
	 *         권한 없음 시 403 Forbidden,
	 *         서버 오류 시 500 Internal Server Error 반환
	 */
	@PatchMapping("/{id}/name")
	public ResponseEntity<UpdateDogNameResponseDto> updateDogName(
			@PathVariable("id") Long id,
			@RequestBody UpdateDogNameRequestDto requestDto,
			HttpServletRequest request) {
		log.info("🐶 [강아지 이름 수정] PATCH /api/dog/{id}/name 요청 수신");

		try {
			Long userId = jwtExtractor.extractUserId(request);

			UpdateDogNameResponseDto responseDto = dogService.updateDogName(id, requestDto, userId);
			log.info("✅ 강아지 이름 수정 성공 - dogId: {}, newName: {}", id, responseDto.getUpdatedName());
			return ResponseEntity.ok(responseDto);
		} catch (IllegalArgumentException e) {
			log.warn("⚠️ 강아지 이름 수정 실패 - {}", e.getMessage());
			return ResponseEntity.badRequest().build();
		} catch (SecurityException e) {
			log.warn("🚫 권한 없음 - {}", e.getMessage());
			return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
		} catch (Exception e) {
			log.error("❌ 강아지 이름 수정 중 예외 발생", e);
			return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
		}
	}

	/**
	 * 📍 강아지 애칭 수정 API
	 * - 강아지 ID와 요청 사용자 ID로 소유자 검증
	 * - 애칭 수정 성공 시 200 OK 반환
	 * - 요청 사용자 권한 없음 시 403 Forbidden, 실패 시 400 Bad Request 또는 500
	 *
	 * 요청 방식: PATCH
	 * 요청 경로: /api/dogs/{id}/call-name
	 *
	 * @param id         경로 변수로 전달받는 강아지 ID
	 * @param requestDto 애칭 수정 요청 DTO
	 * @param request    HttpServletRequest (JWT 토큰에서 userId 추출)
	 * @return 수정 결과 ResponseEntity
	 */
	@PatchMapping("/{id}/call-name")
	public ResponseEntity<UpdateDogCallNameResponseDto> updateDogCallName(
			@PathVariable("id") Long id,
			@RequestBody UpdateDogCallNameRequestDto requestDto,
			HttpServletRequest request) {
		log.info("🐶 [강아지 애칭 수정] PATCH /api/dogs/{}/call-name 요청 수신", id);

		try {
			Long userId = jwtExtractor.extractUserId(request);
			UpdateDogCallNameResponseDto responseDto = dogService.updateDogCallName(id, requestDto, userId);

			log.info("✅ 강아지 애칭 수정 성공 - dogId: {}, updatedCallName: {}", id, responseDto.getUpdatedCallName());
			return ResponseEntity.ok(responseDto);

		} catch (IllegalArgumentException e) {
			log.warn("⚠️ 강아지 애칭 수정 실패 - {}", e.getMessage());
			return ResponseEntity.badRequest().build();
		} catch (SecurityException e) {
			log.warn("🚫 권한 없음 - {}", e.getMessage());
			return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
		} catch (Exception e) {
			log.error("❌ 강아지 애칭 수정 중 예외 발생", e);
			return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
		}
	}
}

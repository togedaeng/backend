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
import org.springframework.web.bind.annotation.RestController;

import com.ohgiraffers.togedaeng.backend.domain.dog.dto.request.CreateDogRequestDto;
import com.ohgiraffers.togedaeng.backend.domain.dog.dto.request.UpdateDogCallNameRequestDto;
import com.ohgiraffers.togedaeng.backend.domain.dog.dto.request.UpdateDogNameRequestDto;
import com.ohgiraffers.togedaeng.backend.domain.dog.dto.response.CreateDogResponseDto;
import com.ohgiraffers.togedaeng.backend.domain.dog.dto.response.UpdateDogCallNameResponseDto;
import com.ohgiraffers.togedaeng.backend.domain.dog.dto.response.UpdateDogNameResponseDto;
import com.ohgiraffers.togedaeng.backend.domain.dog.service.DogService;
import com.ohgiraffers.togedaeng.backend.domain.custom.service.CustomService;
import com.ohgiraffers.togedaeng.backend.global.auth.service.JwtExtractor;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/dog")
@RequiredArgsConstructor
public class DogController {

	Logger log = LoggerFactory.getLogger(DogController.class);

	private final DogService dogService;
	private final CustomService customService;
	private final JwtExtractor jwtExtractor;

	/**
	 * 📍 강아지 등록 및 커스텀 요청 생성 API
	 * - 사용자 인증 정보를 통해 userId 추출
	 * - 강아지를 등록하고, 해당 강아지 ID를 기반으로 커스텀 요청까지 함께 생성
	 *
	 * 요청 방식: multipart/form-data
	 * 요청 경로: POST /api/dogs/create
	 *
	 * @param createDogRequestDto 강아지 등록 요청 DTO (이미지 포함)
	 * @param request HttpServletRequest (JWT 토큰에서 userId 추출용)
	 * @return 등록 성공 시 200 OK, 실패 시 500 서버 에러
	 */
	@PostMapping("/create")
	public ResponseEntity<CreateDogResponseDto> createDog(
		@ModelAttribute @Valid CreateDogRequestDto createDogRequestDto,
		HttpServletRequest request) {
		log.info("🐶 [강아지 등록] POST /api/dog/create 요청 수신");

		try {
			Long userId = jwtExtractor.extractUserId(request);
			log.debug("➡️  userId 추출 완료: {}", userId);

			CreateDogResponseDto responseDto = dogService.createDogInfo(createDogRequestDto, userId);
			log.debug("✅ 강아지 저장 완료 - dogId: {}", responseDto);

			customService.createCustomRequest(responseDto.getId(), createDogRequestDto);
			log.info("📦 커스텀 요청 생성 완료 - dogId: {}", responseDto.getId());

			return ResponseEntity.ok().build();

		} catch (Exception e) {
			log.error("❌ 강아지 등록 중 오류 발생: {}", e.getMessage(), e);
			return ResponseEntity.status(500).build();
		}
	}


	// 사용자별 강아지 조회

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
		HttpServletRequest request
	) {
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
	 * @param id        경로 변수로 전달받는 강아지 ID
	 * @param requestDto 애칭 수정 요청 DTO
	 * @param request   HttpServletRequest (JWT 토큰에서 userId 추출)
	 * @return 수정 결과 ResponseEntity
	 */
	@PatchMapping("/{id}/call-name")
	public ResponseEntity<UpdateDogCallNameResponseDto> updateDogCallName(
		@PathVariable("id") Long id,
		@RequestBody UpdateDogCallNameRequestDto requestDto,
		HttpServletRequest request
	) {
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

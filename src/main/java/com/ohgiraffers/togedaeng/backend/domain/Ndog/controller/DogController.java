package com.ohgiraffers.togedaeng.backend.domain.Ndog.controller;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.ohgiraffers.togedaeng.backend.domain.Ndog.dto.request.CreateDogRequestDto;
import com.ohgiraffers.togedaeng.backend.domain.Ndog.dto.response.CreateDogResponseDto;
import com.ohgiraffers.togedaeng.backend.domain.Ndog.service.DogService;
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
		log.info("🐶 [강아지 등록] POST /api/dogs/register 요청 수신");

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

	// 강아지 전체 조회

	// 사용자별 강아지 조회

	// 강아지 정보 수정 (이름, 애칭)

	// 강아지 상태 변경 -> 이건 커스텀 요청에 따라 달라지지 직접 상태를 변경하는 일은 없을듯?
}

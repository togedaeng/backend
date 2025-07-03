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

	// 강아지 등록
	@PostMapping("/create")
	public ResponseEntity<CreateDogRequestDto> createDog(
		@ModelAttribute @Valid CreateDogRequestDto createDogRequestDto,
		HttpServletRequest request) {
		log.info("🐶 [강아지 등록] POST /api/dogs/register 요청 수신");

		try {
			Long userId = jwtExtractor.extractUserId(request);
			log.debug("➡️  userId 추출 완료: {}", userId);

			Long dogId = dogService.createDogInfo(createDogRequestDto, userId);
			log.debug("✅ 강아지 저장 완료 - dogId: {}", dogId);

			customService.createCustomRequest(dogId, createDogRequestDto);
			log.info("📦 커스텀 요청 생성 완료 - dogId: {}", dogId);

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

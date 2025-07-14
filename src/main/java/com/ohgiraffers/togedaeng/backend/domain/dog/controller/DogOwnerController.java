package com.ohgiraffers.togedaeng.backend.domain.dog.controller;

import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.ohgiraffers.togedaeng.backend.domain.dog.dto.response.DogOwnerResponseDto;
import com.ohgiraffers.togedaeng.backend.domain.dog.service.DogOwnerService;

@RestController
@RequestMapping("/api/owner")
public class DogOwnerController {
	private static final Logger log = LoggerFactory.getLogger(DogOwnerController.class);
	private final DogOwnerService dogOwnerService;

	@Autowired
	public DogOwnerController(DogOwnerService dogOwnerService) {
		this.dogOwnerService = dogOwnerService;
	}

	@GetMapping("/{dogId}")
	public ResponseEntity<List<DogOwnerResponseDto>> getDogOwners(@PathVariable Long dogId) {
		log.info("반려견 별 등록된 사용자 목록 전체 조회 (dogId) : {}", dogId);

		try {
			List<DogOwnerResponseDto> dogOwnerResponseDtos = dogOwnerService.getDogOwners(dogId);
			return ResponseEntity.ok(dogOwnerResponseDtos);
		} catch (IllegalArgumentException e) {
			log.warn("⚠️ 반려견 주인 전체 조회 실패 - {}", e.getMessage());
			return ResponseEntity.badRequest().build();
		} catch (Exception e) {
			log.error("❌  반려견 주인 전체 조회 중 서버 오류", e);
			return ResponseEntity.status(500).build();
		}
	}

}

package com.ohgiraffers.togedaeng.backend.domain.inquiry.controller;

import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.ohgiraffers.togedaeng.backend.domain.inquiry.dto.InquiryListResponseDto;
import com.ohgiraffers.togedaeng.backend.domain.inquiry.service.InquiryService;

import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@RestController
@RequestMapping("/api/inquiry")
public class InquiryController {

	private final Logger log = LoggerFactory.getLogger(InquiryController.class);
	private final InquiryService inquiryService;

	/**
	 * 📍 문의 전체 조회 API (페이지네이션)
	 * - 모든 문의 정보를 페이지네이션으로 반환한다.
	 *
	 * - 요청 방식: GET
	 * - 요청 경로: /api/inquiries
	 *
	 * @param page 페이지 번호 (기본값: 0)
	 * @param size 페이지 크기 (기본값: 8)
	 * @return 200 OK, 페이지네이션된 문의 리스트
	 */
	@GetMapping
	public ResponseEntity<List<InquiryListResponseDto>> getAllInquiries(
		@RequestParam(defaultValue = "0") int page,
		@RequestParam(defaultValue = "8") int size) {
		log.info("🔍 문의 전체 조회 요청 - page: {}, size: {}", page, size);
		List<InquiryListResponseDto> result = inquiryService.getAllInquiries(page, size);
		log.info("✅ 문의 전체 조회 성공 - count: {}", result.size());
		return ResponseEntity.ok(result);
	}


	// 문의 단일 조회

	// 문의 작성

	// 문의 답변 작성

	// 문의 수정 (답변 안 달렸을 때만)


}

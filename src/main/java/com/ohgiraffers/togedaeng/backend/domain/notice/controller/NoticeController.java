package com.ohgiraffers.togedaeng.backend.domain.notice.controller;

import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.ohgiraffers.togedaeng.backend.domain.dog.dto.response.DogListResponseDto;
import com.ohgiraffers.togedaeng.backend.domain.notice.dto.response.NoticeDetailResponseDto;
import com.ohgiraffers.togedaeng.backend.domain.notice.dto.response.NoticeListResponseDto;
import com.ohgiraffers.togedaeng.backend.domain.notice.service.NoticeService;

import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/notice")
@RequiredArgsConstructor
public class NoticeController {

	private final Logger log = LoggerFactory.getLogger(NoticeController.class);
	private final NoticeService noticeService;

	/**
	 * 📍 공지 전체 조회 API (페이지네이션)
	 * - 모든 공지 정보를 페이지네이션으로 반환한다.
	 * - 기본 페이지 크기는 8개이며, 사용자가 지정할 수 있다.
	 *
	 * - 요청 방식: GET
	 * - 요청 경로: /api/notices (예시 경로, 실제 경로에 맞게 수정하세요)
	 *
	 * @param page 페이지 번호 (기본값: 0)
	 * @param size 페이지 크기 (기본값: 8)
	 * @return 200 OK, 페이지네이션된 공지 리스트 (NoticeListResponseDto)
	 */
	@GetMapping
	public ResponseEntity<List<NoticeListResponseDto>> getAllNotices(
		@RequestParam(defaultValue = "0") int page,
		@RequestParam(defaultValue = "8") int size) {
		log.info("🔍 공지 전체 조회 요청 - page: {}, size: {}", page, size);

		try {
			List<NoticeListResponseDto> result = noticeService.getAllNotices(page, size);
			log.info("✅ 공지 전체 조회 성공 - count: {}", result.size());
			return ResponseEntity.ok(result);
		} catch (IllegalArgumentException e) {
			log.warn("⚠️ 공지 전체 조회 실패 - {}", e.getMessage());
			return ResponseEntity.badRequest().build();
		} catch (Exception e) {
			log.error("❌ 공지 전체 조회 중 서버 오류", e);
			return ResponseEntity.status(500).build();
		}
	}

	/**
	 * 📍 공지 단일 상세 조회 API
	 * - 특정 공지의 상세 정보를 반환한다.
	 *
	 * - 요청 방식: GET
	 * - 요청 경로: /api/notices/{id}
	 *
	 * @param noticeId 조회할 공지 ID (PathVariable)
	 * @return 200 OK, 공지 상세 정보 (NoticeDetailResponseDto)
	 */
	@GetMapping("/{id}")
	public ResponseEntity<NoticeDetailResponseDto> getNoticeById(@PathVariable("id") Long noticeId) {
		log.info("🔍 공지 단일 상세 조회 요청 - noticeId: {}", noticeId);

		try {
			NoticeDetailResponseDto result = noticeService.getNoticeById(noticeId);
			log.info("✅ 공지 단일 상세 조회 성공 - noticeId: {}", noticeId);
			return ResponseEntity.ok(result);
		} catch (IllegalArgumentException e) {
			log.warn("⚠️ 공지 단일 상세 조회 실패 - {}", e.getMessage());
			return ResponseEntity.badRequest().build();
		} catch (Exception e) {
			log.error("❌ 공지 단일 상세 조회 중 서버 오류 - noticeId: {}", noticeId, e);
			return ResponseEntity.status(500).build();
		}
	}

	// 공지 작성

	// 공지 수정

	// 공지 삭제
}

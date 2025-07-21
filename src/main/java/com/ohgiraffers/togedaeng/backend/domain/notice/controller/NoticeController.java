package com.ohgiraffers.togedaeng.backend.domain.notice.controller;

import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import com.ohgiraffers.togedaeng.backend.domain.notice.dto.request.CreateNoticeRequestDto;
import com.ohgiraffers.togedaeng.backend.domain.notice.dto.response.CreateNoticeResponseDto;
import com.ohgiraffers.togedaeng.backend.domain.notice.dto.response.NoticeDetailResponseDto;
import com.ohgiraffers.togedaeng.backend.domain.notice.dto.response.NoticeListResponseDto;
import com.ohgiraffers.togedaeng.backend.domain.notice.service.NoticeService;
import com.ohgiraffers.togedaeng.backend.global.auth.service.JwtExtractor;

import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/notice")
@RequiredArgsConstructor
public class NoticeController {

	private final Logger log = LoggerFactory.getLogger(NoticeController.class);
	private final NoticeService noticeService;
	private final JwtExtractor jwtExtractor;


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

	/**
	 * 📍 공지 작성
	 * - 전달받은 이미지 파일을 S3에 업로드하고, 그 URL을 포함하여 새로운 공지를 생성한다.
	 * - 이미지가 없는 경우에도 공지 등록이 가능하다.
	 *
	 * @param requestDto 공지 생성에 필요한 데이터 (제목, 내용, 카테고리)
	 * @param image      S3에 업로드할 이미지 파일 (null일 수 있음)
	 * @param request     HttpServletRequest (JWT 토큰에서 userId 추출용)
	 * @return 생성된 공지의 상세 정보를 담은 DTO (CreateNoticeResponseDto)
	 * @throws IllegalArgumentException 해당 ID의 사용자가 없을 경우 발생
	 * @throws RuntimeException         S3 이미지 업로드 실패 시 발생
	 */
	@PostMapping(value = "/create", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
	public ResponseEntity<CreateNoticeResponseDto> createNotice(
		@RequestPart("requestDto") CreateNoticeRequestDto requestDto,
		@RequestPart(value = "image", required = false) MultipartFile image,
		HttpServletRequest request) {
		log.info("🚀 [공지 등록 및 S3 업로드] POST /api/custom/create 요청 수신");

		try {
			Long userId = jwtExtractor.extractUserId(request);
			log.debug("➡️  userId 추출 완료: {}", userId);

			CreateNoticeResponseDto responseDto = noticeService.createNotice(requestDto, image, userId);
			log.info("✅ 공지 등록 성공 - noticeId: {}", responseDto.getId());

			return ResponseEntity.status(HttpStatus.CREATED).body(responseDto);

		} catch (IllegalArgumentException e) {
			log.warn("⚠️ 공지 등록 실패 - {}", e.getMessage());
			return ResponseEntity.badRequest().build();
		} catch (Exception e) {
			log.error("❌ 공지 등록 중 서버 오류", e);
			return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
		}
	}

	// 공지 수정

	// 공지 삭제
}

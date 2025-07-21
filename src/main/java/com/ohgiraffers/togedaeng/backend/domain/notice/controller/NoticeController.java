package com.ohgiraffers.togedaeng.backend.domain.notice.controller;

import java.nio.file.AccessDeniedException;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import com.ohgiraffers.togedaeng.backend.domain.notice.dto.request.CreateNoticeRequestDto;
import com.ohgiraffers.togedaeng.backend.domain.notice.dto.request.UpdateNoticeRequestDto;
import com.ohgiraffers.togedaeng.backend.domain.notice.dto.response.CreateNoticeResponseDto;
import com.ohgiraffers.togedaeng.backend.domain.notice.dto.response.DeleteNoticeResponseDto;
import com.ohgiraffers.togedaeng.backend.domain.notice.dto.response.NoticeDetailResponseDto;
import com.ohgiraffers.togedaeng.backend.domain.notice.dto.response.NoticeListResponseDto;
import com.ohgiraffers.togedaeng.backend.domain.notice.dto.response.UpdateNoticeResponseDto;
import com.ohgiraffers.togedaeng.backend.domain.notice.service.NoticeService;
import com.ohgiraffers.togedaeng.backend.domain.user.model.dto.DeleteUserResponseDto;
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
	 *
	 * - 요청 방식: GET
	 * - 요청 경로: /api/notice
	 *
	 * @param page 페이지 번호 (기본값: 0)
	 * @param size 페이지 크기 (기본값: 8)
	 * @return 200 OK, 페이지네이션된 공지 리스트
	 */
	@GetMapping
	public ResponseEntity<List<NoticeListResponseDto>> getAllNotices(
		@RequestParam(defaultValue = "0") int page,
		@RequestParam(defaultValue = "8") int size) {
		log.info("🔍 공지 전체 조회 요청 - page: {}, size: {}", page, size);
		List<NoticeListResponseDto> result = noticeService.getAllNotices(page, size);
		log.info("✅ 공지 전체 조회 성공 - count: {}", result.size());
		return ResponseEntity.ok(result);
	}

	/**
	 * 📍 공지 단일 상세 조회 API
	 * - 특정 공지의 상세 정보를 반환한다. (이미지 리스트 포함)
	 *
	 * - 요청 방식: GET
	 * - 요청 경로: /api/notice/{noticeId}
	 *
	 * @param id 조회할 공지 ID
	 * @return 200 OK, 공지 상세 정보
	 */
	@GetMapping("/{id}")
	public ResponseEntity<NoticeDetailResponseDto> getNoticeById(@PathVariable Long id) {
		log.info("🔍 공지 단일 상세 조회 요청 - noticeId: {}", id);
		try {
			NoticeDetailResponseDto result = noticeService.getNoticeById(id);
			log.info("✅ 공지 단일 상세 조회 성공 - noticeId: {}", id);
			return ResponseEntity.ok(result);
		} catch (IllegalArgumentException e) {
			log.warn("⚠️ 공지 단일 상세 조회 실패 - {}", e.getMessage());
			return ResponseEntity.badRequest().build();
		} catch (Exception e) {
			log.error("❌ 공지 단일 상세 조회 중 서버 오류 - noticeId: {}", id, e);
			return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
		}
	}

	/**
	 * 📍 공지 작성 API
	 * - JSON 데이터와 여러 이미지 파일을 함께 받아 새로운 공지를 등록한다.
	 *
	 * - 요청 방식: POST
	 * - 요청 경로: /api/notice/create
	 *
	 * @param requestDto 공지 내용 데이터 (JSON 형식의 파트)
	 * @param images     업로드할 이미지 파일 리스트 (선택 사항)
	 * @param request    JWT 토큰 추출을 위한 HttpServletRequest
	 * @return 201 Created, 생성된 공지의 상세 정보
	 */
	@PostMapping(value = "/create", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
	public ResponseEntity<CreateNoticeResponseDto> createNotice(
		@RequestPart("requestDto") CreateNoticeRequestDto requestDto,
		@RequestPart(value = "images", required = false) List<MultipartFile> images,
		HttpServletRequest request) {
		log.info("🚀 [공지 등록] POST /api/notice/create 요청 수신");

		try {
			Long userId = jwtExtractor.extractUserId(request);
			log.debug("➡️  userId 추출 완료: {}", userId);

			// 서비스 호출 시 단일 image -> 리스트 images로 변경
			CreateNoticeResponseDto responseDto = noticeService.createNotice(requestDto, images, userId);
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

	/**
	 * 📍 공지 수정 API
	 * - 특정 공지의 내용을 수정한다. (기존 이미지 삭제 및 새 이미지 추가 가능)
	 * - 공지 작성자 또는 관리자만 수정 가능하다.
	 *
	 * - 요청 방식: PUT
	 * - 요청 경로: /api/notice/{noticeId}
	 *
	 * @param id   수정할 공지의 ID
	 * @param requestDto 수정할 내용 및 삭제할 이미지 ID (JSON 형식의 파트)
	 * @param newImages  새로 업로드할 이미지 파일 리스트 (선택 사항)
	 * @param request    JWT 토큰 추출을 위한 HttpServletRequest
	 * @return 200 OK, 수정된 공지의 상세 정보
	 */
	@PatchMapping(value = "/{id}/post", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
	public ResponseEntity<UpdateNoticeResponseDto> updateNotice(
		@PathVariable Long id,
		@RequestPart("requestDto") UpdateNoticeRequestDto requestDto,
		@RequestPart(value = "newImages", required = false) List<MultipartFile> newImages,
		HttpServletRequest request) {
		log.info("🚀 [공지 수정] Patch /api/notice/{}/post 요청 수신", id);

		try {
			Long userId = jwtExtractor.extractUserId(request);
			UpdateNoticeResponseDto responseDto = noticeService.updateNotice(id, requestDto, newImages, userId);
			log.info("✅ 공지 수정 성공 - noticeId: {}", responseDto.getId());
			return ResponseEntity.ok(responseDto);
		} catch (IllegalArgumentException e) {
			log.warn("⚠️ 공지 수정 실패 (잘못된 요청) - {}", e.getMessage());
			return ResponseEntity.badRequest().build();
		} catch (AccessDeniedException e) {
			log.warn("⚠️ 공지 수정 실패 (권한 없음) - {}", e.getMessage());
			return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
		} catch (Exception e) {
			log.error("❌ 공지 수정 중 서버 오류 - noticeId: {}", id, e);
			return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
		}
	}

	// 공지 삭제
	@PatchMapping("/{id}/status")
	public ResponseEntity<DeleteNoticeResponseDto> deleteNotice(
		@PathVariable Long id,
		HttpServletRequest request) {
		log.info("🚀 [공지 삭제] Patch /api/notice/{}/status 요청 수신", id);

		try {
			Long userId = jwtExtractor.extractUserId(request);
			DeleteNoticeResponseDto responseDto = noticeService.deleteNotice(id, userId);
			log.info("✅ 공지 삭제 성공 - id: {}", id);

			return ResponseEntity.ok(responseDto);
		} catch (IllegalArgumentException e) {
			log.warn("⚠️ 공지 삭제 실패 (잘못된 요청) - {}", e.getMessage());
			return ResponseEntity.badRequest().build();
		} catch (AccessDeniedException e) {
			log.warn("⚠️ 공지 삭제 실패 (권한 없음) - {}", e.getMessage());
			return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
		} catch (Exception e) {
			log.error("❌ 공지 삭제 중 서버 오류 - id: {}", id, e);
			return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
		}
	}
}

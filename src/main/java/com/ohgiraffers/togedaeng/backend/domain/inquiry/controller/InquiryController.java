package com.ohgiraffers.togedaeng.backend.domain.inquiry.controller;

import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import com.ohgiraffers.togedaeng.backend.domain.inquiry.dto.request.CreateInquiryAnswerRequestDto;
import com.ohgiraffers.togedaeng.backend.domain.inquiry.dto.request.CreateInquiryRequestDto;
import com.ohgiraffers.togedaeng.backend.domain.inquiry.dto.response.CreateInquiryResponseDto;
import com.ohgiraffers.togedaeng.backend.domain.inquiry.dto.response.InquiryAnswerDto;
import com.ohgiraffers.togedaeng.backend.domain.inquiry.dto.response.InquiryDetailResponseDto;
import com.ohgiraffers.togedaeng.backend.domain.inquiry.dto.response.InquiryListResponseDto;
import com.ohgiraffers.togedaeng.backend.domain.inquiry.service.InquiryService;
import com.ohgiraffers.togedaeng.backend.global.auth.service.JwtExtractor;

import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@RestController
@RequestMapping("/api/inquiry")
public class InquiryController {

	private final Logger log = LoggerFactory.getLogger(InquiryController.class);

	private final InquiryService inquiryService;
	private final JwtExtractor jwtExtractor;

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

	/**
	 * 📍 문의 단일 상세 조회 API
	 * - 특정 문의의 상세 정보를 반환한다. (이미지, 답변 포함)
	 *
	 * - 요청 방식: GET
	 * - 요청 경로: /api/inquiries/{id}
	 *
	 * @param id 조회할 문의 ID
	 * @return 200 OK, 문의 상세 정보. 찾지 못할 경우 404 Not Found.
	 */
	@GetMapping("/{id}")
	public ResponseEntity<InquiryDetailResponseDto> getInquiryById(@PathVariable Long id) {
		log.info("🔍 문의 단일 상세 조회 요청 - inquiryId: {}", id);

		try {
			InquiryDetailResponseDto result = inquiryService.getInquiryById(id);
			log.info("✅ 문의 단일 상세 조회 성공 - inquiryId: {}", id);
			return ResponseEntity.ok(result);
		} catch (IllegalArgumentException e) {
			log.warn("⚠️ 문의 단일 상세 조회 실패 - {}", e.getMessage());
			return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
		} catch (Exception e) {
			log.error("❌ 문의 단일 상세 조회 중 서버 오류 - inquiryId: {}", id, e);
			return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
		}
	}

	/**
	 * 📍 문의 작성 API
	 * - JSON 데이터와 여러 이미지 파일을 함께 받아 새로운 문의를 등록한다.
	 *
	 * - 요청 방식: POST
	 * - 요청 경로: /api/inquiries
	 *
	 * @param requestDto 문의 내용 데이터 (JSON 형식의 파트)
	 * @param images     업로드할 이미지 파일 리스트 (선택 사항)
	 * @param request    JWT 토큰 추출을 위한 HttpServletRequest
	 * @return 201 Created, 생성된 문의의 상세 정보
	 */
	@PostMapping(value = "/create", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
	public ResponseEntity<CreateInquiryResponseDto> createInquiry(
		@RequestPart("requestDto") CreateInquiryRequestDto requestDto,
		@RequestPart(value = "images", required = false) List<MultipartFile> images,
		HttpServletRequest request) {
		log.info("🚀 [문의 등록] POST /api/inquiry/create 요청 수신");

		try {
			Long userId = jwtExtractor.extractUserId(request);
			log.debug("➡️ userId 추출 완료: {}", userId);

			CreateInquiryResponseDto responseDto = inquiryService.createInquiry(requestDto, images, userId);
			log.info("✅ 문의 등록 성공 - inquiryId: {}", responseDto.getId());

			return ResponseEntity.status(HttpStatus.CREATED).body(responseDto);
		} catch (IllegalArgumentException e) {
			log.warn("⚠️ 문의 등록 실패 - {}", e.getMessage());
			return ResponseEntity.badRequest().build();
		} catch (Exception e) {
			log.error("❌ 문의 등록 중 서버 오류", e);
			return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
		}
	}


	// 문의 수정 (답변 안 달렸을 때만)

	/**
	 * 📍 문의 답변 작성 API
	 * - 특정 문의에 대한 답변을 등록한다. (관리자 권한 필요)
	 *
	 * - 요청 방식: POST
	 * - 요청 경로: /api/inquiries/{inquiryId}/answer
	 *
	 * @param inquiryId 답변을 등록할 문의 ID
	 * @param requestDto 답변 내용
	 * @param request JWT 토큰에서 관리자 ID를 추출하기 위한 HttpServletRequest
	 * @return 201 Created, 생성된 답변 정보
	 */
	@PostMapping("/{inquiryId}/answer")
	@PreAuthorize("hasRole('ADMIN')")
	public ResponseEntity<InquiryAnswerDto> createInquiryAnswer(
		@PathVariable Long inquiryId,
		@RequestBody CreateInquiryAnswerRequestDto requestDto,
		HttpServletRequest request) {
		log.info("🚀 [답변 등록] POST /api/inquiry/{}/answer 요청 수신", inquiryId);

		try {
			Long adminId = jwtExtractor.extractUserId(request);
			log.debug("➡️ adminId 추출 완료: {}", adminId);

			InquiryAnswerDto responseDto = inquiryService.createInquiryAnswer(inquiryId, requestDto, adminId);
			log.info("✅ 답변 등록 성공 - inquiryId: {}, answerId: {}", inquiryId, responseDto.getAnswerId());

			return ResponseEntity.status(HttpStatus.CREATED).body(responseDto);
		} catch (IllegalArgumentException e) {
			log.warn("⚠️ 답변 등록 실패 (잘못된 요청) - {}", e.getMessage());
			return ResponseEntity.badRequest().build();
		} catch (IllegalStateException e) {
			log.warn("⚠️ 답변 등록 실패 (상태 오류) - {}", e.getMessage());
			// 이미 처리된 요청이므로 409 Conflict 반환
			return ResponseEntity.status(HttpStatus.CONFLICT).build();
		} catch (Exception e) {
			log.error("❌ 답변 등록 중 서버 오류", e);
			return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
		}
	}
}

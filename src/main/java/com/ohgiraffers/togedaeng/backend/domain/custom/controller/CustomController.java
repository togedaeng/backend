package com.ohgiraffers.togedaeng.backend.domain.custom.controller;

import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.ohgiraffers.togedaeng.backend.domain.custom.dto.request.UpdateCustomStatusCanceledRequestDto;
import com.ohgiraffers.togedaeng.backend.domain.custom.dto.request.UpdateCustomStatusCompletedRequestDto;
import com.ohgiraffers.togedaeng.backend.domain.custom.dto.request.UpdateCustomStatusHoldRequestDto;
import com.ohgiraffers.togedaeng.backend.domain.custom.dto.request.UpdateCustomStatusInProgressRequestDto;
import com.ohgiraffers.togedaeng.backend.domain.custom.dto.response.CustomDetailResponseDto;
import com.ohgiraffers.togedaeng.backend.domain.custom.dto.response.CustomListByDogIdResponseDto;
import com.ohgiraffers.togedaeng.backend.domain.custom.dto.response.CustomListResponseDto;
import com.ohgiraffers.togedaeng.backend.domain.custom.dto.response.UpdateCustomStatusCanceledResponseDto;
import com.ohgiraffers.togedaeng.backend.domain.custom.dto.response.UpdateCustomStatusCompletedResponseDto;
import com.ohgiraffers.togedaeng.backend.domain.custom.dto.response.UpdateCustomStatusHoldResponseDto;
import com.ohgiraffers.togedaeng.backend.domain.custom.dto.response.UpdateCustomStatusInProgressResponseDto;
import com.ohgiraffers.togedaeng.backend.domain.custom.service.CustomService;
import com.ohgiraffers.togedaeng.backend.domain.dog.controller.DogController;

import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/custom")
@RequiredArgsConstructor
public class CustomController {

	Logger log = LoggerFactory.getLogger(DogController.class);

	private final CustomService customService;

	/**
	 * 📍 커스텀 요청 전체 조회 API (페이지네이션 적용)
	 * - 커스텀 요청을 페이지네이션으로 조회한다. (8개씩 페이지 처리)
	 * - 요청 방식: GET
	 * - 요청 경로: /api/custom?page=0&size=8
	 *
	 * @param page 페이지 번호 (0부터 시작, 기본값: 0)
	 * @param size 페이지당 항목 수 (기본값: 8)
	 * @return 페이지네이션된 커스텀 요청 리스트 (Page<CustomListResponseDto>)
	 */
	@GetMapping
	public ResponseEntity<Page<CustomListResponseDto>> getAllCustomRequests(
		@RequestParam(value = "page", defaultValue = "0") int page,
		@RequestParam(value = "size", defaultValue = "8") int size) {
		log.info("🔍 커스텀 전체 조회 요청 - page: {}, size: {}", page, size);

		try {
			// 페이지네이션 객체 생성 (최신 등록순으로 정렬)
			Pageable pageable = PageRequest.of(page, size, Sort.by(Sort.Direction.DESC, "createdAt"));
			Page<CustomListResponseDto> result = customService.getAllCustomRequests(pageable);

			log.info("✅ 커스텀 전체 조회 성공 - page: {}, size: {}, totalElements: {}, totalPages: {}",
				page, size, result.getTotalElements(), result.getTotalPages());
			return ResponseEntity.ok(result);
		} catch (IllegalArgumentException e) {
			log.warn("⚠️ 커스텀 전체 조회 실패 - {}", e.getMessage());
			return ResponseEntity.badRequest().build();
		} catch (Exception e) {
			log.error("❌ 커스텀 전체 조회 중 서버 오류", e);
			return ResponseEntity.status(500).build();
		}
	}

	/**
	 * 📍 커스텀 요청 단일 상세 조회 API
	 * - 특정 커스텀 요청의 상세 정보를 반환한다.
	 * - 요청 방식: GET
	 * - 요청 경로: /api/custom/{id}
	 *
	 * @param customId 조회할 커스텀 요청 ID (PathVariable)
	 * @return 커스텀 요청 상세 정보 (CustomDetailResponseDto)
	 */
	@GetMapping("/{id}")
	public ResponseEntity<CustomDetailResponseDto> getCustomById(@PathVariable("id") Long customId) {
		log.info("🔍 커스텀 단일 상세 조회 요청 - customId: {}", customId);

		try {
			CustomDetailResponseDto result = customService.getCustomById(customId);
			log.info("✅ 커스텀 단일 상세 조회 성공 - customId: {}", customId);
			return ResponseEntity.ok(result);
		} catch (IllegalArgumentException e) {
			log.warn("⚠️ 커스텀 단일 상세 조회 실패 - {}", e.getMessage());
			return ResponseEntity.badRequest().build();
		} catch (Exception e) {
			log.error("❌ 커스텀 단일 상세 조회 중 서버 오류 - customId: {}", customId, e);
			return ResponseEntity.status(500).build();
		}
	}

	// 반려견 id별 커스텀 요청 목록 전체 조회
	@GetMapping("/list/{dogId}")
	public ResponseEntity<List<CustomListByDogIdResponseDto>> getCustomRequestsByDogId(
		@PathVariable("dogId") Long dogId) {
		log.info("DogId 별 커스텀 요청 목록 전체 조회 요청 - dogId: {}", dogId);

		try {
			List<CustomListByDogIdResponseDto> customListByDogIdResponseDtos = customService.getAllCustomRequestsByDogId(
				dogId);
			return ResponseEntity.ok(customListByDogIdResponseDtos);
		} catch (IllegalArgumentException e) {
			log.warn("⚠️ 커스텀 전체 조회 실패 - {}", e.getMessage());
			return ResponseEntity.badRequest().build();
		} catch (Exception e) {
			log.error("❌ 커스텀 전체 조회 중 서버 오류", e);
			return ResponseEntity.status(500).build();
		}
	}

	/**
	 * 📍 커스텀 요청 상태를 '진행중(IN_PROGRESS)'으로 변경하는 API
	 * - 요청 경로에서 커스텀 요청 ID를 받고, 요청 바디에서 관리자 ID를 전달받음
	 * - 해당 커스텀 요청의 상태를 진행중으로 변경하고, 관리자 ID를 등록함
	 * - 변경 성공 시 200 OK 반환, 상태 변경 실패 시 400 Bad Request, 서버 에러 시 500 반환
	 *
	 * 요청 방식: PUT
	 * 요청 경로: /api/custom/{id}/in-progress
	 *
	 * @param customId 경로 변수로 전달받는 커스텀 요청 ID
	 * @param dto      요청 바디로 전달받는 관리자 ID 포함 DTO
	 * @return 상태 변경 결과에 따른 ResponseEntity 반환
	 */
	@PutMapping("/{id}/in-progress")
	public ResponseEntity<UpdateCustomStatusInProgressResponseDto> updateCustomStatusInProgress(
		@PathVariable("id") Long customId,
		@RequestBody UpdateCustomStatusInProgressRequestDto dto) {
		log.info("🔄 커스텀 요청 진행중 상태 변경 요청 - customId: {}, adminId: {}", customId, dto.getAdminId());

		try {
			customService.updateCustomStatusInProgress(customId, dto);
			log.info("✅ 커스텀 요청 진행중 상태 변경 성공 - customId: {}", customId);
			return ResponseEntity.ok().build();
		} catch (IllegalArgumentException e) {
			log.warn("⚠️ 커스텀 진행 상태 변경 실패 - {}", e.getMessage());
			return ResponseEntity.badRequest().build();
		} catch (Exception e) {
			log.error("❌ 커스텀 진행 상태 변경 중 예외 발생", e);
			return ResponseEntity.status(500).build();
		}
	}

	/**
	 * 📍 커스텀 요청 상태를 '보류(HOLD)'로 변경하는 API
	 * - 요청 경로에서 커스텀 요청 ID를 받고, 요청 바디에서 관리자 ID와 보류 사유를 전달받음
	 * - 해당 커스텀 요청의 상태를 보류로 변경하고, 관리자 ID 및 보류 사유를 등록함
	 * - 강아지 상태를 SUSPENDED로 변경
	 * - 변경 성공 시 보류 상태 정보 DTO와 함께 200 OK 반환,
	 * 상태 변경 실패 시 400 Bad Request,
	 * 서버 에러 시 500 Internal Server Error 반환
	 *
	 * 요청 방식: PUT
	 * 요청 경로: /api/customs/{id}/hold
	 *
	 * @param customId 경로 변수로 전달받는 커스텀 요청 ID
	 * @param dto      요청 바디로 전달받는 관리자 ID, 보류 사유 포함 DTO
	 * @return 보류 상태 변경 결과를 담은 DTO와 ResponseEntity 반환
	 */
	@PutMapping("/{id}/hold")
	public ResponseEntity<UpdateCustomStatusHoldResponseDto> updateCustomStatusHold(
		@PathVariable("id") Long customId,
		@RequestBody UpdateCustomStatusHoldRequestDto dto) {
		log.info("🔄 커스텀 요청 보류 상태 변경 요청 - customId: {}, adminId: {}, reason: {}", customId, dto.getAdminId(),
			dto.getReason());

		try {
			UpdateCustomStatusHoldResponseDto responseDto = customService.updateCustomStatusHold(customId, dto);
			log.info("✅ 커스텀 요청 보류 상태 변경 성공 - customId: {}", customId);
			return ResponseEntity.ok(responseDto);
		} catch (IllegalArgumentException e) {
			log.warn("⚠️ 커스텀 보류 상태 변경 실패 - {}", e.getMessage());
			return ResponseEntity.badRequest().build();
		} catch (Exception e) {
			log.error("❌ 커스텀 보류 상태 변경 중 예외 발생", e);
			return ResponseEntity.status(500).build();
		}
	}

	/**
	 * 📍 커스텀 요청 상태를 '완료(COMPLETED)'로 변경하는 API
	 * - 렌더링 이미지 파일과 관리자 ID를 받아 S3 업로드 후 Dog에 저장
	 * - 커스텀 상태를 COMPLETED로 변경
	 *
	 * 요청 방식: PUT
	 * 요청 경로: /api/custom/{id}/completed
	 *
	 * @param customId 커스텀 요청 ID (PathVariable)
	 * @param dto      관리자 ID 및 렌더링 이미지 포함 DTO (Multipart)
	 * @return 상태 변경 결과 DTO와 함께 200 OK 반환
	 */
	@PutMapping("/{id}/completed")
	public ResponseEntity<UpdateCustomStatusCompletedResponseDto> updateCustomStatusCompleted(
		@PathVariable("id") Long customId,
		@ModelAttribute UpdateCustomStatusCompletedRequestDto dto) {
		log.info("✅ 커스텀 요청 완료 처리 요청 - customId: {}, adminId: {}", customId, dto.getAdminId());

		try {
			UpdateCustomStatusCompletedResponseDto responseDto = customService.updateCustomStatusCompleted(customId,
				dto);

			log.info("🎉 커스텀 요청 완료 처리 성공 - customId: {}", customId);
			return ResponseEntity.ok(responseDto);
		} catch (IllegalArgumentException e) {
			log.warn("⚠️ 커스텀 완료 처리 실패 - {}", e.getMessage());
			return ResponseEntity.badRequest().build();
		} catch (Exception e) {
			log.error("❌ 커스텀 완료 처리 중 서버 오류 발생", e);
			return ResponseEntity.status(500).build();
		}
	}

	/**
	 * 📍 커스텀 요청 상태를 '취소(CANCELLED)'로 변경하는 API
	 * - 요청 경로에서 커스텀 요청 ID를 받고, 요청 바디에서 관리자 ID를 전달받음
	 * - 해당 커스텀 요청의 상태를 취소로 변경하고, 관리자 ID를 등록함
	 * - 변경 성공 시 200 OK 반환, 상태 변경 실패 시 400 Bad Request, 서버 에러 시 500 반환
	 *
	 * 요청 방식: PUT
	 * 요청 경로: /api/custom/{id}/canceled
	 *
	 * @param customId 경로 변수로 전달받는 커스텀 요청 ID
	 * @param dto      요청 바디로 전달받는 관리자 ID 포함 DTO
	 * @return 상태 변경 결과에 따른 ResponseEntity 반환
	 */
	@PutMapping("/{id}/canceled")
	public ResponseEntity<UpdateCustomStatusCanceledResponseDto> updateCustomStatusCanceled(
		@PathVariable("id") Long customId,
		@RequestBody UpdateCustomStatusCanceledRequestDto dto) {
		log.info("🔄 커스텀 요청 취소 상태 변경 요청 - customId: {}, adminId: {}", customId, dto.getAdminId());

		try {
			customService.updateCustomStatusCanceled(customId, dto);
			log.info("✅ 커스텀 요청 취소 상태 변경 성공 - customId: {}", customId);
			return ResponseEntity.ok().build();
		} catch (IllegalArgumentException e) {
			log.warn("⚠️ 커스텀 취소 상태 변경 실패 - {}", e.getMessage());
			return ResponseEntity.badRequest().build();
		} catch (Exception e) {
			log.error("❌ 커스텀 취소 상태 변경 중 예외 발생", e);
			return ResponseEntity.status(500).build();
		}
	}
}

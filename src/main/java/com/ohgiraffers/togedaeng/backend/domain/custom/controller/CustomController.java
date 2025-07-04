package com.ohgiraffers.togedaeng.backend.domain.custom.controller;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.ohgiraffers.togedaeng.backend.domain.custom.dto.request.UpdateCustomStatusHoldRequestDto;
import com.ohgiraffers.togedaeng.backend.domain.custom.dto.response.UpdateCustomStatusHoldResponseDto;
import com.ohgiraffers.togedaeng.backend.domain.dog.controller.DogController;
import com.ohgiraffers.togedaeng.backend.domain.custom.dto.request.UpdateCustomStatusCanceledRequestDto;
import com.ohgiraffers.togedaeng.backend.domain.custom.dto.request.UpdateCustomStatusInProgressRequestDto;
import com.ohgiraffers.togedaeng.backend.domain.custom.dto.response.UpdateCustomStatusInProgressResponseDto;
import com.ohgiraffers.togedaeng.backend.domain.custom.service.CustomService;

import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/custom")
@RequiredArgsConstructor
public class CustomController {

	Logger log = LoggerFactory.getLogger(DogController.class);

	private final CustomService customService;

	// 커스텀 요청 전체 조회

	// 커스텀 요청 단일 조회

	/**
	 * 📍 커스텀 요청 상태를 '진행중(IN_PROGRESS)'으로 변경하는 API
	 * - 요청 경로에서 커스텀 요청 ID를 받고, 요청 바디에서 관리자 ID를 전달받음
	 * - 해당 커스텀 요청의 상태를 진행중으로 변경하고, 관리자 ID를 등록함
	 * - 변경 성공 시 200 OK 반환, 상태 변경 실패 시 400 Bad Request, 서버 에러 시 500 반환
	 *
	 * 요청 방식: PUT
	 * 요청 경로: /api/custom/{id}/in-progress
	 *
	 * @param customId  경로 변수로 전달받는 커스텀 요청 ID
	 * @param dto       요청 바디로 전달받는 관리자 ID 포함 DTO
	 * @return 상태 변경 결과에 따른 ResponseEntity 반환
	 */
	@PutMapping("/{id}/in-progress")
	public ResponseEntity<UpdateCustomStatusInProgressResponseDto> updateCustomStatusInProgress(
		@PathVariable("id") Long customId,
		@RequestBody UpdateCustomStatusInProgressRequestDto dto
	) {
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
	 *   상태 변경 실패 시 400 Bad Request,
	 *   서버 에러 시 500 Internal Server Error 반환
	 *
	 * 요청 방식: PUT
	 * 요청 경로: /api/customs/{id}/hold
	 *
	 * @param customId  경로 변수로 전달받는 커스텀 요청 ID
	 * @param dto       요청 바디로 전달받는 관리자 ID, 보류 사유 포함 DTO
	 * @return 보류 상태 변경 결과를 담은 DTO와 ResponseEntity 반환
	 */
	@PutMapping("/{id}/hold")
	public ResponseEntity<UpdateCustomStatusHoldResponseDto> updateCustomStatusHold(
		@PathVariable("id") Long customId,
		@RequestBody UpdateCustomStatusHoldRequestDto dto
	) {
		log.info("🔄 커스텀 요청 보류 상태 변경 요청 - customId: {}, adminId: {}, reason: {}", customId, dto.getAdminId(), dto.getReason());

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

	// 커스텀 상태 변경 - 완료 -> 렌더링 이미지 dog에 저장 후 상태 변경, 강아지 상태는 APPROVED

	/**
	 * 📍 커스텀 요청 상태를 '취소(CANCELLED)'로 변경하는 API
	 * - 요청 경로에서 커스텀 요청 ID를 받고, 요청 바디에서 관리자 ID를 전달받음
	 * - 해당 커스텀 요청의 상태를 취소로 변경하고, 관리자 ID를 등록함
	 * - 변경 성공 시 200 OK 반환, 상태 변경 실패 시 400 Bad Request, 서버 에러 시 500 반환
	 *
	 * 요청 방식: PUT
	 * 요청 경로: /api/custom/{id}/canceled
	 *
	 * @param customId  경로 변수로 전달받는 커스텀 요청 ID
	 * @param dto       요청 바디로 전달받는 관리자 ID 포함 DTO
	 * @return 상태 변경 결과에 따른 ResponseEntity 반환
	 */
	@PutMapping("/{id}/canceled")
	public ResponseEntity<UpdateCustomStatusInProgressResponseDto> updateCustomStatusCanceled(
		@PathVariable("id") Long customId,
		@RequestBody UpdateCustomStatusCanceledRequestDto dto
	) {
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

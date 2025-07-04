package com.ohgiraffers.togedaeng.backend.domain.custom.service;

import java.io.IOException;
import java.time.LocalDateTime;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import com.ohgiraffers.togedaeng.backend.domain.custom.dto.request.UpdateCustomStatusHoldRequestDto;
import com.ohgiraffers.togedaeng.backend.domain.custom.dto.response.UpdateCustomStatusHoldResponseDto;
import com.ohgiraffers.togedaeng.backend.domain.custom.entity.Hold;
import com.ohgiraffers.togedaeng.backend.domain.custom.repository.HoldRepository;
import com.ohgiraffers.togedaeng.backend.domain.dog.dto.request.CreateDogRequestDto;
import com.ohgiraffers.togedaeng.backend.domain.dog.entity.Dog;
import com.ohgiraffers.togedaeng.backend.domain.dog.exception.ImageUploadException;
import com.ohgiraffers.togedaeng.backend.domain.dog.repository.DogRepository;
import com.ohgiraffers.togedaeng.backend.domain.custom.dto.request.UpdateCustomStatusCanceledRequestDto;
import com.ohgiraffers.togedaeng.backend.domain.custom.dto.request.UpdateCustomStatusInProgressRequestDto;
import com.ohgiraffers.togedaeng.backend.domain.custom.dto.response.UpdateCustomStatusCanceledResponseDto;
import com.ohgiraffers.togedaeng.backend.domain.custom.dto.response.UpdateCustomStatusInProgressResponseDto;
import com.ohgiraffers.togedaeng.backend.domain.custom.entity.Custom;
import com.ohgiraffers.togedaeng.backend.domain.custom.entity.DogImage;
import com.ohgiraffers.togedaeng.backend.domain.custom.entity.Status;
import com.ohgiraffers.togedaeng.backend.domain.custom.entity.Type;
import com.ohgiraffers.togedaeng.backend.domain.custom.repository.CustomRepository;
import com.ohgiraffers.togedaeng.backend.domain.custom.repository.DogImageRepository;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class CustomService {

	Logger log = LoggerFactory.getLogger(CustomService.class);

	private final CustomRepository customRepository;
	private final S3Uploader s3Uploader;
	private final DogImageRepository dogImageRepository;
	private final DogRepository dogRepository;
	private final HoldRepository holdRepository;

	/**
	 * 📍 강아지 등록 시 함께 커스텀 요청을 생성하는 메서드
	 * - 상태는 기본적으로 PENDING으로 저장됨
	 * - 메인 이미지는 필수이며, 서브 이미지는 최대 3장까지 허용
	 * - 업로드된 이미지는 S3에 저장되고, 각각 DogImage 엔티티로 저장됨
	 *
	 * @param dogId 등록된 강아지의 ID
	 * @param dto 강아지 등록 요청 DTO (이미지 포함)
	 * @throws IllegalArgumentException 메인 이미지가 없거나 서브 이미지가 3장을 초과할 경우
	 * @throws ImageUploadException S3 업로드에 실패한 경우
	 */
	@Transactional
	public void createCustomRequest(Long dogId, CreateDogRequestDto dto) {
		log.info("📦 [커스텀 요청 생성] 시작 - dogId: {}", dogId);

		if (dto.getMainImage() == null) {
			throw new IllegalArgumentException("메인 이미지는 필수입니다.");
		}

		if (dto.getSubImages() != null && dto.getSubImages().size() > 3) {
			throw new IllegalArgumentException("서브 이미지는 최대 3장까지 등록 가능합니다.");
		}

		// 1. 커스텀 요청 저장
		Custom custom = new Custom(dogId, Status.PENDING, LocalDateTime.now());

		customRepository.save(custom);
		log.debug("📝 커스텀 요청 저장 완료 - customId: {}", custom.getId());

		// 2. 이미지 업로드 및 저장
		try {
			// 메인 이미지
			String mainUrl = s3Uploader.upload(dto.getMainImage(), "dog-images");
			dogImageRepository.save(new DogImage(null, custom.getId(), mainUrl, Type.MAIN));
			log.debug("📷 메인 이미지 업로드 완료 - url: {}", mainUrl);

			// 서브 이미지
			if (dto.getSubImages() != null) {
				for (MultipartFile sub : dto.getSubImages()) {
					String subUrl = s3Uploader.upload(sub, "dog-images");
					dogImageRepository.save(new DogImage(null, custom.getId(), subUrl, Type.SUB));
					log.debug("📷 서브 이미지 업로드 완료 - url: {}", subUrl);
				}
			}

		} catch (IOException e) {
			log.error("❌ 이미지 업로드 실패", e);
			throw new ImageUploadException("이미지 업로드 실패", e);
		}

		log.info("✅ [커스텀 요청 생성] 완료 - customId: {}", custom.getId());
	}

	/**
	 * 📍 커스텀 요청 상태를 '진행중(IN_PROGRESS)'으로 변경하는 메서드
	 * - 커스텀 요청 ID로 해당 요청을 조회하고 존재하지 않으면 예외 발생
	 * - 상태를 IN_PROGRESS로 변경하고, 관리자 ID와 시작 일자를 설정
	 * - 해당 커스텀 요청에 연결된 강아지의 상태를 APPROVED로 변경
	 * - 변경된 커스텀 요청 정보를 담은 응답 DTO를 반환
	 *
	 * @param customId 변경할 커스텀 요청의 ID
	 * @param dto     관리자 ID를 포함한 상태 변경 요청 DTO
	 * @return 상태 변경 결과를 담은 UpdateCustomStatusInProgressResponseDto
	 * @throws IllegalArgumentException 존재하지 않는 커스텀 요청 또는 강아지일 경우
	 */
	@Transactional
	public UpdateCustomStatusInProgressResponseDto updateCustomStatusInProgress(Long customId, UpdateCustomStatusInProgressRequestDto dto) {
		Long adminId = dto.getAdminId();

		// 커스텀 요청 조회
		Custom custom = customRepository.findById(customId)
			.orElseThrow(() -> new IllegalArgumentException("해당 커스텀 요청이 존재하지 않습니다."));

		// 상태 변경 및 관리자 아이디, 시작일자 설정
		custom.setStatus(Status.IN_PROGRESS);
		custom.setAdminId(adminId);
		custom.setStartedAt(LocalDateTime.now());
		customRepository.save(custom);

		// 강아지 엔티티 조회 및 상태 변경
		Dog dog = dogRepository.findById(custom.getDogId())
			.orElseThrow(() -> new IllegalArgumentException("해당 강아지가 존재하지 않습니다."));
		dog.setStatus(com.ohgiraffers.togedaeng.backend.domain.dog.entity.Status.APPROVED);
		dogRepository.save(dog);

		// 응답 DTO 생성 및 반환
		UpdateCustomStatusInProgressResponseDto responseDto = new UpdateCustomStatusInProgressResponseDto(
			custom.getId(),
			custom.getDogId(),
			custom.getAdminId(),
			custom.getStatus(),
			custom.getStartedAt()
		);

		return responseDto;
	}

	/**
	 * 📍 커스텀 요청 상태를 '보류(HOLD)'로 변경하는 메서드
	 * - 커스텀 요청 ID로 해당 요청을 조회하고 존재하지 않으면 예외 발생
	 * - 상태를 HOLD로 변경하고, 관리자 ID를 설정
	 * - 보류 사유를 Hold 엔티티로 새로 저장
	 * - 해당 커스텀 요청에 연결된 강아지의 상태를 SUSPENDED로 변경
	 * - 변경된 커스텀 요청 및 보류 정보를 담은 응답 DTO를 반환
	 *
	 * @param customId 변경할 커스텀 요청의 ID
	 * @param dto     관리자 ID와 보류 사유를 포함한 상태 변경 요청 DTO
	 * @return 상태 변경 결과를 담은 UpdateCustomStatusHoldResponseDto
	 * @throws IllegalArgumentException 존재하지 않는 커스텀 요청 또는 강아지일 경우
	 * @throws IllegalStateException    이미 HOLD 또는 COMPLETED 상태일 경우
	 */
	@Transactional
	public UpdateCustomStatusHoldResponseDto updateCustomStatusHold(Long customId, UpdateCustomStatusHoldRequestDto dto) {

		// 커스텀 요청 조회
		Custom custom = customRepository.findById(customId)
			.orElseThrow(() -> new IllegalArgumentException("Custom 요청을 찾을 수 없습니다. ID=" + customId));

		// 상태 검증
		if (custom.getStatus() == Status.HOLD || custom.getStatus() == Status.COMPLETED) {
			throw new IllegalStateException("이미 보류 중이거나 완료된 요청은 보류 처리할 수 없습니다.");
		}

		// 상태 변경 및 관리자 ID 등록
		custom.setStatus(Status.HOLD);
		custom.setAdminId(dto.getAdminId());
		customRepository.save(custom);

		// Hold 엔티티 생성 및 저장
		Hold hold = new Hold(custom.getId(), dto.getReason(), LocalDateTime.now());
		holdRepository.save(hold);

		// 강아지 상태 SUSPENDED로 변경
		Dog dog = dogRepository.findById(custom.getDogId())
			.orElseThrow(() -> new IllegalArgumentException("강아지를 찾을 수 없습니다. ID=" + custom.getDogId()));
		dog.setStatus(com.ohgiraffers.togedaeng.backend.domain.dog.entity.Status.SUSPENDED);
		dogRepository.save(dog);

		// 응답 DTO 생성 및 반환
		UpdateCustomStatusHoldResponseDto responseDto = new UpdateCustomStatusHoldResponseDto(
			custom.getId(),
			dog.getId(),
			custom.getAdminId(),
			custom.getStatus(),
			hold.getReason(),
			hold.getCreatedAt()
		);

		return responseDto;
	}

	/**
	 * 📍 커스텀 요청 상태를 '취소(CANCELLED)'로 변경하는 메서드
	 * - 커스텀 요청 ID로 해당 요청을 조회하고 존재하지 않으면 예외 발생
	 * - 상태를 CANCELLED로 변경하고, 관리자 ID와 취소 일자를 설정
	 * - 해당 커스텀 요청에 연결된 강아지의 상태를 SUSPENDED로 변경
	 * - 변경된 커스텀 요청 정보를 담은 응답 DTO를 반환
	 *
	 * @param customId 변경할 커스텀 요청의 ID
	 * @param dto     관리자 ID를 포함한 상태 변경 요청 DTO
	 * @return 상태 변경 결과를 담은 UpdateCustomStatusCanceledResponseDto
	 * @throws IllegalArgumentException 존재하지 않는 커스텀 요청 또는 강아지일 경우
	 */
	@Transactional
	public UpdateCustomStatusCanceledResponseDto updateCustomStatusCanceled(Long customId, UpdateCustomStatusCanceledRequestDto dto) {
		Long adminId = dto.getAdminId();

		// 커스텀 요청 조회
		Custom custom = customRepository.findById(customId)
			.orElseThrow(() -> new IllegalArgumentException("해당 커스텀 요청이 존재하지 않습니다."));

		// 상태 변경 및 관리자 아이디, 취소일자 설정
		custom.setStatus(Status.CANCELLED);
		custom.setAdminId(adminId);
		custom.setCanceledAt(LocalDateTime.now());
		customRepository.save(custom);

		// 강아지 엔티티 조회 및 상태 변경
		Dog dog = dogRepository.findById(custom.getDogId())
			.orElseThrow(() -> new IllegalArgumentException("해당 강아지가 존재하지 않습니다."));
		dog.setStatus(com.ohgiraffers.togedaeng.backend.domain.dog.entity.Status.SUSPENDED);
		dogRepository.save(dog);

		// 응답 DTO 생성 및 반환
		UpdateCustomStatusCanceledResponseDto responseDto = new UpdateCustomStatusCanceledResponseDto(
			custom.getId(),
			custom.getDogId(),
			custom.getAdminId(),
			custom.getStatus(),
			custom.getCanceledAt()
		);

		return responseDto;
	}
}

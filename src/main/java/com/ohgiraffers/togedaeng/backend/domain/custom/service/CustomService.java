package com.ohgiraffers.togedaeng.backend.domain.custom.service;

import java.io.IOException;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import com.ohgiraffers.togedaeng.backend.domain.custom.dto.request.UpdateCustomStatusCanceledRequestDto;
import com.ohgiraffers.togedaeng.backend.domain.custom.dto.request.UpdateCustomStatusCompletedRequestDto;
import com.ohgiraffers.togedaeng.backend.domain.custom.dto.request.UpdateCustomStatusHoldRequestDto;
import com.ohgiraffers.togedaeng.backend.domain.custom.dto.request.UpdateCustomStatusInProgressRequestDto;
import com.ohgiraffers.togedaeng.backend.domain.custom.dto.response.CustomDetailResponseDto;
import com.ohgiraffers.togedaeng.backend.domain.custom.dto.response.CustomListByDogIdResponseDto;
import com.ohgiraffers.togedaeng.backend.domain.custom.dto.response.CustomListResponseDto;
import com.ohgiraffers.togedaeng.backend.domain.custom.dto.response.HoldSimpleDto;
import com.ohgiraffers.togedaeng.backend.domain.custom.dto.response.UpdateCustomStatusCanceledResponseDto;
import com.ohgiraffers.togedaeng.backend.domain.custom.dto.response.UpdateCustomStatusCompletedResponseDto;
import com.ohgiraffers.togedaeng.backend.domain.custom.dto.response.UpdateCustomStatusHoldResponseDto;
import com.ohgiraffers.togedaeng.backend.domain.custom.dto.response.UpdateCustomStatusInProgressResponseDto;
import com.ohgiraffers.togedaeng.backend.domain.custom.entity.Custom;
import com.ohgiraffers.togedaeng.backend.domain.custom.entity.DogImage;
import com.ohgiraffers.togedaeng.backend.domain.custom.entity.Hold;
import com.ohgiraffers.togedaeng.backend.domain.custom.entity.Status;
import com.ohgiraffers.togedaeng.backend.domain.custom.entity.Type;
import com.ohgiraffers.togedaeng.backend.domain.custom.repository.CustomRepository;
import com.ohgiraffers.togedaeng.backend.domain.custom.repository.DogImageRepository;
import com.ohgiraffers.togedaeng.backend.domain.custom.repository.HoldRepository;
import com.ohgiraffers.togedaeng.backend.domain.dog.entity.Dog;
import com.ohgiraffers.togedaeng.backend.domain.dog.entity.DogOwner;
import com.ohgiraffers.togedaeng.backend.domain.dog.entity.Gender;
import com.ohgiraffers.togedaeng.backend.domain.dog.exception.ImageUploadException;
import com.ohgiraffers.togedaeng.backend.domain.dog.repository.DogOwnerRepository;
import com.ohgiraffers.togedaeng.backend.domain.dog.repository.DogRepository;
import com.ohgiraffers.togedaeng.backend.domain.personality.repository.DogPersonalityRepository;
import com.ohgiraffers.togedaeng.backend.domain.personality.repository.PersonalityCombinationRepository;
import com.ohgiraffers.togedaeng.backend.domain.user.model.entity.User;
import com.ohgiraffers.togedaeng.backend.domain.user.repository.UserRepository;

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
	private final DogOwnerRepository dogOwnerRepository;
	private final UserRepository userRepository;
	private final DogPersonalityRepository dogPersonalityRepository;
	private final PersonalityCombinationRepository personalityCombinationRepository;

	/**
	 * 📍 강아지 등록 시 커스텀 요청의 메인 이미지를 업로드하는 메서드
	 * - 상태는 기본적으로 PENDING으로 저장됨
	 * - 메인 이미지는 필수이며, 업로드된 이미지는 S3에 저장되고 DogImage 엔티티로 저장
	 *
	 * @param dogId 등록된 강아지의 ID
	 * @param mainImage 메인 이미지 MultipartFile
	 * @return 생성된 커스텀 요청의 ID (customId)
	 * @throws IllegalArgumentException 메인 이미지가 없을 경우
	 * @throws ImageUploadException     S3 업로드에 실패한 경우
	 */
	@Transactional
	public Long uploadMainImage(Long dogId, MultipartFile mainImage) {
		log.info("📦 [커스텀 메인 이미지 업로드] 시작 - dogId: {}", dogId);

		if (mainImage == null) {
			throw new IllegalArgumentException("메인 이미지는 필수입니다.");
		}

		// 1. 커스텀 요청 저장
		Custom custom = new Custom(dogId, Status.PENDING, LocalDateTime.now());

		customRepository.save(custom);
		log.debug("📝 커스텀 요청 저장 완료 - customId: {}", custom.getId());

		// 2. 메인 이미지 업로드 및 저장
		try {
			String mainUrl = s3Uploader.upload(mainImage, "dog-images");
			dogImageRepository.save(new DogImage(null, custom.getId(), mainUrl, Type.MAIN));
			log.debug("📷 메인 이미지 업로드 완료 - url: {}", mainUrl);
		} catch (IOException e) {
			log.error("❌ 메인 이미지 업로드 실패", e);
			throw new ImageUploadException("메인 이미지 업로드 실패", e);
		}

		log.info("✅ [커스텀 메인 이미지 업로드] 완료 - customId: {}", custom.getId());
		return custom.getId();
	}

	/**
	 * 📍 커스텀 요청의 서브 이미지를 업로드하는 메서드
	 * - 서브 이미지는 최대 3장까지 허용
	 * - 업로드된 이미지는 S3에 저장되고 각각 DogImage 엔티티로 저장
	 *
	 * @param customId 커스텀 요청 ID
	 * @param subImages 서브 이미지 목록 (MultipartFile 리스트)
	 * @throws IllegalArgumentException 서브 이미지가 3장을 초과할 경우
	 * @throws ImageUploadException     S3 업로드에 실패한 경우
	 */
	@Transactional
	public void uploadSubImages(Long customId, List<MultipartFile> subImages) {
		log.info("📦 [커스텀 서브 이미지 업로드] 시작 - customId: {}", customId);

		if (subImages != null && subImages.size() > 3) {
			throw new IllegalArgumentException("서브 이미지는 최대 3장까지 등록 가능합니다.");
		}

		// 1. 서브 이미지 업로드 및 저장
		try {
			if (subImages != null) {
				for (MultipartFile sub : subImages) {
					String subUrl = s3Uploader.upload(sub, "dog-images");
					dogImageRepository.save(new DogImage(null, customId, subUrl, Type.SUB));
					log.debug("📷 서브 이미지 업로드 완료 - url: {}", subUrl);
				}
			}
		} catch (IOException e) {
			log.error("❌ 서브 이미지 업로드 실패", e);
			throw new ImageUploadException("서브 이미지 업로드 실패", e);
		}

		log.info("✅ [커스텀 서브 이미지 업로드] 완료 - customId: {}", customId);
	}

	// 반려견 별 커스텀 요청 목록 전체 조회
	public List<CustomListByDogIdResponseDto> getAllCustomRequestsByDogId(Long dogId) {
		try {
			List<Custom> customs = customRepository.findByDogId(dogId);

			return customs.stream()
				.map(custom -> {
					Hold hold = holdRepository.findTopByCustomIdOrderByCreatedAtDesc(custom.getId());
					HoldSimpleDto holdDto =
						(hold != null) ? new HoldSimpleDto(hold.getId(), hold.getCreatedAt()) : null;

					return new CustomListByDogIdResponseDto(
						custom.getId(),
						custom.getAdminId(),     // adminId 그대로 전달
						custom.getStatus(),      // enum 그대로 전달
						custom.getCreatedAt(),
						custom.getStartedAt(),
						custom.getCompletedAt(),
						custom.getCanceledAt(),
						holdDto
					);
				})
				.toList();

		} catch (IllegalArgumentException e) {
			log.warn("⚠️ 커스텀 요청 목록 조회 실패 (dogId: {}) - {}", dogId, e.getMessage());
			throw e;
		} catch (Exception e) {
			log.error("❌ 커스텀 요청 목록 조회 중 서버 오류 (dogId: {})", dogId, e);
			throw e;
		}
	}

	/**
	 * 📍 커스텀 요청 전체 조회 서비스
	 * - Pageable을 받아 Page<CustomListResponseDto>로 반환한다.
	 * - 각 요청에 대해 강아지, 소유자, 관리자, 보류, 이미지 등 부가 정보를 조합하여 DTO로 변환한다.
	 *
	 * @param pageable 페이지네이션 정보
	 * @return 페이지네이션된 커스텀 요청 리스트 (Page<CustomListResponseDto>)
	 */
	public Page<CustomListResponseDto> getAllCustomRequests(Pageable pageable) {
		try {
			Page<Custom> customsPage = customRepository.findAll(pageable);
			return customsPage.map(custom -> {
				// Dog 정보
				Dog dog = dogRepository.findById(custom.getDogId()).orElse(null);
				String dogName = (dog != null) ? dog.getName() : null;

				// Owner 정보
				DogOwner dogOwner = dogOwnerRepository.findByDogId(custom.getDogId());
				String ownerNickname = null;
				if (dogOwner != null) {
					User owner = userRepository.findById(dogOwner.getUserId()).orElse(null);
					ownerNickname = (owner != null) ? owner.getNickname() : null;
				}

				// Admin 정보
				String adminNickname = null;
				if (custom.getAdminId() != null) {
					User admin = userRepository.findById(custom.getAdminId()).orElse(null);
					adminNickname = (admin != null) ? admin.getNickname() : null;
				}

				// Hold 정보 (최신 1건)
				Hold hold = holdRepository.findTopByCustomIdOrderByCreatedAtDesc(custom.getId());
				LocalDateTime holdCreatedAt = (hold != null) ? hold.getCreatedAt() : null;

				return new CustomListResponseDto(
					custom.getId(),
					dogName,
					ownerNickname,
					adminNickname,
					custom.getStatus(),
					custom.getCreatedAt(),
					custom.getStartedAt(),
					holdCreatedAt,
					custom.getCompletedAt(),
					custom.getCanceledAt()
				);
			});
		} catch (IllegalArgumentException e) {
			log.warn("⚠️ 커스텀 전체 조회(페이지네이션) 실패 - {}", e.getMessage());
			throw e;
		} catch (Exception e) {
			log.error("❌ 커스텀 전체 조회(페이지네이션) 중 서버 오류", e);
			throw e;
		}
	}

	/**
	 * 📍 커스텀 요청 단일 상세 조회 서비스
	 * - 특정 커스텀 요청의 상세 정보를 반환한다.
	 * - 강아지, 소유자, 관리자, 보류, 이미지, 성격 등 부가 정보를 모두 조합하여 DTO로 변환한다.
	 * - 예외 발생 시 로그를 남기고 예외를 다시 throw한다.
	 *
	 * 📍 커스텀 요청 단일 상세 조회
	 *
	 * @param customId 커스텀 요청 ID
	 * @return CustomDetailResponseDto
	 */
	public CustomDetailResponseDto getCustomById(Long customId) {
		try {
			Custom custom = customRepository.findById(customId)
				.orElseThrow(() -> new IllegalArgumentException("해당 커스텀 요청이 존재하지 않습니다."));

			// Dog 정보
			Dog dog = dogRepository.findById(custom.getDogId()).orElse(null);
			String dogName = (dog != null) ? dog.getName() : null;
			Gender dogGender = (dog != null) ? dog.getGender() : null;
			java.time.LocalDate dogBirth = (dog != null) ? dog.getBirth() : null;

			// Owner 정보
			DogOwner dogOwner = dogOwnerRepository.findByDogId(custom.getDogId());
			String requesterEmail = null;
			String requesterNickname = null;
			if (dogOwner != null) {
				User owner = userRepository.findById(dogOwner.getUserId()).orElse(null);
				if (owner != null) {
					requesterEmail = owner.getEmail();
					requesterNickname = owner.getNickname();
				}
			}

			// Admin 정보
			String adminNickname = null;
			if (custom.getAdminId() != null) {
				User admin = userRepository.findById(custom.getAdminId()).orElse(null);
				adminNickname = (admin != null) ? admin.getNickname() : null;
			}

			// Hold 정보 (최신 1건)
			Hold hold = holdRepository.findTopByCustomIdOrderByCreatedAtDesc(custom.getId());
			LocalDateTime holdCreatedAt = (hold != null) ? hold.getCreatedAt() : null;
			String holdReason = (hold != null) ? hold.getReason() : null;

			// Dog 이미지 (customId로)
			List<DogImage> dogImages = dogImageRepository.findByCustomId(custom.getId());
			List<String> dogImageUrls = dogImages.stream().map(DogImage::getImageUrl).collect(Collectors.toList());

			// 성격 (dogId로 PersonalityCombination에서 id 2개를 얻고, DogPersonality에서 name 조회)
			List<String> personalityNames = new ArrayList<>();
			personalityCombinationRepository.findByDogId(custom.getDogId()).ifPresent(comb -> {
				if (comb.getPersonalityId1() != null) {
					dogPersonalityRepository.findById(comb.getPersonalityId1())
						.ifPresent(p -> personalityNames.add(p.getName()));
				}
				if (comb.getPersonalityId2() != null) {
					dogPersonalityRepository.findById(comb.getPersonalityId2())
						.ifPresent(p -> personalityNames.add(p.getName()));
				}
			});

			return new CustomDetailResponseDto(
				custom.getId(),
				custom.getStatus(),
				requesterEmail,
				requesterNickname,
				adminNickname,
				custom.getCreatedAt(),
				custom.getStartedAt(),
				holdCreatedAt,
				holdReason,
				custom.getCompletedAt(),
				custom.getCanceledAt(),
				dogName,
				dogGender,
				dogBirth,
				personalityNames,
				dogImageUrls);
		} catch (IllegalArgumentException e) {
			log.warn("⚠️ 커스텀 단일 상세 조회 실패 - {}", e.getMessage());
			throw e;
		} catch (Exception e) {
			log.error("❌ 커스텀 단일 상세 조회 중 서버 오류 - customId: {}", customId, e);
			throw e;
		}
	}

	/**
	 * 📍 커스텀 요청 상태를 '진행중(IN_PROGRESS)'으로 변경하는 메서드
	 * - 커스텀 요청 ID로 해당 요청을 조회하고 존재하지 않으면 예외 발생
	 * - 상태를 IN_PROGRESS로 변경하고, 관리자 ID와 시작 일자를 설정
	 * - 해당 커스텀 요청에 연결된 강아지의 상태를 APPROVED로 변경
	 * - 변경된 커스텀 요청 정보를 담은 응답 DTO를 반환
	 *
	 * @param customId 변경할 커스텀 요청의 ID
	 * @param dto      관리자 ID를 포함한 상태 변경 요청 DTO
	 * @return 상태 변경 결과를 담은 UpdateCustomStatusInProgressResponseDto
	 * @throws IllegalArgumentException 존재하지 않는 커스텀 요청 또는 강아지일 경우
	 */
	@Transactional
	public UpdateCustomStatusInProgressResponseDto updateCustomStatusInProgress(Long customId,
		UpdateCustomStatusInProgressRequestDto dto) {
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
			custom.getStartedAt());

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
	 * @param dto      관리자 ID와 보류 사유를 포함한 상태 변경 요청 DTO
	 * @return 상태 변경 결과를 담은 UpdateCustomStatusHoldResponseDto
	 * @throws IllegalArgumentException 존재하지 않는 커스텀 요청 또는 강아지일 경우
	 * @throws IllegalStateException    이미 HOLD 또는 COMPLETED 상태일 경우
	 */
	@Transactional
	public UpdateCustomStatusHoldResponseDto updateCustomStatusHold(Long customId,
		UpdateCustomStatusHoldRequestDto dto) {

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
			hold.getCreatedAt());

		return responseDto;
	}

	/**
	 * 📍 커스텀 요청 상태를 '완료(COMPLETED)'로 변경하는 메서드
	 * - 커스텀 요청 ID로 요청 조회, 상태 검증
	 * - 렌더링 이미지 S3 업로드 후 Dog 엔티티에 저장
	 * - 커스텀 요청 상태 COMPLETED로 변경 및 완료일자 설정
	 * - 강아지 상태 APPROVED로 변경
	 *
	 * @param customId 변경할 커스텀 요청 ID
	 * @param dto      관리자 ID 및 렌더링 이미지 포함 DTO
	 * @return 완료 상태로 변경된 커스텀 요청 정보 DTO
	 * @throws IllegalArgumentException 존재하지 않는 커스텀 요청 또는 강아지일 경우
	 * @throws IOException              S3 업로드 실패 시
	 */
	@Transactional
	public UpdateCustomStatusCompletedResponseDto updateCustomStatusCompleted(
		Long customId,
		UpdateCustomStatusCompletedRequestDto dto) throws IOException {
		Long adminId = dto.getAdminId();
		MultipartFile renderedImage = dto.getRenderedImage();

		// 커스텀 요청 조회
		Custom custom = customRepository.findById(customId)
			.orElseThrow(() -> new IllegalArgumentException("커스텀 요청을 찾을 수 없습니다. ID=" + customId));

		if (custom.getStatus() != Status.IN_PROGRESS) {
			throw new IllegalStateException("현재 요청은 완료 처리할 수 없습니다. 상태: " + custom.getStatus());
		}

		// 렌더링 이미지 S3 업로드
		String uploadedUrl = s3Uploader.upload(renderedImage, "dog-images/rendered");

		// Dog 엔티티에 렌더링 이미지 URL 저장 및 상태 변경
		Dog dog = dogRepository.findById(custom.getDogId())
			.orElseThrow(() -> new IllegalArgumentException("강아지를 찾을 수 없습니다. ID=" + custom.getDogId()));
		dog.setRenderedUrl(uploadedUrl);
		dog.setStatus(com.ohgiraffers.togedaeng.backend.domain.dog.entity.Status.APPROVED);
		dogRepository.save(dog);

		// Custom 상태 및 완료일자 갱신
		custom.setStatus(Status.COMPLETED);
		custom.setAdminId(adminId);
		custom.setCompletedAt(LocalDateTime.now());
		customRepository.save(custom);

		// 응답 DTO 생성 및 반환
		return new UpdateCustomStatusCompletedResponseDto(
			custom.getId(),
			dog.getId(),
			custom.getAdminId(),
			custom.getStatus(),
			dog.getRenderedUrl(),
			custom.getCompletedAt());
	}

	/**
	 * 📍 커스텀 요청 상태를 '취소(CANCELLED)'로 변경하는 메서드
	 * - 커스텀 요청 ID로 해당 요청을 조회하고 존재하지 않으면 예외 발생
	 * - 상태를 CANCELLED로 변경하고, 관리자 ID와 취소 일자를 설정
	 * - 해당 커스텀 요청에 연결된 강아지의 상태를 SUSPENDED로 변경
	 * - 변경된 커스텀 요청 정보를 담은 응답 DTO를 반환
	 *
	 * @param customId 변경할 커스텀 요청의 ID
	 * @param dto      관리자 ID를 포함한 상태 변경 요청 DTO
	 * @return 상태 변경 결과를 담은 UpdateCustomStatusCanceledResponseDto
	 * @throws IllegalArgumentException 존재하지 않는 커스텀 요청 또는 강아지일 경우
	 */
	@Transactional
	public UpdateCustomStatusCanceledResponseDto updateCustomStatusCanceled(Long customId,
		UpdateCustomStatusCanceledRequestDto dto) {
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
			custom.getCanceledAt());

		return responseDto;
	}

	public long countPendingCustomRequests() {
		return customRepository.countByStatus(Status.PENDING);
	}
}

package com.ohgiraffers.togedaeng.backend.domain.dog.service;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import com.ohgiraffers.togedaeng.backend.domain.dog.dto.request.CreateDogRequestDto;
import com.ohgiraffers.togedaeng.backend.domain.dog.dto.request.UpdateDogCallNameRequestDto;
import com.ohgiraffers.togedaeng.backend.domain.dog.dto.request.UpdateDogNameRequestDto;
import com.ohgiraffers.togedaeng.backend.domain.dog.dto.response.CreateDogResponseDto;
import com.ohgiraffers.togedaeng.backend.domain.dog.dto.response.DogDetailResponseDto;
import com.ohgiraffers.togedaeng.backend.domain.dog.dto.response.DogListResponseDto;
import com.ohgiraffers.togedaeng.backend.domain.dog.dto.response.UpdateDogCallNameResponseDto;
import com.ohgiraffers.togedaeng.backend.domain.dog.dto.response.UpdateDogNameResponseDto;
import com.ohgiraffers.togedaeng.backend.domain.dog.entity.Dog;
import com.ohgiraffers.togedaeng.backend.domain.dog.entity.DogOwner;
import com.ohgiraffers.togedaeng.backend.domain.dog.entity.Status;
import com.ohgiraffers.togedaeng.backend.domain.dog.repository.DogOwnerRepository;
import com.ohgiraffers.togedaeng.backend.domain.dog.repository.DogRepository;
import com.ohgiraffers.togedaeng.backend.domain.personality.entity.PersonalityCombination;
import com.ohgiraffers.togedaeng.backend.domain.personality.repository.DogPersonalityRepository;
import com.ohgiraffers.togedaeng.backend.domain.personality.repository.PersonalityCombinationRepository;
import com.ohgiraffers.togedaeng.backend.domain.user.model.entity.User;
import com.ohgiraffers.togedaeng.backend.domain.user.repository.UserRepository;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class DogService {

	Logger log = LoggerFactory.getLogger(DogService.class);

	private final DogRepository dogRepository;
	private final PersonalityCombinationRepository personalityCombinationRepository;
	private final DogOwnerRepository dogOwnerRepository;
	private final DogPersonalityRepository dogPersonalityRepository;
	private final UserRepository userRepository;

	/**
	 * 📍 강아지 등록 및 관련 데이터 저장 서비스 메서드
	 * - 강아지 기본 정보 저장
	 * - 성격 조합 저장 (중복 성격 제거 및 조합 생성/조회)
	 * - DogOwner 엔티티 저장 (사용자와 강아지 연결)
	 * - 등록 완료 후 상세 정보를 담은 Response DTO 반환
	 *
	 * @param dto    강아지 등록 요청 DTO (이름, 성별, 생일, 성격 등)
	 * @param userId 현재 로그인된 사용자 ID
	 * @return 등록된 강아지 정보가 포함된 CreateDogResponseDto 객체
	 * @throws IllegalArgumentException 성격 ID가 누락되었거나 잘못된 경우
	 */
	@Transactional
	public CreateDogResponseDto createDogInfo(CreateDogRequestDto dto, Long userId) {

		log.info("🐶 [강아지 등록] 시작 - userId: {}", userId);

		// 1. PersonalityCombination 조회 또는 생성 (성격 1개 또는 2개)
		PersonalityCombination combination = personalityCombinationRepository
			.findByPersonalityId1AndPersonalityId2(dto.getPersonalityId1(), dto.getPersonalityId2())
			.orElseGet(() -> {
				// 기존 조합이 없으면 새로 생성
				PersonalityCombination newCombination = PersonalityCombination.builder()
					.personalityId1(dto.getPersonalityId1())
					.personalityId2(dto.getPersonalityId2())
					.build();
				return personalityCombinationRepository.save(newCombination);
			});

		log.debug("📌 성격 조합 조회/생성 완료 - combinationId: {}", combination.getId());

		// 2. Dog 엔티티 생성 (personality_combo_id 세팅 후 저장)
		Dog dog = Dog.builder()
			.name(dto.getName())
			.gender(dto.getGender())
			.birth(dto.getBirth())
			.status(Status.REGISTERED)
			.createdAt(LocalDateTime.now())
			.personalityComboId(combination.getId())
			.build();
		dogRepository.save(dog);
		log.debug("📌 강아지 저장 완료 - dogId: {}", dog.getId());

		// 3. DogOwner 저장 (userId 관리)
		DogOwner owner = new DogOwner(userId, dog.getId(), dto.getCallName(), true, LocalDateTime.now());
		dogOwnerRepository.save(owner);

		// 4. 응답 DTO 생성 및 반환
		CreateDogResponseDto responseDto = new CreateDogResponseDto(
			dog.getId(),
			userId,
			combination.getId(),
			dog.getName(),
			dog.getGender(),
			dog.getBirth(),
			dto.getCallName(),
			dog.getCreatedAt());

		log.info("✅ [강아지 등록] 완료 - dogId: {}", dog.getId());

		return responseDto;
	}

	/**
	 * 📍 강아지 전체 조회 서비스
	 * - 모든 강아지 정보를 페이지네이션으로 반환한다.
	 * - 각 강아지별로 대표 소유자(첫 DogOwner)의 닉네임을 ownerNickname으로 반환한다.
	 *
	 * @param page 페이지 번호 (0부터 시작)
	 * @param size 페이지 크기
	 * @return 페이지네이션된 강아지 리스트 (DogListResponseDto)
	 */
	public List<DogListResponseDto> getAllDogs(int page, int size) {
		log.info("🔍 강아지 전체 조회 서비스 시작 - page: {}, size: {}", page, size);

		try {
			Pageable pageable = PageRequest.of(page, size);
			Page<Dog> dogsPage = dogRepository.findAll(pageable);

			List<DogListResponseDto> result = new ArrayList<>();
			for (Dog dog : dogsPage.getContent()) {
				result.add(new DogListResponseDto(
					dog.getId(),
					dog.getName(),
					dog.getStatus(),
					dog.getCreatedAt(),
					dog.getDeletedAt()));
			}
			log.info("✅ 강아지 전체 조회 서비스 성공 - page: {}, size: {}, totalElements: {}, resultCount: {}",
				page, size, dogsPage.getTotalElements(), result.size());

			return result;
		} catch (IllegalArgumentException e) {
			log.warn("⚠️ 강아지 전체 조회 서비스 실패 - {}", e.getMessage());
			throw e;
		} catch (Exception e) {
			log.error("❌ 강아지 전체 조회 서비스 중 서버 오류", e);
			throw e;
		}
	}

	/**
	 * 📍 강아지 단일 상세 조회 서비스
	 * - 강아지 ID로 상세 정보를 반환한다.
	 * - 여러 소유자, 애칭, 성격, 이미지 등 부가 정보를 모두 조합한다.
	 *
	 * @param dogId 조회할 강아지 ID
	 * @return 강아지 상세 정보 (DogDetailResponseDto)
	 */
	public DogDetailResponseDto getDogById(Long dogId) {
		log.info("🔍 강아지 단일 상세 조회 서비스 시작 - dogId: {}", dogId);

		try {
			Dog dog = dogRepository.findById(dogId)
				.orElseThrow(() -> new IllegalArgumentException("해당 강아지가 존재하지 않습니다."));

			// DogOwner(여러 명)
			List<DogOwner> dogOwners = dogOwnerRepository.findAll().stream()
				.filter(o -> o.getDogId().equals(dogId)).collect(Collectors.toList());
			List<String> ownerNicknames = new ArrayList<>();
			List<String> callNames = new ArrayList<>();
			for (DogOwner owner : dogOwners) {
				User user = userRepository.findById(owner.getUserId()).orElse(null);
				if (user != null)
					ownerNicknames.add(user.getNickname());
				callNames.add(owner.getName());
			}

			List<String> personalities = new ArrayList<>();
			Long comboId = dog.getPersonalityComboId();
			if (comboId != null) {
				personalityCombinationRepository.findById(comboId).ifPresent(comb -> {
					if (comb.getPersonalityId1() != null) {
						dogPersonalityRepository.findById(comb.getPersonalityId1())
							.ifPresent(p -> personalities.add(p.getName()));
					}
					if (comb.getPersonalityId2() != null) {
						dogPersonalityRepository.findById(comb.getPersonalityId2())
							.ifPresent(p -> personalities.add(p.getName()));
					}
				});
			}

			DogDetailResponseDto result = new DogDetailResponseDto(
				dog.getId(),
				dog.getName(),
				dog.getGender(),
				dog.getBirth(),
				personalities,
				callNames,
				dog.getStatus(),
				ownerNicknames,
				dog.getCreatedAt(),
				dog.getUpdatedAt(),
				dog.getDeletedAt(),
				dog.getRenderedUrl());
			log.info("✅ 강아지 단일 상세 조회 서비스 성공 - dogId: {}", dogId);

			return result;
		} catch (IllegalArgumentException e) {
			log.warn("⚠️ 강아지 단일 상세 조회 서비스 실패 - {}", e.getMessage());
			throw e;
		} catch (Exception e) {
			log.error("❌ 강아지 단일 상세 조회 서비스 중 서버 오류 - dogId: {}", dogId, e);
			throw e;
		}
	}

	/**
	 * 📍 강아지 이름 수정 서비스 메서드
	 * - 강아지 ID로 엔티티를 조회하고 존재 여부를 확인
	 * - DogOwner 테이블에서 해당 강아지의 소유자가 현재 사용자(userId)인지 검증
	 * - 소유자 검증 후 이름과 수정일(updatedAt)을 갱신하고 저장
	 * - 수정 완료 시 수정된 강아지 정보를 담은 Response DTO 반환
	 *
	 * @param dogId  수정할 강아지의 ID
	 * @param dto    강아지 이름 수정 요청 DTO (newName 포함)
	 * @param userId 현재 로그인된 사용자 ID
	 * @return 수정된 강아지 정보를 담은 UpdateDogNameResponseDto
	 * @throws IllegalArgumentException 강아지가 존재하지 않거나 잘못된 ID인 경우
	 * @throws SecurityException        요청 사용자가 강아지의 소유자가 아닌 경우
	 * @throws RuntimeException         이름 수정 처리 중 서버 오류 발생 시
	 */
	@Transactional
	public UpdateDogNameResponseDto updateDogName(Long dogId, UpdateDogNameRequestDto dto, Long userId) {
		log.info("🔄 강아지 이름 수정 요청 - dogId: {}, userId: {}, newName: {}", dogId, userId, dto.getNewName());

		try {
			// 강아지 엔티티 조회
			Dog dog = dogRepository.findById(dogId)
				.orElseThrow(() -> {
					log.warn("❌ 강아지 조회 실패 - dogId: {}", dogId);
					return new IllegalArgumentException("해당 강아지가 존재하지 않습니다.");
				});

			// 소유자 권한 체크
			boolean isOwner = dogOwnerRepository.existsByDogIdAndUserId(dogId, userId);
			if (!isOwner) {
				log.warn("🚫 권한 없음 - 요청 userId: {}, dogId: {}", userId, dogId);
				throw new SecurityException("본인 소유의 강아지만 이름을 수정할 수 있습니다.");
			}

			// 이름 수정
			dog.setName(dto.getNewName());
			dog.setUpdatedAt(LocalDateTime.now());
			dogRepository.save(dog);

			log.info("✅ 강아지 이름 수정 성공 - dogId: {}, updatedName: {}", dogId, dog.getName());
			return new UpdateDogNameResponseDto(dog.getId(), dog.getName(), dog.getUpdatedAt());

		} catch (IllegalArgumentException | SecurityException e) {
			log.error("⚠️ 강아지 이름 수정 실패 - {}", e.getMessage());
			throw e;
		} catch (Exception e) {
			log.error("❌ 강아지 이름 수정 처리 중 예외 발생", e);
			throw new RuntimeException("강아지 이름 수정 중 서버 오류가 발생했습니다.", e);
		}
	}

	/**
	 * 📍 강아지 애칭 수정 서비스 메서드
	 * - 강아지 ID로 엔티티를 조회하고 존재 여부를 확인
	 * - DogOwner 테이블에서 해당 강아지의 소유자가 현재 사용자(userId)인지 검증
	 * - 소유자 검증 후 애칭과 수정일(updatedAt)을 갱신하고 저장
	 * - 수정 완료 시 수정된 강아지 정보를 담은 Response DTO 반환
	 *
	 * @param dogId  수정할 강아지의 ID
	 * @param dto    강아지 애칭 수정 요청 DTO (newCallName 포함)
	 * @param userId 현재 로그인된 사용자 ID
	 * @return 수정된 강아지 정보를 담은 UpdateDogNameResponseDto
	 * @throws IllegalArgumentException 강아지가 존재하지 않거나 잘못된 ID인 경우
	 * @throws SecurityException        요청 사용자가 강아지의 소유자가 아닌 경우
	 * @throws RuntimeException         애칭 수정 처리 중 서버 오류 발생 시
	 */
	@Transactional
	public UpdateDogCallNameResponseDto updateDogCallName(Long dogId, UpdateDogCallNameRequestDto dto, Long userId) {
		log.info("🔄 강아지 애칭 수정 요청 - dogId: {}, userId: {}, newCallName: {}", dogId, userId, dto.getNewCallName());

		try {
			// DogOwner 엔티티 조회
			DogOwner dogOwner = dogOwnerRepository.findByDogIdAndUserId(dogId, userId)
				.orElseThrow(() -> {
					log.warn("❌ DogOwner 조회 실패 - dogId: {}, userId: {}", dogId, userId);
					return new IllegalArgumentException("해당 강아지의 소유자 정보가 존재하지 않습니다.");
				});

			// 소유자 권한 체크
			boolean isOwner = dogOwnerRepository.existsByDogIdAndUserId(dogId, userId);
			if (!isOwner) {
				log.warn("🚫 권한 없음 - 요청 userId: {}, dogId: {}", userId, dogId);
				throw new SecurityException("본인 소유의 강아지만 애칭을 수정할 수 있습니다.");
			}

			// 애칭 수정
			dogOwner.setName(dto.getNewCallName());
			dogOwner.setUpdatedAt(LocalDateTime.now());
			dogOwnerRepository.save(dogOwner);

			log.info("✅ 강아지 애칭 수정 성공 - dogId: {}, updatedCallName: {}", dogId, dogOwner.getName());
			return new UpdateDogCallNameResponseDto(dogId, dogOwner.getName(), dogOwner.getUpdatedAt());

		} catch (IllegalArgumentException e) {
			log.error("⚠️ 강아지 애칭 수정 실패 - {}", e.getMessage());
			throw e;
		} catch (Exception e) {
			log.error("❌ 강아지 애칭 수정 처리 중 예외 발생", e);
			throw new RuntimeException("강아지 애칭 수정 중 서버 오류가 발생했습니다.", e);
		}
	}
}

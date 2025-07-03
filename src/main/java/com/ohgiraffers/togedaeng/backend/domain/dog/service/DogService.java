package com.ohgiraffers.togedaeng.backend.domain.dog.service;

import java.time.LocalDateTime;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import com.ohgiraffers.togedaeng.backend.domain.dog.dto.request.CreateDogRequestDto;
import com.ohgiraffers.togedaeng.backend.domain.dog.dto.request.UpdateDogNameRequestDto;
import com.ohgiraffers.togedaeng.backend.domain.dog.dto.response.CreateDogResponseDto;
import com.ohgiraffers.togedaeng.backend.domain.dog.dto.response.UpdateDogNameResponseDto;
import com.ohgiraffers.togedaeng.backend.domain.dog.entity.Dog;
import com.ohgiraffers.togedaeng.backend.domain.dog.entity.DogOwner;
import com.ohgiraffers.togedaeng.backend.domain.dog.entity.Status;
import com.ohgiraffers.togedaeng.backend.domain.dog.repository.DogOwnerRepository;
import com.ohgiraffers.togedaeng.backend.domain.dog.repository.DogRepository;
import com.ohgiraffers.togedaeng.backend.domain.personality.entity.PersonalityCombination;
import com.ohgiraffers.togedaeng.backend.domain.personality.repository.PersonalityCombinationRepository;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class DogService {

	Logger log = LoggerFactory.getLogger(DogService.class);

	private final DogRepository dogRepository;
	private final PersonalityCombinationRepository personalityCombinationRepository;
	private final DogOwnerRepository dogOwnerRepository;

	/**
	 * 📍 강아지 등록 및 관련 데이터 저장 서비스 메서드
	 * - 강아지 기본 정보 저장
	 * - 성격 조합 저장 (중복 성격 제거 및 조합 생성/조회)
	 * - DogOwner 엔티티 저장 (사용자와 강아지 연결)
	 * - 등록 완료 후 상세 정보를 담은 Response DTO 반환
	 *
	 * @param dto 강아지 등록 요청 DTO (이름, 성별, 생일, 성격 등)
	 * @param userId 현재 로그인된 사용자 ID
	 * @return 등록된 강아지 정보가 포함된 CreateDogResponseDto 객체
	 * @throws IllegalArgumentException 성격 ID가 누락되었거나 잘못된 경우
	 */
	@Transactional
	public CreateDogResponseDto createDogInfo(CreateDogRequestDto dto, Long userId) {

		log.info("🐶 [강아지 등록] 시작 - userId: {}", userId);

		// 1. 강아지 엔티티 저장
		Dog dog = dogRepository.save(
			Dog.builder()
				.name(dto.getName())
				.gender(dto.getGender())
				.birth(dto.getBirth())
				.status(Status.REGISTERED)
				.createdAt(LocalDateTime.now())
				.build()
		);
		log.debug("📌 강아지 저장 완료 - dogId: {}", dog.getId());

		// 2. 성격 조합 처리
		Long p1 = dto.getPersonalityId1();
		Long p2 = dto.getPersonalityId2();

		if (p1 == null) {
			throw new IllegalArgumentException("성격 하나는 반드시 선택해야 합니다.");
		}

		if (p2 != null && p1.equals(p2)) {
			p2 = null; // 중복 제거
		}

		Long first = (p2 == null || p1 < p2) ? p1 : p2;
		Long second = (p2 == null || p1 < p2) ? p2 : p1;

		PersonalityCombination combination = personalityCombinationRepository
			.findByDogIdAndPersonalityId1AndPersonalityId2(dog.getId(), first, second)
			.orElseGet(() -> {
				PersonalityCombination newCombo = new PersonalityCombination();
				newCombo.setDogId(dog.getId());
				newCombo.setPersonalityId1(first);
				newCombo.setPersonalityId2(second);
				return personalityCombinationRepository.save(newCombo);
			});

		combination.setDogId(dog.getId());
		personalityCombinationRepository.save(combination);
		log.debug("🧠 성격 조합 저장 완료 - dogId: {}, p1: {}, p2: {}", dog.getId(), first, second);

		// 3. DogOwner 저장
		DogOwner owner = new DogOwner(userId, dog.getId(), dto.getCallName(), LocalDateTime.now());
		dogOwnerRepository.save(owner);

		log.debug("👤 DogOwner 저장 완료 - userId: {}, dogId: {}", userId, dog.getId());

		log.info("✅ [강아지 등록] 완료 - dogId: {}", dog.getId());

		// 4. ResponseDto 생성
		CreateDogResponseDto responseDto = new CreateDogResponseDto(
			dog.getId(),
			userId,
			combination.getId(),
			dog.getName(),
			dog.getGender(),
			dog.getBirth(),
			dto.getCallName(),
			dog.getCreatedAt()
		);

		return responseDto;
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
	 * @throws SecurityException 요청 사용자가 강아지의 소유자가 아닌 경우
	 * @throws RuntimeException 이름 수정 처리 중 서버 오류 발생 시
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

}

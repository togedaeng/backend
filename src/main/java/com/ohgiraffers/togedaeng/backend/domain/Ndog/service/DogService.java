package com.ohgiraffers.togedaeng.backend.domain.Ndog.service;

import java.time.LocalDateTime;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import com.ohgiraffers.togedaeng.backend.domain.Ndog.dto.request.CreateDogRequestDto;
import com.ohgiraffers.togedaeng.backend.domain.Ndog.dto.response.CreateDogResponseDto;
import com.ohgiraffers.togedaeng.backend.domain.Ndog.entity.Dog;
import com.ohgiraffers.togedaeng.backend.domain.Ndog.entity.DogOwner;
import com.ohgiraffers.togedaeng.backend.domain.Ndog.entity.Status;
import com.ohgiraffers.togedaeng.backend.domain.Ndog.repository.DogOwnerRepository;
import com.ohgiraffers.togedaeng.backend.domain.Ndog.repository.DogRepository;
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
}

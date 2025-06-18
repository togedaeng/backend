package com.ohgiraffers.togedaeng.backend.domain.dog.service;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import com.ohgiraffers.togedaeng.backend.domain.dog.dto.request.DeleteDogRequestDto;
import com.ohgiraffers.togedaeng.backend.domain.dog.dto.request.DogRequestDto;
import com.ohgiraffers.togedaeng.backend.domain.dog.dto.request.UpdateDogCallNameRequestDto;
import com.ohgiraffers.togedaeng.backend.domain.dog.dto.request.UpdateDogNameRequestDto;
import com.ohgiraffers.togedaeng.backend.domain.dog.dto.response.DeleteDogResponseDto;
import com.ohgiraffers.togedaeng.backend.domain.dog.dto.response.DogResponseDto;
import com.ohgiraffers.togedaeng.backend.domain.dog.dto.response.UpdateDogCallNameResponseDto;
import com.ohgiraffers.togedaeng.backend.domain.dog.dto.response.UpdateDogNameResponseDto;
import com.ohgiraffers.togedaeng.backend.domain.dog.entity.Dog;
import com.ohgiraffers.togedaeng.backend.domain.dog.entity.Status;
import com.ohgiraffers.togedaeng.backend.domain.dog.repository.DogRepository;
import com.ohgiraffers.togedaeng.backend.domain.personality.entity.PersonalityCombination;
import com.ohgiraffers.togedaeng.backend.domain.personality.repository.DogPersonalityRepository;
import com.ohgiraffers.togedaeng.backend.domain.personality.repository.PersonalityCombinationRepository;

@Service
public class DogService {

	Logger log = LoggerFactory.getLogger(DogService.class);

	private final DogRepository dogRepository;
	private final DogPersonalityRepository dogPersonalityRepository;
	private final PersonalityCombinationRepository personalityCombinationRepository;

	public DogService(DogRepository dogRepository, PersonalityCombinationRepository personalityCombinationRepository,
		DogPersonalityRepository dogPersonalityRepository) {
		this.dogRepository = dogRepository;
		this.personalityCombinationRepository = personalityCombinationRepository;
		this.dogPersonalityRepository = dogPersonalityRepository;
	}

	/**
	 * 📍 기본 강아지 등록
	 * @param dto 강아지 등록 DTO
	 * @return 등록된 강아지 DTO 변환
	 */
	public DogResponseDto createDog(DogRequestDto dto) {

		// 유저 아이디로 유저 정보 찾기

		Long personalityId1 = dto.getPersonalityId1();  // 필수
		Long personalityId2 = dto.getPersonalityId2();  // null 가능

		if (personalityId1 == null) {
			throw new IllegalArgumentException("성격 하나는 반드시 선택해야 함");
		}

		// 같은 값 두 번 선택한 경우 -> 하나만 사용
		if (personalityId2 != null && personalityId1.equals(personalityId2)) {
			personalityId2 = null;
		}

		// 정렬 (순서에 상관없이 동일한 조합으로 판단)
		Long first = (personalityId2 == null || personalityId1 < personalityId2) ? personalityId1 : personalityId2;
		Long second = (personalityId2 == null || personalityId1 < personalityId2) ? personalityId2 : personalityId1;

		// 조합 조회 or 생성
		PersonalityCombination combination = personalityCombinationRepository
			.findByPersonalityId1AndPersonalityId2(first, second)
			.orElseGet(() -> {
				PersonalityCombination newCombo = new PersonalityCombination();
				newCombo.setPersonalityId1(first);
				newCombo.setPersonalityId2(second); // p2가 null이면 null 저장됨
				return personalityCombinationRepository.save(newCombo);
			});

		try {
			Dog dog = Dog.builder()
				.userId(dto.getUserId())
				.personalityCombinationId(combination.getId())
				.name(dto.getName())
				.gender(dto.getGender())
				.birth(LocalDate.now())
				.type(dto.getType())
				.callName(dto.getCallName())
				.status(dto.getStatus())
				.createdAt(LocalDateTime.now())
				.build();

			Dog savedDog = dogRepository.save(dog);
			log.info("Creating new dog: {}", dto.getName());

			return new DogResponseDto(
				savedDog.getId(),
				savedDog.getUserId(),
				savedDog.getPersonalityCombinationId(),
				savedDog.getName(),
				savedDog.getGender(),
				savedDog.getBirth(),
				savedDog.getType(),
				savedDog.getCallName(),
				savedDog.getStatus(),
				savedDog.getCreatedAt(),
				savedDog.getUpdatedAt(),
				savedDog.getDeletedAt()
			);
		} catch (Exception e) {
			e.printStackTrace();
			return null;
		}
	}

	/**
	 * 📍 강아지 전체 조회
	 * @return 모든 강아지 리스트
	 */
	public List<DogResponseDto> getAllDogs() {
		List<Dog> dogs = dogRepository.findAll();
		List<DogResponseDto> dogResponseDtos = new ArrayList<>();

		for (Dog dog : dogs) {
			dogResponseDtos.add(new DogResponseDto(
				dog.getId(),
				dog.getUserId(),
				dog.getPersonalityCombinationId(),
				dog.getName(),
				dog.getGender(),
				dog.getBirth(),
				dog.getType(),
				dog.getCallName(),
				dog.getStatus(),
				dog.getCreatedAt(),
				dog.getUpdatedAt(),
				dog.getDeletedAt()
			));
		}

		log.info("Get all dogs: {}", dogResponseDtos);

		return dogResponseDtos;
	}

	/**
	 * 📍 강아지 단일 조회
	 * @param id 강아지 id
	 * @return 강아지 정보 DTO 변환
	 */
	public DogResponseDto getDogById(Long id) {
		Dog dog = dogRepository.findById(id).orElse(null);
		log.info("Get dog by id: {}", id);

		return new DogResponseDto(
			dog.getId(),
			dog.getUserId(),
			dog.getPersonalityCombinationId(),
			dog.getName(),
			dog.getGender(),
			dog.getBirth(),
			dog.getType(),
			dog.getCallName(),
			dog.getStatus(),
			dog.getCreatedAt(),
			dog.getUpdatedAt(),
			dog.getDeletedAt()
		);

	}

	/**
	 * 📍 강아지 이름 수정
	 * @param id 강아지 id
	 * @param dto 강아지 id, 수정할 이름
	 * @return 수정된 강아지 이름 정보 (id, 이름, 수정 시각)
	 */
	public UpdateDogNameResponseDto updateDogName(Long id, UpdateDogNameRequestDto dto) {
		Dog dog = dogRepository.findById(id).orElseThrow(() ->
			new IllegalArgumentException("Dog not found"));

		log.info("Update dog name: {}", dto.getNewName());

		dog.setName(dto.getNewName());
		dog.setUpdatedAt(LocalDateTime.now());

		Dog updatedDog = dogRepository.save(dog);

		return new UpdateDogNameResponseDto(
			updatedDog.getId(),
			updatedDog.getName(),
			updatedDog.getUpdatedAt()
		);
	}

	/**
	 * 📍 강아지 애칭 수정
	 * @param id 강아지 id
	 * @param dto 강아지 id, 수정할 주인 애칭
	 * @return 수정된 강아지 애칭 정보 (id, 애칭, 수정 시각)
	 */
	public UpdateDogCallNameResponseDto updateDogCallName(Long id, UpdateDogCallNameRequestDto dto) {
		Dog dog = dogRepository.findById(id).orElseThrow(() ->
			new IllegalArgumentException("Dog not found"));

		log.info("Update call name: {}", dto.getNewCallName());

		dog.setCallName(dto.getNewCallName());
		dog.setUpdatedAt(LocalDateTime.now());

		Dog updatedDog = dogRepository.save(dog);

		return new UpdateDogCallNameResponseDto(
			updatedDog.getId(),
			updatedDog.getCallName(),
			updatedDog.getUpdatedAt()
		);
	}

	/**
	 * 📍 강아지 삭제
	 * @param id 강아지 id
	 * @param dto 강아지 id
	 * @return 삭제된 강아지 정보 (id, 이름, 상태(INACTIVE), 삭제일자)
	 */
	public DeleteDogResponseDto deleteDog(Long id, DeleteDogRequestDto dto) {
		Dog dog = dogRepository.findById(id).orElseThrow(() ->
			new IllegalArgumentException("Dog not found"));

		log.info("Delete dog: {}", dto.getDogId());

		dog.setStatus(Status.INACTIVE);
		dog.setDeletedAt(LocalDateTime.now());

		Dog updatedDog = dogRepository.save(dog);

		return new DeleteDogResponseDto(
			updatedDog.getId(),
			updatedDog.getName(),
			updatedDog.getStatus(),
			updatedDog.getDeletedAt()
		);
	}
}

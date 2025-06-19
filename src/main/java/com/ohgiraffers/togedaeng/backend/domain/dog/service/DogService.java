package com.ohgiraffers.togedaeng.backend.domain.dog.service;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import com.ohgiraffers.togedaeng.backend.domain.condition.entity.Condition;
import com.ohgiraffers.togedaeng.backend.domain.condition.repository.ConditionRepository;
import com.ohgiraffers.togedaeng.backend.domain.dog.dto.request.CreateDogRequestDto;
import com.ohgiraffers.togedaeng.backend.domain.dog.dto.request.DeleteDogRequestDto;
import com.ohgiraffers.togedaeng.backend.domain.dog.dto.request.UpdateDogCallNameRequestDto;
import com.ohgiraffers.togedaeng.backend.domain.dog.dto.request.UpdateDogNameRequestDto;
import com.ohgiraffers.togedaeng.backend.domain.dog.dto.request.UpdateDogPersonalityRequestDto;
import com.ohgiraffers.togedaeng.backend.domain.dog.dto.response.CreateDogResponseDto;
import com.ohgiraffers.togedaeng.backend.domain.dog.dto.response.DeleteDogResponseDto;
import com.ohgiraffers.togedaeng.backend.domain.dog.dto.response.DogResponseDto;
import com.ohgiraffers.togedaeng.backend.domain.dog.dto.response.UpdateDogCallNameResponseDto;
import com.ohgiraffers.togedaeng.backend.domain.dog.dto.response.UpdateDogIsMainResponseDto;
import com.ohgiraffers.togedaeng.backend.domain.dog.dto.response.UpdateDogNameResponseDto;
import com.ohgiraffers.togedaeng.backend.domain.dog.dto.response.UpdateDogPersonalityResponseDto;
import com.ohgiraffers.togedaeng.backend.domain.dog.entity.Dog;
import com.ohgiraffers.togedaeng.backend.domain.dog.entity.Status;
import com.ohgiraffers.togedaeng.backend.domain.dog.entity.Step;
import com.ohgiraffers.togedaeng.backend.domain.dog.repository.DogRepository;
import com.ohgiraffers.togedaeng.backend.domain.personality.entity.PersonalityCombination;
import com.ohgiraffers.togedaeng.backend.domain.personality.repository.DogPersonalityRepository;
import com.ohgiraffers.togedaeng.backend.domain.personality.repository.PersonalityCombinationRepository;

import jakarta.transaction.Transactional;

@Service
public class DogService {

	Logger log = LoggerFactory.getLogger(DogService.class);

	private final DogRepository dogRepository;
	private final PersonalityCombinationRepository personalityCombinationRepository;
	private final DogPersonalityRepository dogPersonalityRepository;
	private final ConditionRepository conditionRepository;

	public DogService(DogRepository dogRepository, PersonalityCombinationRepository personalityCombinationRepository,
		DogPersonalityRepository dogPersonalityRepository, ConditionRepository conditionRepository) {
		this.dogRepository = dogRepository;
		this.personalityCombinationRepository = personalityCombinationRepository;
		this.dogPersonalityRepository = dogPersonalityRepository;
		this.conditionRepository = conditionRepository;
	}

	/**
	 * 📍 기본 강아지 등록
	 * @param dto 강아지 등록 DTO
	 * @return 등록된 강아지 DTO 변환
	 */
	@Transactional
	public CreateDogResponseDto createDog(CreateDogRequestDto dto) {

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

		boolean existsDog = dogRepository.existsByUserIdAndDeletedAtIsNull(dto.getUserId());

		// 대표 강아지 여부 결정
		int isMainDog = existsDog ? 0 : 1;

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
				.step(Step.PUPPY)
				.isMainDog(isMainDog)
				.createdAt(LocalDateTime.now())
				.build();

			Dog savedDog = dogRepository.save(dog);
			log.info("Creating new dog: {}", dto.getName());

			Condition condition = new Condition();
			condition.setDogId(savedDog.getId());
			condition.setFullness(50);
			condition.setWaterful(50);
			condition.setAffection(50);
			condition.setLevel(1);
			condition.setExp(0);
			condition.setUpdatedAt(LocalDateTime.now());
			conditionRepository.save(condition);

			return new CreateDogResponseDto(
				savedDog.getId(),
				savedDog.getUserId(),
				savedDog.getPersonalityCombinationId(),
				savedDog.getName(),
				savedDog.getGender(),
				savedDog.getBirth(),
				savedDog.getType(),
				savedDog.getCallName(),
				savedDog.getStatus(),
				savedDog.getStep(),
				savedDog.getIsMainDog(),
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
	@Transactional
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
				dog.getStep(),
				dog.getIsMainDog(),
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
	@Transactional
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
			dog.getStep(),
			dog.getIsMainDog(),
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
	@Transactional
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
	@Transactional
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
	 * 📍 강아지 성격 수정
	 * @param id 강아지 id
	 * @param dto 강아지 id, 바꿀 성격 id 1, 바꿀 성격 id 2
	 * @return 수정된 강아지 성격 정보 (id, 성격 조합 id, 바뀐 성격 이름, 수정 일자)
	 */
	@Transactional
	public UpdateDogPersonalityResponseDto updateDogPersonality(Long id, UpdateDogPersonalityRequestDto dto) {
		Dog dog = dogRepository.findById(id).orElseThrow(() ->
			new IllegalArgumentException("Dog not found"));

		Long newPersonalityId1 = dto.getNewPersonalityId1();  // 필수
		Long newPersonalityId2 = dto.getNewPersonalityId2();  // null 가능

		if (newPersonalityId1 == null) {
			throw new IllegalArgumentException("성격 하나는 반드시 선택해야 함");
		}

		// 같은 값 두 번 선택한 경우 -> 하나만 사용
		if (newPersonalityId2 != null && newPersonalityId1.equals(newPersonalityId2)) {
			newPersonalityId2 = null;
		}

		// 정렬 (순서에 상관없이 동일한 조합으로 판단)
		Long first = (newPersonalityId2 == null || newPersonalityId1 < newPersonalityId2) ? newPersonalityId1 :
			newPersonalityId2;
		Long second = (newPersonalityId2 == null || newPersonalityId1 < newPersonalityId2) ? newPersonalityId2 :
			newPersonalityId1;

		// 조합 조회 or 생성
		PersonalityCombination combination = personalityCombinationRepository
			.findByPersonalityId1AndPersonalityId2(first, second)
			.orElseGet(() -> {
				PersonalityCombination newCombo = new PersonalityCombination();
				newCombo.setPersonalityId1(first);
				newCombo.setPersonalityId2(second); // p2가 null이면 null 저장됨
				return personalityCombinationRepository.save(newCombo);
			});

		// 강아지에 조합 ID 설정
		dog.setPersonalityCombinationId(combination.getId());
		dog.setUpdatedAt(LocalDateTime.now());

		// 저장
		Dog updatedDog = dogRepository.save(dog);

		// 성격 이름 조회
		List<String> personalityNames = new ArrayList<>();
		dogPersonalityRepository.findById(first)
			.ifPresent(p -> personalityNames.add(p.getName()));
		if (second != null) {
			dogPersonalityRepository.findById(second)
				.ifPresent(p -> personalityNames.add(p.getName()));
		}

		// 응답
		return new UpdateDogPersonalityResponseDto(
			updatedDog.getId(),
			combination.getId(),
			personalityNames,
			updatedDog.getUpdatedAt()
		);
	}

	/**
	 * 📍 대표 반려견 설정
	 * @param dogId 강아지 id
	 * @param userId 유저 id (로그인한 사용자 아이디로 추후 수정 예정)
	 * @return 대표 강아지 정보 (id, 메인 강아지 여부)
	 */
	@Transactional
	public UpdateDogIsMainResponseDto updateDogIsMain(Long dogId, Long userId) {

		// 1. 해당 강아지가 유저 소유인지 확인
		Dog selectedDog = dogRepository.findByIdAndUserIdAndDeletedAtIsNull(dogId, userId)
			.orElseThrow(() -> new IllegalArgumentException("해당 강아지를 찾을 수 없습니다."));

		// 2. 기존 대표 강아지를 찾아서 비대표로 설정
		dogRepository.findByUserIdAndIsMainDogAndDeletedAtIsNull(userId, 1)
			.ifPresent(existingMain -> {
				if (!existingMain.getId().equals(selectedDog.getId())) {
					existingMain.setIsMainDog(0);
					dogRepository.save(existingMain);
				}
			});

		// 3. 선택한 강아지를 대표로 설정
		selectedDog.setIsMainDog(1);
		Dog updatedDog = dogRepository.save(selectedDog);

		// 4. 결과 반환
		return new UpdateDogIsMainResponseDto(updatedDog.getId(), 1);
	}

	/**
	 * 📍 강아지 삭제
	 * @param id 강아지 id
	 * @param dto 강아지 id
	 * @return 삭제된 강아지 정보 (id, 이름, 상태(INACTIVE), 삭제일자)
	 */
	@Transactional
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

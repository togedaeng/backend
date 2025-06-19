package com.ohgiraffers.togedaeng.backend.domain.dog.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.ohgiraffers.togedaeng.backend.domain.dog.entity.Dog;

public interface DogRepository extends JpaRepository<Dog, Long> {
	boolean existsByUserIdAndDeletedAtIsNull(Long userId);

	Optional<Dog> findByIdAndUserIdAndDeletedAtIsNull(Long id, Long userId);

	Optional<Dog> findByUserIdAndIsMainDogAndDeletedAtIsNull(Long userId, int isMainDog);

}

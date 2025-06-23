package com.ohgiraffers.togedaeng.backend.domain.dog.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.ohgiraffers.togedaeng.backend.domain.dog.entity.Dog;

public interface DogRepository extends JpaRepository<Dog, Long> {
	boolean existsByUserIdAndDeletedAtIsNull(Long userId);
}

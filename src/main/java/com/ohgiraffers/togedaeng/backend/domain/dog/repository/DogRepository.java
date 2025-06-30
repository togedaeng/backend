package com.ohgiraffers.togedaeng.backend.domain.dog.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.ohgiraffers.togedaeng.backend.domain.dog.entity.Dog;
import com.ohgiraffers.togedaeng.backend.domain.dog.entity.Status;

public interface DogRepository extends JpaRepository<Dog, Long> {
	boolean existsByUserIdAndDeletedAtIsNull(Long userId);

	List<Dog> findByStatus(Status status);

	List<Dog> findAllByUserId(Long userId);
}

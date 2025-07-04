package com.ohgiraffers.togedaeng.backend.domain.dog.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.ohgiraffers.togedaeng.backend.domain.dog.entity.DogOwner;

public interface DogOwnerRepository extends JpaRepository<DogOwner, Long> {
	boolean existsByDogIdAndUserId(Long dogId, Long userId);

	Optional<DogOwner> findByDogIdAndUserId(Long dogId, Long userId);
}

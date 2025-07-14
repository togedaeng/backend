package com.ohgiraffers.togedaeng.backend.domain.dog.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import com.ohgiraffers.togedaeng.backend.domain.dog.entity.DogOwner;

public interface DogOwnerRepository extends JpaRepository<DogOwner, Long> {
	boolean existsByDogIdAndUserId(Long dogId, Long userId);

	Optional<DogOwner> findByDogIdAndUserId(Long dogId, Long userId);

	Optional<DogOwner> findFirstByDogId(Long dogId);

	DogOwner findByDogId(Long dogId);

	@Query("SELECT o.dogId FROM DogOwner o WHERE o.userId = :userId")
	Long findDogIdByUserId(Long userId); // userId로 dogId찾기

	List<DogOwner> findOwnerIdByDogId(Long dogId);
}

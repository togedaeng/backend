package com.ohgiraffers.togedaeng.backend.domain.personality.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.ohgiraffers.togedaeng.backend.domain.personality.entity.PersonalityCombination;

public interface PersonalityCombinationRepository extends JpaRepository<PersonalityCombination, Long> {
	Optional<PersonalityCombination> findByDogIdAndPersonalityId1AndPersonalityId2(Long dogId, Long personalityId1, Long personalityId2);
}

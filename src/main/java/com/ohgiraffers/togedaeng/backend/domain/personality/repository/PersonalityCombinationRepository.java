package com.ohgiraffers.togedaeng.backend.domain.personality.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.ohgiraffers.togedaeng.backend.domain.personality.entity.PersonalityCombination;

public interface PersonalityCombinationRepository extends JpaRepository<PersonalityCombination, Long> {
	Optional<PersonalityCombination> findByPersonalityId1AndPersonalityId2(Long personalityId1, Long personalityId2);
}

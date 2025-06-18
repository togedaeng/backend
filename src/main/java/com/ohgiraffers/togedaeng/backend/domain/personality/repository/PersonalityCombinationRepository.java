package com.ohgiraffers.togedaeng.backend.domain.personality.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.ohgiraffers.togedaeng.backend.domain.personality.entity.PersonalityCombination;

public interface PersonalityCombinationRepository extends JpaRepository<PersonalityCombination, Long> {
	@Query("SELECT pc FROM PersonalityCombination pc " +
		"WHERE (pc.personalityId1 = :id1 AND pc.personalityId2 = :id2) " +
		"   OR (pc.personalityId1 = :id2 AND pc.personalityId2 = :id1)")
	Optional<PersonalityCombination> findByPersonalityId1AndPersonalityId2(
		@Param("id1") Long id1, @Param("id2") Long id2);
}

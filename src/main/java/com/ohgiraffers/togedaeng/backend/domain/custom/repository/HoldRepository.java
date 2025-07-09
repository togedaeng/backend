package com.ohgiraffers.togedaeng.backend.domain.custom.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.ohgiraffers.togedaeng.backend.domain.custom.entity.Hold;

public interface HoldRepository extends JpaRepository<Hold, Long> {
	Hold findTopByCustomIdOrderByCreatedAtDesc(Long customId);

	List<Hold> findByCustomId(Long customId);
}

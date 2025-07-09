package com.ohgiraffers.togedaeng.backend.domain.custom.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.ohgiraffers.togedaeng.backend.domain.custom.entity.Hold;

public interface HoldRepository extends JpaRepository<Hold, Long> {
	Hold findTopByCustomIdOrderByCreatedAtDesc(Long customId);
}

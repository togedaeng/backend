package com.ohgiraffers.togedaeng.backend.domain.custom.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.ohgiraffers.togedaeng.backend.domain.custom.entity.Hold;
import java.util.Optional;

public interface HoldRepository extends JpaRepository<Hold, Long> {
  Hold findTopByCustomIdOrderByCreatedAtDesc(Long customId);
}

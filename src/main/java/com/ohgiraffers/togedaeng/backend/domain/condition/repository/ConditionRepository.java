package com.ohgiraffers.togedaeng.backend.domain.condition.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.ohgiraffers.togedaeng.backend.domain.condition.entity.Condition;

public interface ConditionRepository extends JpaRepository<Condition, Long> {
}

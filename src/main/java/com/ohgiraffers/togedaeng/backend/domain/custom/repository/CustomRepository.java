package com.ohgiraffers.togedaeng.backend.domain.custom.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.ohgiraffers.togedaeng.backend.domain.custom.entity.Custom;
import com.ohgiraffers.togedaeng.backend.domain.custom.entity.Status;

public interface CustomRepository extends JpaRepository<Custom, Long> {

	long countByStatus(Status status);

	List<Custom> findByDogId(Long dogId);

}

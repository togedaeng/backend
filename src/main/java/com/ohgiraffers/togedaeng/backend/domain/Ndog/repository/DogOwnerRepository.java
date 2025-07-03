package com.ohgiraffers.togedaeng.backend.domain.Ndog.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.ohgiraffers.togedaeng.backend.domain.Ndog.entity.DogOwner;

public interface DogOwnerRepository extends JpaRepository<DogOwner, Long> {
}

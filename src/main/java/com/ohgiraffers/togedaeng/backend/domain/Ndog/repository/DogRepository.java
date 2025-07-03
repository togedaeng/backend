package com.ohgiraffers.togedaeng.backend.domain.Ndog.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.ohgiraffers.togedaeng.backend.domain.Ndog.entity.Dog;

public interface DogRepository extends JpaRepository<Dog, Long> {
}

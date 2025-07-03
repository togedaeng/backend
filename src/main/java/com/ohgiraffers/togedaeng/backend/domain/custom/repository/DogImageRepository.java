package com.ohgiraffers.togedaeng.backend.domain.custom.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.ohgiraffers.togedaeng.backend.domain.custom.entity.DogImage;

public interface DogImageRepository extends JpaRepository<DogImage, Long> {
}

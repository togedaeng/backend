package com.ohgiraffers.togedaeng.backend.domain.personality.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.ohgiraffers.togedaeng.backend.domain.personality.entity.DogPersonality;

public interface DogPersonalityRepository extends JpaRepository<DogPersonality, Long> {


}

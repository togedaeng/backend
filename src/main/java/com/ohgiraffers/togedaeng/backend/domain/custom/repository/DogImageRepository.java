package com.ohgiraffers.togedaeng.backend.domain.custom.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.ohgiraffers.togedaeng.backend.domain.custom.entity.DogImage;

public interface DogImageRepository extends JpaRepository<DogImage, Long> {

  List<DogImage> findByCustomId(Long customId);
}

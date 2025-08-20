package com.ohgiraffers.togedaeng.backend.domain.dog.repository;


import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.ohgiraffers.togedaeng.backend.domain.dog.entity.Dog;

public interface DogRepository extends JpaRepository<Dog, Long> {
	@Query("""
    SELECT d
    FROM Dog d
    JOIN DogOwner o ON d.id = o.dogId
    WHERE o.userId = :userId
    AND d.deletedAt IS NULL
""")
	List<Dog> findAllByUserId(@Param("userId") Long userId);

    @Query("""
    SELECT d
    FROM Dog d
    JOIN DogOwner o ON d.id = o.dogId
    WHERE o.userId = :userId
    AND d.deletedAt IS NULL
""")
    List<Dog> findByUserId(Long userId);
}

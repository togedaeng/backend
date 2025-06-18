package com.ohgiraffers.togedaeng.backend.domain.user.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.ohgiraffers.togedaeng.backend.domain.user.model.entity.User;

@Repository
public interface UserRepository extends JpaRepository<User, Long> {
}

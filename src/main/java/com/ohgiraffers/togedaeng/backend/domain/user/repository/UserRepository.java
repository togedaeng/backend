package com.ohgiraffers.togedaeng.backend.domain.user.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.ohgiraffers.togedaeng.backend.domain.user.model.entity.User;
import com.ohgiraffers.togedaeng.backend.domain.user.model.entity.UserStatus;

@Repository
public interface UserRepository extends JpaRepository<User, Long> {
	Optional<User> findByProviderAndProviderId(String provider, String providerId);

	Optional<User> findByEmail(String email);

	boolean existsByNickname(String nickname);

	Optional<User> findByNickname(String nickname);

    List<User> findByStatus(UserStatus active);
}

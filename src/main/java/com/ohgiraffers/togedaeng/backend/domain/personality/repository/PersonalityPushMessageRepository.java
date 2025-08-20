package com.ohgiraffers.togedaeng.backend.domain.personality.repository;

import com.ohgiraffers.togedaeng.backend.domain.personality.entity.PersonalityPushMessage;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface PersonalityPushMessageRepository extends JpaRepository<PersonalityPushMessage, Long> {
}

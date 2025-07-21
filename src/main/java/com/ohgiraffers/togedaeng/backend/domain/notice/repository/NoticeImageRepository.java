package com.ohgiraffers.togedaeng.backend.domain.notice.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.ohgiraffers.togedaeng.backend.domain.notice.entity.NoticeImage;

public interface NoticeImageRepository extends JpaRepository<NoticeImage, Long> {
}

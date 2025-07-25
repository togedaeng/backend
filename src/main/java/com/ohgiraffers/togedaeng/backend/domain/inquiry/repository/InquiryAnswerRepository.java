package com.ohgiraffers.togedaeng.backend.domain.inquiry.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.ohgiraffers.togedaeng.backend.domain.inquiry.entity.InquiryAnswer;

public interface InquiryAnswerRepository extends JpaRepository<InquiryAnswer, Long> {
}

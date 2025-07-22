package com.ohgiraffers.togedaeng.backend.domain.inquiry.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import com.ohgiraffers.togedaeng.backend.domain.inquiry.controller.InquiryController;
import com.ohgiraffers.togedaeng.backend.domain.inquiry.repository.InquiryRepository;

import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Service
public class InquiryService {

	private final Logger log = LoggerFactory.getLogger(InquiryController.class);
	private final InquiryRepository inquiryRepository;

	// 문의 전체 조회

	// 문의 단일 조회

	// 문의 작성

	// 문의 답변 작성

	// 문의 수정 (답변 안 달렸을 때만)
}

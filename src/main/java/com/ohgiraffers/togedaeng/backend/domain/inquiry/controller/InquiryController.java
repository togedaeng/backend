package com.ohgiraffers.togedaeng.backend.domain.inquiry.controller;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.ohgiraffers.togedaeng.backend.domain.inquiry.service.InquiryService;

import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@RestController
@RequestMapping("/api/inquiry")
public class InquiryController {

	private final Logger log = LoggerFactory.getLogger(InquiryController.class);
	private final InquiryService inquiryService;

	// 문의 전체 조회

	// 문의 단일 조회

	// 문의 작성

	// 문의 답변 작성

	// 문의 수정 (답변 안 달렸을 때만)


}

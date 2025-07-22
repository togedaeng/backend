package com.ohgiraffers.togedaeng.backend.domain.inquiry.service;

import java.util.List;
import java.util.stream.Collectors;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.ohgiraffers.togedaeng.backend.domain.inquiry.controller.InquiryController;
import com.ohgiraffers.togedaeng.backend.domain.inquiry.dto.InquiryListResponseDto;
import com.ohgiraffers.togedaeng.backend.domain.inquiry.entity.Inquiry;
import com.ohgiraffers.togedaeng.backend.domain.inquiry.repository.InquiryRepository;

import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Service
public class InquiryService {

	private final Logger log = LoggerFactory.getLogger(InquiryController.class);
	private final InquiryRepository inquiryRepository;

	/**
	 * 📍 문의 전체 조회 서비스
	 * - 모든 문의 정보를 페이지네이션으로 반환한다.
	 * - 각 문의별로 작성자 닉네임을 함께 반환한다. (N+1 문제 해결)
	 *
	 * @param page 페이지 번호 (0부터 시작)
	 * @param size 페이지 크기
	 * @return 페이지네이션된 문의 리스트
	 */
	@Transactional(readOnly = true)
	public List<InquiryListResponseDto> getAllInquiries(int page, int size) {
		log.info("🔍 문의 전체 조회 서비스 시작 - page: {}, size: {}", page, size);
		Pageable pageable = PageRequest.of(page, size);

		Page<Inquiry> inquiriesPage = inquiryRepository.findAllWithUser(pageable);

		return inquiriesPage.getContent().stream()
			.map(InquiryListResponseDto::from)
			.collect(Collectors.toList());
	}

	// 문의 단일 조회

	// 문의 작성

	// 문의 답변 작성

	// 문의 수정 (답변 안 달렸을 때만)
}

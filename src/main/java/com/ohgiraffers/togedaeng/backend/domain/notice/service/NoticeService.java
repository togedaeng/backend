package com.ohgiraffers.togedaeng.backend.domain.notice.service;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import com.ohgiraffers.togedaeng.backend.domain.dog.dto.response.DogListResponseDto;
import com.ohgiraffers.togedaeng.backend.domain.dog.entity.Dog;
import com.ohgiraffers.togedaeng.backend.domain.notice.dto.response.NoticeListResponseDto;
import com.ohgiraffers.togedaeng.backend.domain.notice.entity.Notice;
import com.ohgiraffers.togedaeng.backend.domain.notice.repository.NoticeRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class NoticeService {

	private final Logger log = LoggerFactory.getLogger(NoticeService.class);
	private final NoticeRepository noticeRepository;

	/**
	 * 📍 공지 전체 조회 서비스
	 * - 모든 공지 정보를 페이지네이션으로 반환한다.
	 * - 각 공지별로 작성자(User)의 닉네임을 함께 반환한다.
	 *
	 * @param page 페이지 번호 (0부터 시작)
	 * @param size 페이지 크기
	 * @return 페이지네이션된 공지 리스트 (NoticeListResponseDto)
	 */
	public List<NoticeListResponseDto> getAllNotices(int page, int size) {
		log.info("🔍 공지 전체 조회 서비스 시작 - page: {}, size: {}", page, size);

		try {
			Pageable pageable = PageRequest.of(page, size);
			Page<Notice> noticesPage = noticeRepository.findAllWithUser(pageable);

			List<NoticeListResponseDto> result = noticesPage.getContent().stream()
				.map(notice -> new NoticeListResponseDto(
					notice.getId(),
					notice.getCategory(),
					notice.getTitle(),
					notice.getUser().getNickname(),
					notice.getCreatedAt()
				))
				.collect(Collectors.toList());

			log.info("✅ 공지 전체 조회 서비스 성공 - page: {}, size: {}, totalElements: {}, resultCount: {}",
				page, size, noticesPage.getTotalElements(), result.size());

			return result;
		} catch (IllegalArgumentException e) {
			log.warn("⚠️ 공지 전체 조회 서비스 실패 - {}", e.getMessage());
			throw e;
		} catch (Exception e) {
			log.error("❌ 공지 전체 조회 서비스 중 서버 오류", e);
			throw e;
		}
	}

	// 공지 단일 조회

	// 공지 작성

	// 공지 수정

	// 공지 삭제
}

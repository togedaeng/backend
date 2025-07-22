package com.ohgiraffers.togedaeng.backend.domain.inquiry.service;

import java.io.IOException;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import com.ohgiraffers.togedaeng.backend.domain.custom.service.S3Uploader;
import com.ohgiraffers.togedaeng.backend.domain.inquiry.controller.InquiryController;
import com.ohgiraffers.togedaeng.backend.domain.inquiry.dto.request.CreateInquiryAnswerRequestDto;
import com.ohgiraffers.togedaeng.backend.domain.inquiry.dto.request.CreateInquiryRequestDto;
import com.ohgiraffers.togedaeng.backend.domain.inquiry.dto.response.CreateInquiryResponseDto;
import com.ohgiraffers.togedaeng.backend.domain.inquiry.dto.response.InquiryAnswerDto;
import com.ohgiraffers.togedaeng.backend.domain.inquiry.dto.response.InquiryDetailResponseDto;
import com.ohgiraffers.togedaeng.backend.domain.inquiry.dto.response.InquiryListResponseDto;
import com.ohgiraffers.togedaeng.backend.domain.inquiry.entity.Inquiry;
import com.ohgiraffers.togedaeng.backend.domain.inquiry.entity.InquiryAnswer;
import com.ohgiraffers.togedaeng.backend.domain.inquiry.entity.InquiryImage;
import com.ohgiraffers.togedaeng.backend.domain.inquiry.entity.Status;
import com.ohgiraffers.togedaeng.backend.domain.inquiry.repository.InquiryAnswerRepository;
import com.ohgiraffers.togedaeng.backend.domain.inquiry.repository.InquiryRepository;
import com.ohgiraffers.togedaeng.backend.domain.user.model.entity.User;
import com.ohgiraffers.togedaeng.backend.domain.user.repository.UserRepository;

import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Service
public class InquiryService {

	private final Logger log = LoggerFactory.getLogger(InquiryController.class);

	private final InquiryRepository inquiryRepository;
	private final InquiryAnswerRepository inquiryAnswerRepository;
	private final UserRepository userRepository;
	private final S3Uploader s3Uploader;

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

	/**
	 * 📍 문의 단일 상세 조회 서비스
	 * - ID로 특정 문의를 조회하여 상세 정보를 반환한다.
	 * - 연관된 이미지 URL 리스트와 답변 정보를 함께 반환한다.
	 *
	 * @param inquiryId 조회할 문의 ID
	 * @return 문의 상세 정보 DTO
	 * @throws IllegalArgumentException 해당 ID의 문의가 없을 경우 발생
	 */
	@Transactional(readOnly = true)
	public InquiryDetailResponseDto getInquiryById(Long inquiryId) {
		log.info("🔍 문의 단일 상세 조회 서비스 시작 - inquiryId: {}", inquiryId);

		Inquiry inquiry = inquiryRepository.findInquiryDetailsById(inquiryId)
			.orElseThrow(() -> new IllegalArgumentException("해당 문의를 찾을 수 없습니다. id: " + inquiryId));

		log.info("✅ 문의 단일 상세 조회 서비스 성공 - inquiryId: {}", inquiryId);
		return InquiryDetailResponseDto.from(inquiry);
	}

	/**
	 * 📍 문의 작성 서비스 (다중 이미지 지원)
	 * - 전달받은 이미지 파일들을 S3에 업로드하고, 그 URL들을 포함하여 새로운 문의를 작성한다.
	 *
	 * @param requestDto 문의 작성에 필요한 데이터
	 * @param images     S3에 업로드할 이미지 파일 리스트 (null일 수 있음)
	 * @param userId     작성자 ID
	 * @return 작성된 문의의 상세 정보
	 */
	@Transactional
	public CreateInquiryResponseDto createInquiry(CreateInquiryRequestDto requestDto, List<MultipartFile> images, Long userId) {
		log.info("🚀 [문의 등록] 서비스 시작 - userId: {}", userId);

		User user = userRepository.findById(userId)
			.orElseThrow(() -> new IllegalArgumentException("사용자를 찾을 수 없습니다. id: " + userId));

		Inquiry newInquiry = Inquiry.builder()
			.user(user)
			.category(requestDto.getCategory())
			.title(requestDto.getTitle())
			.content(requestDto.getContent())
			.status(Status.WAITING) // 초기 상태는 WAITING
			.createdAt(LocalDateTime.now())
			.images(new ArrayList<>()) // images 리스트 초기화
			.build();

		if (images != null && !images.isEmpty()) {
			for (MultipartFile image : images) {
				try {
					String imageUrl = s3Uploader.upload(image, "inquiries"); // S3 'inquiries' 폴더에 저장
					InquiryImage inquiryImage = new InquiryImage(null, null, imageUrl);
					newInquiry.addImage(inquiryImage); // 연관관계 편의 메소드 사용
				} catch (IOException e) {
					log.error("❌ S3 이미지 업로드 중 오류 발생", e);
					throw new RuntimeException("이미지 업로드에 실패했습니다.");
				}
			}
		}

		Inquiry savedInquiry = inquiryRepository.save(newInquiry);
		log.info("✅ 문의 등록 서비스 성공 - inquiryId: {}", savedInquiry.getId());

		return CreateInquiryResponseDto.from(savedInquiry);
	}

	// 문의 수정 (답변 안 달렸을 때만)


	/**
	 * 📍 문의 답변 작성 서비스
	 * - 특정 문의에 대한 답변을 작성하고, 문의의 상태를 'ANSWERED'로 변경한다.
	 * - 답변은 관리자만 작성할 수 있다.
	 *
	 * @param inquiryId 답변을 달 문의 ID
	 * @param requestDto 답변 내용
	 * @param adminId 답변을 작성하는 관리자 ID
	 * @return 작성된 답변 정보 DTO
	 * @throws IllegalStateException 이미 답변이 달린 경우 발생
	 * @throws IllegalArgumentException 문의 또는 관리자 ID가 유효하지 않을 경우 발생
	 */
	@Transactional
	public InquiryAnswerDto createInquiryAnswer(Long inquiryId, CreateInquiryAnswerRequestDto requestDto, Long adminId) {
		log.info("🚀 [답변 등록] 서비스 시작 - inquiryId: {}, adminId: {}", inquiryId, adminId);

		Inquiry inquiry = inquiryRepository.findById(inquiryId)
			.orElseThrow(() -> new IllegalArgumentException("답변할 문의를 찾을 수 없습니다. id: " + inquiryId));

		// 이미 답변이 달렸거나 삭제된 문의인지 확인
		if (inquiry.getStatus() != Status.WAITING) {
			throw new IllegalStateException("이미 답변이 완료되었거나 처리할 수 없는 문의입니다.");
		}

		User admin = userRepository.findById(adminId)
			.orElseThrow(() -> new IllegalArgumentException("관리자 정보를 찾을 수 없습니다. id: " + adminId));

		InquiryAnswer newAnswer = InquiryAnswer.builder()
			.inquiry(inquiry)
			.user(admin)
			.comment(requestDto.getComment())
			.createdAt(LocalDateTime.now())
			.build();

		InquiryAnswer savedAnswer = inquiryAnswerRepository.save(newAnswer);

		// 문의 상태를 'ANSWERED'로 변경하고, 답변을 연결
		inquiry.setStatus(Status.ANSWERED);
		inquiry.setInquiryAnswer(savedAnswer);
		inquiry.setUpdatedAt(LocalDateTime.now());

		log.info("✅ [답변 등록] 서비스 성공 - answerId: {}", savedAnswer.getId());
		return InquiryAnswerDto.from(savedAnswer);
	}
}

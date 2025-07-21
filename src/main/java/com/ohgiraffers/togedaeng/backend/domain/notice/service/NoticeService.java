package com.ohgiraffers.togedaeng.backend.domain.notice.service;

import java.io.IOException;
import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import com.ohgiraffers.togedaeng.backend.domain.custom.service.S3Uploader;
import com.ohgiraffers.togedaeng.backend.domain.notice.dto.request.CreateNoticeRequestDto;
import com.ohgiraffers.togedaeng.backend.domain.notice.dto.response.CreateNoticeResponseDto;
import com.ohgiraffers.togedaeng.backend.domain.notice.dto.response.NoticeDetailResponseDto;
import com.ohgiraffers.togedaeng.backend.domain.notice.dto.response.NoticeListResponseDto;
import com.ohgiraffers.togedaeng.backend.domain.notice.entity.Notice;
import com.ohgiraffers.togedaeng.backend.domain.notice.repository.NoticeRepository;
import com.ohgiraffers.togedaeng.backend.domain.user.model.entity.User;
import com.ohgiraffers.togedaeng.backend.domain.user.repository.UserRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class NoticeService {

	private final Logger log = LoggerFactory.getLogger(NoticeService.class);
	private final NoticeRepository noticeRepository;
	private final UserRepository userRepository;
	private final S3Uploader s3Uploader;

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

	/**
	 * 📍 공지 단일 상세 조회 서비스
	 * - ID로 특정 공지를 조회하여 상세 정보를 반환한다.
	 * - 연관된 작성자(User) 정보도 함께 조회한다.
	 *
	 * @param noticeId 조회할 공지 ID
	 * @return 공지 상세 정보 (NoticeDetailResponseDto)
	 * @throws IllegalArgumentException 해당 ID의 공지가 없을 경우 발생
	 */
	public NoticeDetailResponseDto getNoticeById(Long noticeId) {
		log.info("🔍 공지 단일 상세 조회 서비스 시작 - noticeId: {}", noticeId);

		// Repository에서 ID로 Notice를 User 정보와 함께 조회 (Fetch Join)
		Notice notice = noticeRepository.findByIdWithUser(noticeId)
			.orElseThrow(() -> {
				log.warn("⚠️ 존재하지 않는 공지 조회 시도 - noticeId: {}", noticeId);
				return new IllegalArgumentException("해당 공지를 찾을 수 없습니다. id: " + noticeId);
			});

		// Entity를 DTO로 변환
		NoticeDetailResponseDto responseDto = new NoticeDetailResponseDto(
			notice.getId(),
			notice.getCategory(),
			notice.getTitle(),
			notice.getContent(),
			notice.getUser().getNickname(), // Fetch Join으로 조회했기 때문에 추가 쿼리 없음
			notice.getImageUrl(),
			notice.getCreatedAt(),
			notice.getUpdatedAt()
		);

		log.info("✅ 공지 단일 상세 조회 서비스 성공 - noticeId: {}", noticeId);
		return responseDto;
	}

	/**
	 * 📍 공지 작성
	 * - 전달받은 이미지 파일을 S3에 업로드하고, 그 URL을 포함하여 새로운 공지를 작성한다.
	 * - 이미지가 없는 경우에도 공지 작성이 가능하다.
	 *
	 * @param requestDto 공지 작성에 필요한 데이터 (제목, 내용, 카테고리)
	 * @param image      S3에 업로드할 이미지 파일 (null일 수 있음)
	 * @param userId     공지를 작성하는 사용자의 ID
	 * @return 작성된 공지의 상세 정보를 담은 DTO (CreateNoticeResponseDto)
	 * @throws IllegalArgumentException 해당 ID의 사용자가 없을 경우 발생
	 * @throws RuntimeException         S3 이미지 업로드 실패 시 발생
	 */
	public CreateNoticeResponseDto createNotice(CreateNoticeRequestDto requestDto, MultipartFile image, Long userId) {
		log.info("🚀 [공지 등록] 시작 - userId: {}", userId);

		User user = userRepository.findById(userId)
			.orElseThrow(() -> new IllegalArgumentException("사용자를 찾을 수 없습니다. id: " + userId));

		// 1. 이미지 파일이 존재하면 S3에 업로드하고 URL을 가져옵니다.
		String imageUrl = null;
		if (image != null && !image.isEmpty()) {
			try {
				// 'notices' 라는 디렉토리 안에 이미지 저장
				imageUrl = s3Uploader.upload(image, "notices");
				log.info("🖼️ S3 이미지 업로드 성공, URL: {}", imageUrl);
			} catch (IOException e) {
				log.error("❌ S3 이미지 업로드 중 오류 발생", e);
				throw new RuntimeException("이미지 업로드에 실패했습니다.");
			}
		}

		// 2. DTO와 S3 이미지 URL을 바탕으로 Notice 엔티티 생성
		Notice newNotice = Notice.builder()
			.user(user)
			.category(requestDto.getCategory())
			.title(requestDto.getTitle())
			.content(requestDto.getContent())
			.imageUrl(imageUrl) // S3에서 받은 URL 저장 (null일 수 있음)
			.createdAt(LocalDateTime.now())
			.build();

		Notice savedNotice = noticeRepository.save(newNotice);
		log.info("✅ 공지 등록 서비스 성공 - noticeId: {}", savedNotice.getId());

		return new CreateNoticeResponseDto(
			savedNotice.getId(),
			savedNotice.getUser().getNickname(),
			savedNotice.getCategory(),
			savedNotice.getTitle(),
			savedNotice.getContent(),
			savedNotice.getImageUrl()
		);
	}

	// 공지 수정

	// 공지 삭제
}

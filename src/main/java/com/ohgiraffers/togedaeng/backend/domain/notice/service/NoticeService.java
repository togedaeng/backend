package com.ohgiraffers.togedaeng.backend.domain.notice.service;

import java.io.IOException;
import java.nio.file.AccessDeniedException;
import java.time.LocalDateTime;
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
import com.ohgiraffers.togedaeng.backend.domain.notice.dto.request.CreateNoticeRequestDto;
import com.ohgiraffers.togedaeng.backend.domain.notice.dto.request.UpdateNoticeRequestDto;
import com.ohgiraffers.togedaeng.backend.domain.notice.dto.response.CreateNoticeResponseDto;
import com.ohgiraffers.togedaeng.backend.domain.notice.dto.response.DeleteNoticeResponseDto;
import com.ohgiraffers.togedaeng.backend.domain.notice.dto.response.NoticeDetailResponseDto;
import com.ohgiraffers.togedaeng.backend.domain.notice.dto.response.NoticeListResponseDto;
import com.ohgiraffers.togedaeng.backend.domain.notice.dto.response.UpdateNoticeResponseDto;
import com.ohgiraffers.togedaeng.backend.domain.notice.entity.Notice;
import com.ohgiraffers.togedaeng.backend.domain.notice.entity.NoticeImage;
import com.ohgiraffers.togedaeng.backend.domain.notice.repository.NoticeImageRepository;
import com.ohgiraffers.togedaeng.backend.domain.notice.repository.NoticeRepository;
import com.ohgiraffers.togedaeng.backend.domain.user.model.entity.Role;
import com.ohgiraffers.togedaeng.backend.domain.user.model.entity.User;
import com.ohgiraffers.togedaeng.backend.domain.user.repository.UserRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true) // 기본적으로 읽기 전용 트랜잭션 설정
public class NoticeService {

	private final Logger log = LoggerFactory.getLogger(NoticeService.class);
	private final NoticeRepository noticeRepository;
	private final UserRepository userRepository;
	private final NoticeImageRepository noticeImageRepository;
	private final S3Uploader s3Uploader;

	/**
	 * 📍 공지 전체 조회 서비스
	 * - 모든 공지 정보를 페이지네이션으로 반환한다.
	 * - 각 공지별로 작성자 닉네임을 함께 반환한다.
	 *
	 * @param page 페이지 번호 (0부터 시작)
	 * @param size 페이지 크기
	 * @return 페이지네이션된 공지 리스트
	 */
	@Transactional
	public List<NoticeListResponseDto> getAllNotices(int page, int size) {
		log.info("🔍 공지 전체 조회 서비스 시작 - page: {}, size: {}", page, size);
		Pageable pageable = PageRequest.of(page, size);

		Page<Notice> noticesPage = noticeRepository.findAllWithUser(pageable);

		return noticesPage.getContent().stream()
			.map(notice -> new NoticeListResponseDto(
				notice.getId(),
				notice.getCategory(),
				notice.getTitle(),
				notice.getUser().getNickname(),
				notice.getCreatedAt()
			))
			.collect(Collectors.toList());
	}

	/**
	 * 📍 공지 단일 상세 조회 서비스
	 * - ID로 특정 공지를 조회하여 상세 정보를 반환한다.
	 * - 연관된 모든 이미지 URL 리스트를 함께 반환한다.
	 *
	 * @param noticeId 조회할 공지 ID
	 * @return 공지 상세 정보
	 */
	@Transactional
	public NoticeDetailResponseDto getNoticeById(Long noticeId) {
		log.info("🔍 공지 단일 상세 조회 서비스 시작 - noticeId: {}", noticeId);

		// User와 Image 리스트를 함께 조회하는 쿼리 필요 (N+1 문제 방지)
		Notice notice = noticeRepository.findNoticeDetailsById(noticeId)
			.orElseThrow(() -> new IllegalArgumentException("해당 공지를 찾을 수 없습니다. id: " + noticeId));

		// 이미지 URL 리스트 추출
		List<String> imageUrls = notice.getImages().stream()
			.map(NoticeImage::getImageUrl)
			.collect(Collectors.toList());

		return new NoticeDetailResponseDto(
			notice.getId(),
			notice.getCategory(),
			notice.getTitle(),
			notice.getContent(),
			notice.getUser().getNickname(),
			imageUrls,
			notice.getCreatedAt(),
			notice.getUpdatedAt()
		);
	}

	/**
	 * 📍 공지 작성 서비스 (다중 이미지 지원)
	 * - 전달받은 이미지 파일들을 S3에 업로드하고, 그 URL들을 포함하여 새로운 공지를 작성한다.
	 *
	 * @param requestDto 공지 작성에 필요한 데이터
	 * @param images     S3에 업로드할 이미지 파일 리스트 (null일 수 있음)
	 * @param userId     작성자 ID
	 * @return 작성된 공지의 상세 정보
	 */
	@Transactional
	public CreateNoticeResponseDto createNotice(CreateNoticeRequestDto requestDto, List<MultipartFile> images, Long userId) {
		log.info("🚀 [공지 등록] 시작 - userId: {}", userId);

		User user = userRepository.findById(userId)
			.orElseThrow(() -> new IllegalArgumentException("사용자를 찾을 수 없습니다. id: " + userId));

		Notice newNotice = Notice.builder()
			.user(user)
			.category(requestDto.getCategory())
			.title(requestDto.getTitle())
			.content(requestDto.getContent())
			.createdAt(LocalDateTime.now())
			.build();

		// 이미지 파일 리스트가 존재하면 S3에 업로드하고 Notice에 추가
		if (images != null && !images.isEmpty()) {
			for (MultipartFile image : images) {
				try {
					String imageUrl = s3Uploader.upload(image, "notices");
					NoticeImage noticeImage = new NoticeImage(imageUrl);
					newNotice.addImage(noticeImage);
				} catch (IOException e) {
					log.error("❌ S3 이미지 업로드 중 오류 발생", e);
					throw new RuntimeException("이미지 업로드에 실패했습니다.");
				}
			}
		}

		Notice savedNotice = noticeRepository.save(newNotice);
		log.info("✅ 공지 등록 서비스 성공 - noticeId: {}", savedNotice.getId());

		List<String> imageUrls = savedNotice.getImages().stream()
			.map(NoticeImage::getImageUrl)
			.collect(Collectors.toList());

		return new CreateNoticeResponseDto(
			savedNotice.getId(),
			savedNotice.getUser().getNickname(),
			savedNotice.getCategory(),
			savedNotice.getTitle(),
			savedNotice.getContent(),
			imageUrls
		);
	}

	/**
	 * 📍 공지 수정 서비스
	 * - 공지 작성자 본인이거나 관리자(ADMIN)일 경우에만 수정을 허용한다.
	 * - 기존 이미지를 삭제하고 새로운 이미지를 추가할 수 있다.
	 *
	 * @param noticeId    수정할 공지 ID
	 * @param requestDto  수정할 공지 내용 및 삭제할 이미지 ID 리스트
	 * @param newImages   새로 업로드할 이미지 파일 리스트
	 * @param userId      요청을 보낸 사용자의 ID
	 * @return 수정된 공지의 상세 정보
	 * @throws AccessDeniedException 수정 권한이 없을 경우 발생
	 */
	@Transactional
	public UpdateNoticeResponseDto updateNotice(Long noticeId, UpdateNoticeRequestDto requestDto, List<MultipartFile> newImages, Long userId) throws AccessDeniedException {
		log.info("🚀 [공지 수정] 시작 - noticeId: {}, userId: {}", noticeId, userId);

		User user = userRepository.findById(userId)
			.orElseThrow(() -> new IllegalArgumentException("사용자를 찾을 수 없습니다. id: " + userId));

		Notice notice = noticeRepository.findNoticeDetailsById(noticeId)
			.orElseThrow(() -> new IllegalArgumentException("공지를 찾을 수 없습니다. id: " + noticeId));

		// 권한 검사 (본인 또는 ADMIN 확인)
		if (!user.getRole().equals(Role.ADMIN) && !notice.getUser().getId().equals(userId)) {
			log.warn("⚠️ 공지 수정 권한 없음 - noticeId: {}, userId: {}", noticeId, userId);
			throw new AccessDeniedException("공지를 수정할 권한이 없습니다.");
		}

		// 1. 기존 이미지 삭제 처리
		if (requestDto.getDeleteImageIds() != null && !requestDto.getDeleteImageIds().isEmpty()) {
			List<Long> idsToDelete = requestDto.getDeleteImageIds();
			// notice가 이미 가지고 있는 images 리스트에서 삭제할 대상을 찾습니다.
			List<NoticeImage> imagesToRemove = notice.getImages().stream()
				.filter(image -> idsToDelete.contains(image.getId()))
				.collect(Collectors.toList());

			for(NoticeImage image : imagesToRemove) {
				s3Uploader.delete(image.getImageUrl()); // S3에서 파일 삭제
				notice.getImages().remove(image); // 컬렉션에서 제거 (orphanRemoval=true가 DB에서 삭제)
			}
			log.info("🖼️ 기존 S3 이미지 {}개 삭제 성공", imagesToRemove.size());
		}

		// 2. 새로운 이미지 추가
		if (newImages != null && !newImages.isEmpty()) {
			for (MultipartFile image : newImages) {
				try {
					String imageUrl = s3Uploader.upload(image, "notices");
					notice.addImage(new NoticeImage(imageUrl));
				} catch (IOException e) {
					throw new RuntimeException("새 이미지 업로드에 실패했습니다.");
				}
			}
			log.info("🖼️ 새로운 S3 이미지 {}개 추가 성공", newImages.size());
		}

		// 3. 공지 내용 업데이트
		notice.update(
			requestDto.getTitle(),
			requestDto.getContent(),
			requestDto.getCategory()
		);

		// 명시적 저장은 Cascade와 Dirty Checking을 확실히 전파하기 위해 유지합니다.
		noticeRepository.save(notice);

		log.info("✅ 공지 수정 서비스 성공 - noticeId: {}", notice.getId());

		// DB와 동기화된 최종 이미지 리스트를 가져옵니다.
		List<String> finalImageUrls = notice.getImages().stream()
			.map(NoticeImage::getImageUrl)
			.collect(Collectors.toList());

		return new UpdateNoticeResponseDto(
			notice.getId(),
			notice.getUser().getNickname(),
			notice.getCategory(),
			notice.getTitle(),
			notice.getContent(),
			finalImageUrls,
			notice.getUpdatedAt()
		);
	}

	/**
	 * 📍 공지 삭제 서비스 (소프트 딜리트)
	 * - 공지를 실제로 삭제하는 대신 상태를 DELETED로 변경한다.
	 * - 공지 작성자 본인이거나 관리자(ADMIN)일 경우에만 삭제를 허용한다.
	 *
	 * @param noticeId 삭제할 공지 ID
	 * @param userId   요청을 보낸 사용자의 ID
	 * @throws AccessDeniedException 삭제 권한이 없을 경우 발생
	 */
	@Transactional
	public DeleteNoticeResponseDto deleteNotice(Long noticeId, Long userId) throws AccessDeniedException {
		log.info("🚀 [공지 삭제] 시작 - noticeId: {}, userId: {}", noticeId, userId);

		// 1. 사용자 및 공지 정보 조회
		User user = userRepository.findById(userId)
			.orElseThrow(() -> new IllegalArgumentException("사용자를 찾을 수 없습니다. id: " + userId));
		// 삭제된 공지도 찾아야 하므로 일반 findById 사용
		Notice notice = noticeRepository.findById(noticeId)
			.orElseThrow(() -> new IllegalArgumentException("공지를 찾을 수 없습니다. id: " + noticeId));

		// 2. 권한 검사 (본인 또는 ADMIN 확인)
		if (!user.getRole().equals(Role.ADMIN) && !notice.getUser().getId().equals(userId)) {
			log.warn("⚠️ 공지 삭제 권한 없음 - noticeId: {}, userId: {}", noticeId, userId);
			throw new AccessDeniedException("공지를 삭제할 권한이 없습니다.");
		}

		// 3. 소프트 딜리트 처리
		notice.softDelete();
		log.info("✅ 공지 삭제(소프트) 서비스 성공 - noticeId: {}", notice.getId());

		return new DeleteNoticeResponseDto(
			notice.getId(),
			notice.getTitle(),
			notice.getDeletedAt()
		);
	}
}
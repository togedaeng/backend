package com.ohgiraffers.togedaeng.backend.domain.custom.service;

import java.io.IOException;
import java.time.LocalDateTime;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import com.ohgiraffers.togedaeng.backend.domain.Ndog.dto.request.CreateDogRequestDto;
import com.ohgiraffers.togedaeng.backend.domain.Ndog.exception.ImageUploadException;
import com.ohgiraffers.togedaeng.backend.domain.custom.entity.Custom;
import com.ohgiraffers.togedaeng.backend.domain.custom.entity.DogImage;
import com.ohgiraffers.togedaeng.backend.domain.custom.entity.Status;
import com.ohgiraffers.togedaeng.backend.domain.custom.entity.Type;
import com.ohgiraffers.togedaeng.backend.domain.custom.repository.CustomRepository;
import com.ohgiraffers.togedaeng.backend.domain.custom.repository.DogImageRepository;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class CustomService {

	Logger log = LoggerFactory.getLogger(CustomService.class);

	private final CustomRepository customRepository;
	private final S3Uploader s3Uploader;
	private final DogImageRepository dogImageRepository;

	@Transactional
	public void createCustomRequest(Long dogId, CreateDogRequestDto dto) {
		log.info("📦 [커스텀 요청 생성] 시작 - dogId: {}", dogId);

		if (dto.getMainImage() == null) {
			throw new IllegalArgumentException("메인 이미지는 필수입니다.");
		}

		if (dto.getSubImages() != null && dto.getSubImages().size() > 3) {
			throw new IllegalArgumentException("서브 이미지는 최대 3장까지 등록 가능합니다.");
		}

		// 1. 커스텀 요청 저장
		Custom custom = new Custom(dogId, Status.PENDING, LocalDateTime.now());

		customRepository.save(custom);
		log.debug("📝 커스텀 요청 저장 완료 - customId: {}", custom.getId());

		// 2. 이미지 업로드 및 저장
		try {
			// 메인 이미지
			String mainUrl = s3Uploader.upload(dto.getMainImage(), "dog-images");
			dogImageRepository.save(new DogImage(null, custom.getId(), mainUrl, Type.MAIN));
			log.debug("📷 메인 이미지 업로드 완료 - url: {}", mainUrl);

			// 서브 이미지
			if (dto.getSubImages() != null) {
				for (MultipartFile sub : dto.getSubImages()) {
					String subUrl = s3Uploader.upload(sub, "dog-images");
					dogImageRepository.save(new DogImage(null, custom.getId(), subUrl, Type.SUB));
					log.debug("📷 서브 이미지 업로드 완료 - url: {}", subUrl);
				}
			}

		} catch (IOException e) {
			log.error("❌ 이미지 업로드 실패", e);
			throw new ImageUploadException("이미지 업로드 실패", e);
		}

		log.info("✅ [커스텀 요청 생성] 완료 - customId: {}", custom.getId());
	}
}

package com.ohgiraffers.togedaeng.backend.domain.dog.dto.request;

import org.springframework.web.multipart.MultipartFile;

import com.ohgiraffers.togedaeng.backend.domain.dog.entity.Status;

import jakarta.validation.constraints.NotNull;

public class UpdateDogStatusActiveRequestDto {

	private Long userId;
	private Status status;

	@NotNull(message = "렌더링 이미지 파일은 필수입니다.")
	private MultipartFile renderedImage;

	public UpdateDogStatusActiveRequestDto() {
	}

	public UpdateDogStatusActiveRequestDto(Long userId, Status status, MultipartFile renderedImage) {
		this.userId = userId;
		this.status = status;
		this.renderedImage = renderedImage;
	}

	public Long getUserId() {
		return userId;
	}

	public void setUserId(Long userId) {
		this.userId = userId;
	}

	public Status getStatus() {
		return status;
	}

	public void setStatus(Status status) {
		this.status = status;
	}

	public MultipartFile getRenderedImage() {
		return renderedImage;
	}

	public void setRenderedImage(MultipartFile renderedImage) {
		this.renderedImage = renderedImage;
	}

	@Override
	public String toString() {
		return "UpdateDogStatusActiveRequestDto{" +
			"userId=" + userId +
			", status=" + status +
			", renderedImage='" + renderedImage + '\'' +
			'}';
	}
}

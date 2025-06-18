package com.ohgiraffers.togedaeng.backend.domain.dog.dto.response;

import java.time.LocalDateTime;

import com.ohgiraffers.togedaeng.backend.domain.dog.entity.Status;

public class DeleteDogResponseDto {

	private Long dogId;
	private Status status;
	private LocalDateTime deletedAt;

	public DeleteDogResponseDto() {
	}

	public DeleteDogResponseDto(Long dogId, Status status, LocalDateTime deletedAt) {
		this.dogId = dogId;
		this.status = status;
		this.deletedAt = deletedAt;
	}

	public Long getDogId() {
		return dogId;
	}

	public void setDogId(Long dogId) {
		this.dogId = dogId;
	}

	public Status getStatus() {
		return status;
	}

	public void setStatus(Status status) {
		this.status = status;
	}

	public LocalDateTime getDeletedAt() {
		return deletedAt;
	}

	public void setDeletedAt(LocalDateTime deletedAt) {
		this.deletedAt = deletedAt;
	}

	@Override
	public String toString() {
		return "DeleteDogResponseDto{" +
			"dogId=" + dogId +
			", status=" + status +
			", deletedAt=" + deletedAt +
			'}';
	}
}

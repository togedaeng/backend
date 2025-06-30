package com.ohgiraffers.togedaeng.backend.domain.dog.dto.response;

import java.time.LocalDateTime;

import com.ohgiraffers.togedaeng.backend.domain.dog.entity.Status;

public class UpdateDogStatusActiveResponseDto {

	private Long dogId;
	private Status updatedStatus;
	private LocalDateTime updatedTime;

	public UpdateDogStatusActiveResponseDto() {
	}

	public UpdateDogStatusActiveResponseDto(Long dogId, Status updatedStatus, LocalDateTime updatedTime) {
		this.dogId = dogId;
		this.updatedStatus = updatedStatus;
		this.updatedTime = updatedTime;
	}

	public Long getDogId() {
		return dogId;
	}

	public void setDogId(Long dogId) {
		this.dogId = dogId;
	}

	public Status getUpdatedStatus() {
		return updatedStatus;
	}

	public void setUpdatedStatus(Status updatedStatus) {
		this.updatedStatus = updatedStatus;
	}

	public LocalDateTime getUpdatedTime() {
		return updatedTime;
	}

	public void setUpdatedTime(LocalDateTime updatedTime) {
		this.updatedTime = updatedTime;
	}

	@Override
	public String toString() {
		return "UpdateDogStatusActiveResponseDto{" +
			"dogId=" + dogId +
			", updatedStatus=" + updatedStatus +
			", updatedTime=" + updatedTime +
			'}';
	}
}

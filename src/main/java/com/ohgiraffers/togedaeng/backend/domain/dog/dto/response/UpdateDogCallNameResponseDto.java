package com.ohgiraffers.togedaeng.backend.domain.dog.dto.response;

import java.time.LocalDateTime;

public class UpdateDogCallNameResponseDto {

	private Long dogId;
	private String updatedCallName;
	private LocalDateTime updatedTime;

	public UpdateDogCallNameResponseDto() {
	}

	public UpdateDogCallNameResponseDto(Long dogId, String updatedCallName, LocalDateTime updatedTime) {
		this.dogId = dogId;
		this.updatedCallName = updatedCallName;
		this.updatedTime = updatedTime;
	}

	public Long getDogId() {
		return dogId;
	}

	public void setDogId(Long dogId) {
		this.dogId = dogId;
	}

	public String getUpdatedCallName() {
		return updatedCallName;
	}

	public void setUpdatedCallName(String updatedCallName) {
		this.updatedCallName = updatedCallName;
	}

	public LocalDateTime getUpdatedTime() {
		return updatedTime;
	}

	public void setUpdatedTime(LocalDateTime updatedTime) {
		this.updatedTime = updatedTime;
	}

	@Override
	public String toString() {
		return "UpdateDogCallNameResponseDto{" +
			"dogId=" + dogId +
			", updatedCallName='" + updatedCallName + '\'' +
			", updatedTime=" + updatedTime +
			'}';
	}
}

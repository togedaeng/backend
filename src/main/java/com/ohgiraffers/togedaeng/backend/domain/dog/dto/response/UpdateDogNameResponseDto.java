package com.ohgiraffers.togedaeng.backend.domain.dog.dto.response;

import java.time.LocalDateTime;

public class UpdateDogNameResponseDto {

	private Long dogId;
	private String updatedName;
	private LocalDateTime updatedTime;

	public UpdateDogNameResponseDto() {
	}

	public UpdateDogNameResponseDto(Long dogId, String updatedName, LocalDateTime updatedTime) {
		this.dogId = dogId;
		this.updatedName = updatedName;
		this.updatedTime = updatedTime;
	}

	public Long getDogId() {
		return dogId;
	}

	public void setDogId(Long dogId) {
		this.dogId = dogId;
	}

	public String getUpdatedName() {
		return updatedName;
	}

	public void setUpdatedName(String updatedName) {
		this.updatedName = updatedName;
	}

	public LocalDateTime getUpdatedTime() {
		return updatedTime;
	}

	public void setUpdatedTime(LocalDateTime updatedTime) {
		this.updatedTime = updatedTime;
	}

	@Override
	public String toString() {
		return "UpdateDogNameResponseDto{" +
			"dogId=" + dogId +
			", updatedName='" + updatedName + '\'' +
			", updatedTime=" + updatedTime +
			'}';
	}
}

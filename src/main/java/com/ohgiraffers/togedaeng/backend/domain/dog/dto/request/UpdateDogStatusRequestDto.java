package com.ohgiraffers.togedaeng.backend.domain.dog.dto.request;

import com.ohgiraffers.togedaeng.backend.domain.dog.entity.Status;

public class UpdateDogStatusRequestDto {

	private Long userId;
	private Status newStatus;

	public UpdateDogStatusRequestDto() {
	}

	public UpdateDogStatusRequestDto(Status newStatus, Long userId) {
		this.newStatus = newStatus;
		this.userId = userId;
	}

	public Long getUserId() {
		return userId;
	}

	public void setUserId(Long userId) {
		this.userId = userId;
	}

	public Status getNewStatus() {
		return newStatus;
	}

	public void setNewStatus(Status newStatus) {
		this.newStatus = newStatus;
	}

	@Override
	public String toString() {
		return "UpdateDogStatusRequestDto{" +
			"userId=" + userId +
			", newStatus=" + newStatus +
			'}';
	}
}

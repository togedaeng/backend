package com.ohgiraffers.togedaeng.backend.domain.dog.dto.request;

import com.ohgiraffers.togedaeng.backend.domain.dog.entity.Status;

public class UpdateDogStatusRequestDto {

	private Status newStatus;

	public UpdateDogStatusRequestDto() {
	}

	public UpdateDogStatusRequestDto(Status newStatus) {
		this.newStatus = newStatus;
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
			"newStatus=" + newStatus +
			'}';
	}
}

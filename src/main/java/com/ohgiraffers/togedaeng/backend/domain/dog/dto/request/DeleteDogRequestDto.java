package com.ohgiraffers.togedaeng.backend.domain.dog.dto.request;

import com.ohgiraffers.togedaeng.backend.domain.dog.entity.Status;

public class DeleteDogRequestDto {

	private Long dogId;
	private Status status;

	public DeleteDogRequestDto() {
	}

	public DeleteDogRequestDto(Long dogId, Status status) {
		this.dogId = dogId;
		this.status = status;
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

	@Override
	public String toString() {
		return "DeleteDogRequestDto{" +
			"dogId=" + dogId +
			", status=" + status +
			'}';
	}
}

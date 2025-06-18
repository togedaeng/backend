package com.ohgiraffers.togedaeng.backend.domain.dog.dto.request;

public class DeleteDogRequestDto {

	private Long dogId;

	public DeleteDogRequestDto() {
	}

	public DeleteDogRequestDto(Long dogId) {
		this.dogId = dogId;
	}

	public Long getDogId() {
		return dogId;
	}

	public void setDogId(Long dogId) {
		this.dogId = dogId;
	}

	@Override
	public String toString() {
		return "DeleteDogRequestDto{" +
			"dogId=" + dogId +
			'}';
	}
}

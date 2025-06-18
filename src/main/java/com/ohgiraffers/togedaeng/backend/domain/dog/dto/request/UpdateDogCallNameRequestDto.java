package com.ohgiraffers.togedaeng.backend.domain.dog.dto.request;

public class UpdateDogCallNameRequestDto {

	private Long dogId;
	private String newCallName;

	public UpdateDogCallNameRequestDto() {
	}

	public UpdateDogCallNameRequestDto(Long dogId, String newCallName) {
		this.dogId = dogId;
		this.newCallName = newCallName;
	}

	public Long getDogId() {
		return dogId;
	}

	public void setDogId(Long dogId) {
		this.dogId = dogId;
	}

	public String getNewCallName() {
		return newCallName;
	}

	public void setNewCallName(String newCallName) {
		this.newCallName = newCallName;
	}

	@Override
	public String toString() {
		return "UpdateDogCallNameRequestDto{" +
			"dogId=" + dogId +
			", newCallName='" + newCallName + '\'' +
			'}';
	}
}

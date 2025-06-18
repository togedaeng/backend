package com.ohgiraffers.togedaeng.backend.domain.dog.dto.request;

public class UpdateDogNameRequestDto {

	private Long dogId;
	private String newName;

	public UpdateDogNameRequestDto() {
	}

	public UpdateDogNameRequestDto(Long dogId, String newName) {
		this.dogId = dogId;
		this.newName = newName;
	}

	public Long getDogId() {
		return dogId;
	}

	public void setDogId(Long dogId) {
		this.dogId = dogId;
	}

	public String getNewName() {
		return newName;
	}

	public void setNewName(String newName) {
		this.newName = newName;
	}

	@Override
	public String toString() {
		return "UpdateDogNameRequestDto{" +
			"dogId=" + dogId +
			", newName='" + newName + '\'' +
			'}';
	}
}

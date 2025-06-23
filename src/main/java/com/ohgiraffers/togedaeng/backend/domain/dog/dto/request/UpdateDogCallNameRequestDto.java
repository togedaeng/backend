package com.ohgiraffers.togedaeng.backend.domain.dog.dto.request;

public class UpdateDogCallNameRequestDto {

	private String newCallName;

	public UpdateDogCallNameRequestDto() {
	}

	public UpdateDogCallNameRequestDto(String newCallName) {
		this.newCallName = newCallName;
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
			", newCallName='" + newCallName + '\'' +
			'}';
	}
}

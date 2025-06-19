package com.ohgiraffers.togedaeng.backend.domain.dog.dto.request;

public class UpdateDogNameRequestDto {

	private String newName;

	public UpdateDogNameRequestDto() {
	}

	public UpdateDogNameRequestDto(String newName) {
		this.newName = newName;
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
			", newName='" + newName + '\'' +
			'}';
	}
}

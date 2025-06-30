package com.ohgiraffers.togedaeng.backend.domain.dog.dto.request;

public class UpdateDogNameRequestDto {

	private Long userId;
	private String newName;

	public UpdateDogNameRequestDto() {
	}

	public UpdateDogNameRequestDto(Long userId, String newName) {
		this.userId = userId;
		this.newName = newName;
	}

	public Long getUserId() {
		return userId;
	}

	public void setUserId(Long userId) {
		this.userId = userId;
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
			"userId=" + userId +
			", newName='" + newName + '\'' +
			'}';
	}
}

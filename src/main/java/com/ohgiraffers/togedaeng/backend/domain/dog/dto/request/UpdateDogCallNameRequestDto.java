package com.ohgiraffers.togedaeng.backend.domain.dog.dto.request;

public class UpdateDogCallNameRequestDto {

	private Long userId;
	private String newCallName;

	public UpdateDogCallNameRequestDto() {
	}

	public UpdateDogCallNameRequestDto(Long userId, String newCallName) {
		this.userId = userId;
		this.newCallName = newCallName;
	}

	public Long getUserId() {
		return userId;
	}

	public void setUserId(Long userId) {
		this.userId = userId;
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
			"userId=" + userId +
			", newCallName='" + newCallName + '\'' +
			'}';
	}
}

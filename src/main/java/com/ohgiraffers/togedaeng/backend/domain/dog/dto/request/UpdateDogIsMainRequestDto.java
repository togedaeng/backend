package com.ohgiraffers.togedaeng.backend.domain.dog.dto.request;

public class UpdateDogIsMainRequestDto {

	private int isMainDog;

	public UpdateDogIsMainRequestDto() {
	}

	public UpdateDogIsMainRequestDto(int isMainDog) {
		this.isMainDog = isMainDog;
	}

	public int getIsMainDog() {
		return isMainDog;
	}

	public void setIsMainDog(int isMainDog) {
		this.isMainDog = isMainDog;
	}

	@Override
	public String toString() {
		return "UpdateDogIsMainRequestDto{" +
			", isMainDog=" + isMainDog +
			'}';
	}
}

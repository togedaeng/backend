package com.ohgiraffers.togedaeng.backend.domain.dog.dto.response;

public class UpdateDogIsMainResponseDto {

	private Long dogId;
	private int updatedIsMainDog;

	public UpdateDogIsMainResponseDto() {
	}

	public UpdateDogIsMainResponseDto(Long dogId, int updatedIsMainDog) {
		this.dogId = dogId;
		this.updatedIsMainDog = updatedIsMainDog;
	}

	public Long getDogId() {
		return dogId;
	}

	public void setDogId(Long dogId) {
		this.dogId = dogId;
	}

	public int getUpdatedIsMainDog() {
		return updatedIsMainDog;
	}

	public void setUpdatedIsMainDog(int updatedIsMainDog) {
		this.updatedIsMainDog = updatedIsMainDog;
	}

	@Override
	public String toString() {
		return "UpdateDogIsMainResponseDto{" +
			"dogId=" + dogId +
			", updatedIsMainDog=" + updatedIsMainDog +
			'}';
	}
}

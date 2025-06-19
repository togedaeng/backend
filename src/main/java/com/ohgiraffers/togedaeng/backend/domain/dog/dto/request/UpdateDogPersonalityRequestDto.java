package com.ohgiraffers.togedaeng.backend.domain.dog.dto.request;

public class UpdateDogPersonalityRequestDto {

	private Long dogId;
	private Long newPersonalityId1;
	private Long newPersonalityId2; // nullable

	public UpdateDogPersonalityRequestDto() {
	}

	public UpdateDogPersonalityRequestDto(Long dogId, Long newPersonalityId1, Long newPersonalityId2) {
		this.dogId = dogId;
		this.newPersonalityId1 = newPersonalityId1;
		this.newPersonalityId2 = newPersonalityId2;
	}

	public Long getDogId() {
		return dogId;
	}

	public void setDogId(Long dogId) {
		this.dogId = dogId;
	}

	public Long getNewPersonalityId1() {
		return newPersonalityId1;
	}

	public void setNewPersonalityId1(Long newPersonalityId1) {
		this.newPersonalityId1 = newPersonalityId1;
	}

	public Long getNewPersonalityId2() {
		return newPersonalityId2;
	}

	public void setNewPersonalityId2(Long newPersonalityId2) {
		this.newPersonalityId2 = newPersonalityId2;
	}

	@Override
	public String toString() {
		return "UpdateDogPersonalityRequestDto{" +
			"dogId=" + dogId +
			", newPersonalityId1=" + newPersonalityId1 +
			", newPersonalityId2=" + newPersonalityId2 +
			'}';
	}
}

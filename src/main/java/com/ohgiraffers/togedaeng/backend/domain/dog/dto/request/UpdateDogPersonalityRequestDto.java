package com.ohgiraffers.togedaeng.backend.domain.dog.dto.request;

public class UpdateDogPersonalityRequestDto {

	private Long newPersonalityId1;
	private Long newPersonalityId2; // nullable

	public UpdateDogPersonalityRequestDto() {
	}

	public UpdateDogPersonalityRequestDto(Long newPersonalityId1, Long newPersonalityId2) {
		this.newPersonalityId1 = newPersonalityId1;
		this.newPersonalityId2 = newPersonalityId2;
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
			", newPersonalityId1=" + newPersonalityId1 +
			", newPersonalityId2=" + newPersonalityId2 +
			'}';
	}
}

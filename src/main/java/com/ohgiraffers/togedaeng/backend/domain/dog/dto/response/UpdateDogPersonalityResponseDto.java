package com.ohgiraffers.togedaeng.backend.domain.dog.dto.response;

import java.time.LocalDateTime;
import java.util.List;

public class UpdateDogPersonalityResponseDto {

	private Long dogId;
	private Long personalityCombinationId;
	private List<String> personalityNames;
	private LocalDateTime updatedAt;

	public UpdateDogPersonalityResponseDto() {
	}

	public UpdateDogPersonalityResponseDto(Long dogId, Long personalityCombinationId, List<String> personalityNames,
		LocalDateTime updatedAt) {
		this.dogId = dogId;
		this.personalityCombinationId = personalityCombinationId;
		this.personalityNames = personalityNames;
		this.updatedAt = updatedAt;
	}

	public Long getDogId() {
		return dogId;
	}

	public void setDogId(Long dogId) {
		this.dogId = dogId;
	}

	public Long getPersonalityCombinationId() {
		return personalityCombinationId;
	}

	public void setPersonalityCombinationId(Long personalityCombinationId) {
		this.personalityCombinationId = personalityCombinationId;
	}

	public List<String> getPersonalityNames() {
		return personalityNames;
	}

	public void setPersonalityNames(List<String> personalityNames) {
		this.personalityNames = personalityNames;
	}

	public LocalDateTime getUpdatedAt() {
		return updatedAt;
	}

	public void setUpdatedAt(LocalDateTime updatedAt) {
		this.updatedAt = updatedAt;
	}

	@Override
	public String toString() {
		return "UpdateDogPersonalityResponseDto{" +
			"dogId=" + dogId +
			", personalityCombinationId=" + personalityCombinationId +
			", personalityNames=" + personalityNames +
			", updatedAt=" + updatedAt +
			'}';
	}
}

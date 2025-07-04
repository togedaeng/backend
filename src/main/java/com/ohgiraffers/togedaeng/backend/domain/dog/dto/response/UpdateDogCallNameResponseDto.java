package com.ohgiraffers.togedaeng.backend.domain.dog.dto.response;

import java.time.LocalDateTime;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class UpdateDogCallNameResponseDto {

	private Long dogId;
	private String updatedCallName;
	private LocalDateTime updatedAt;

	@Override
	public String toString() {
		return "UpdateDogCallNameResponseDto{" +
			"dogId=" + dogId +
			", updatedCallName='" + updatedCallName + '\'' +
			", updatedAt=" + updatedAt +
			'}';
	}
}

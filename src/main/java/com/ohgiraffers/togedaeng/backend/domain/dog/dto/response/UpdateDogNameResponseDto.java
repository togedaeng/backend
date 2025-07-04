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
public class UpdateDogNameResponseDto {

	private Long dogId;
	private String updatedName;
	private LocalDateTime updatedAt;

	@Override
	public String toString() {
		return "UpdateDogNameResponseDto{" +
			"dogId=" + dogId +
			", updatedName='" + updatedName + '\'' +
			", updatedTime=" + updatedAt +
			'}';
	}
}

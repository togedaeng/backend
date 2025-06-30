package com.ohgiraffers.togedaeng.backend.domain.dog.dto.response;

import java.time.LocalDate;
import java.time.LocalDateTime;

import com.ohgiraffers.togedaeng.backend.domain.dog.entity.Gender;
import com.ohgiraffers.togedaeng.backend.domain.dog.entity.Status;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@AllArgsConstructor
@Getter
@Setter
public class DogResponseDto {

	private Long id;
	private Long userId;
	private Long personalityCombinationId;
	private String name;
	private Gender gender;
	private LocalDate birth;
	private String callName;
	private Status status;
	private LocalDateTime createdAt;
	private LocalDateTime updatedAt;
	private LocalDateTime deletedAt;

	public DogResponseDto() {
	}

	@Override
	public String toString() {
		return "DogResponseDto{" +
			"id=" + id +
			", userId=" + userId +
			", personalityCombinationId=" + personalityCombinationId +
			", name='" + name + '\'' +
			", gender=" + gender +
			", birth=" + birth +
			", callName='" + callName + '\'' +
			", status=" + status +
			", createdAt=" + createdAt +
			", updatedAt=" + updatedAt +
			", deletedAt=" + deletedAt +
			'}';
	}
}

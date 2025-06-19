package com.ohgiraffers.togedaeng.backend.domain.dog.dto.response;

import java.time.LocalDate;
import java.time.LocalDateTime;

import com.ohgiraffers.togedaeng.backend.domain.dog.entity.Gender;
import com.ohgiraffers.togedaeng.backend.domain.dog.entity.Status;
import com.ohgiraffers.togedaeng.backend.domain.dog.entity.Step;
import com.ohgiraffers.togedaeng.backend.domain.dog.entity.Type;

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
	private Type type;
	private String callName;
	private Status status;
	private Step step;
	private int isMainDog;
	private LocalDateTime createdAt;
	private LocalDateTime updatedAt;
	private LocalDateTime deletedAt;

	public DogResponseDto() {
	}
}

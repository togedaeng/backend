package com.ohgiraffers.togedaeng.backend.domain.dog.dto.response;

import java.time.LocalDate;
import java.time.LocalDateTime;

import com.ohgiraffers.togedaeng.backend.domain.dog.entity.Gender;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class CreateDogResponseDto {

	private Long id;
	private Long userId;
	private Long personalityCombinationId;
	private String name;
	private Gender gender;
	private LocalDate birth;
	private String callName;
	private LocalDateTime createdAt;

}

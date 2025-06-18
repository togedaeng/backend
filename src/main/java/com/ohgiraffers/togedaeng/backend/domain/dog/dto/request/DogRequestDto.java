package com.ohgiraffers.togedaeng.backend.domain.dog.dto.request;

import com.ohgiraffers.togedaeng.backend.domain.dog.entity.Gender;
import com.ohgiraffers.togedaeng.backend.domain.dog.entity.Status;
import com.ohgiraffers.togedaeng.backend.domain.dog.entity.Type;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class DogRequestDto {

	private Long userId;
	private Long personalityCombinationId;
	private String name;
	private Gender gender;
	private Type type;
	private String callName;
	private Status status;
}

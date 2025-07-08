package com.ohgiraffers.togedaeng.backend.domain.user.model.dto;

import com.ohgiraffers.togedaeng.backend.domain.dog.dto.response.DogDetailResponseDto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
public class UserWithDogResponseDto {
	private UserResponseDto user;
	private DogDetailResponseDto dog;
}

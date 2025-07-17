package com.ohgiraffers.togedaeng.backend.domain.custom.dto.request;

import java.util.List;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
public class CustomDogRegisterRequestDto {
	private DogRegisterRequestDto dogInfo;
	private DogOwnerRegisterRequestDto ownerInfo;
	private List<DogImageRegisterRequestDto> imageLists;
}

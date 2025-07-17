package com.ohgiraffers.togedaeng.backend.domain.custom.dto.request;

import com.ohgiraffers.togedaeng.backend.domain.custom.entity.Type;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
public class DogImageRegisterRequestDto {
	private String imageUrl;
	private Type type;
}

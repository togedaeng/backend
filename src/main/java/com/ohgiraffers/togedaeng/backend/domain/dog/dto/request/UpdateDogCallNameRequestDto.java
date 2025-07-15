package com.ohgiraffers.togedaeng.backend.domain.dog.dto.request;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class UpdateDogCallNameRequestDto {

	private String newCallName;

	@Override
	public String toString() {
		return "UpdateDogCallNameRequestDto{" +
			"newCallName='" + newCallName + '\'' +
			'}';
	}
}

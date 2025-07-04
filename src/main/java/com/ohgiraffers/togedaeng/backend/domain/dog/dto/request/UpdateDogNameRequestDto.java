package com.ohgiraffers.togedaeng.backend.domain.dog.dto.request;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class UpdateDogNameRequestDto {

	private String newName;

	@Override
	public String toString() {
		return "UpdateDogNameRequestDto{" +
			"newName='" + newName + '\'' +
			'}';
	}
}

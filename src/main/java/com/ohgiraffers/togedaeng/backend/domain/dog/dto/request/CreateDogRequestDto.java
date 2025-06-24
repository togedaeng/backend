package com.ohgiraffers.togedaeng.backend.domain.dog.dto.request;

import com.ohgiraffers.togedaeng.backend.domain.dog.entity.Gender;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class CreateDogRequestDto {

	private Long userId;
	private String name;
	private Gender gender;
	private String callName;
	private Long personalityId1; // 필수 성격 1
	private Long personalityId2; // 선택 성격 2

	@Override
	public String toString() {
		return "CreateDogRequestDto{" +
			"userId=" + userId +
			", name='" + name + '\'' +
			", gender=" + gender +
			", callName='" + callName + '\'' +
			", personalityId1=" + personalityId1 +
			", personalityId2=" + personalityId2 +
			'}';
	}
}

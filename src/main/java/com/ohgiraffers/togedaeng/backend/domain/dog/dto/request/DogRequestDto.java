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
	private String name;
	private Gender gender;
	private Type type;
	private String callName;
	private Status status;
	private Long personalityId1; // 필수 성격 1
	private Long personalityId2; // 선택 성격 2

	@Override
	public String toString() {
		return "DogRequestDto{" +
			"userId=" + userId +
			", name='" + name + '\'' +
			", gender=" + gender +
			", type=" + type +
			", callName='" + callName + '\'' +
			", status=" + status +
			", personalityId1=" + personalityId1 +
			", personalityId2=" + personalityId2 +
			'}';
	}
}

package com.ohgiraffers.togedaeng.backend.domain.custom.dto.request;

import java.time.LocalDate;
import java.util.List;

import com.ohgiraffers.togedaeng.backend.domain.dog.entity.Gender;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
public class DogRegisterRequestDto {
	private String name;
	private LocalDate birth;
	private Gender gender;
	private List<Long> personalityIds;
}

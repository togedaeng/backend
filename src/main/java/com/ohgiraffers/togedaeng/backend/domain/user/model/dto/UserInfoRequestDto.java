package com.ohgiraffers.togedaeng.backend.domain.user.model.dto;

import java.time.LocalDate;

import com.ohgiraffers.togedaeng.backend.domain.dog.entity.Gender;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@ToString
public class UserInfoRequestDto {
	private String email;
	private String provider;
	private String providerId;
	private String nickname;
	private Gender gender;
	private LocalDate birth;
}

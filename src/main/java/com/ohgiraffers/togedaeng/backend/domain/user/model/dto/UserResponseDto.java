package com.ohgiraffers.togedaeng.backend.domain.user.model.dto;

import java.time.LocalDate;
import java.time.LocalDateTime;

import com.ohgiraffers.togedaeng.backend.domain.dog.entity.Gender;
import com.ohgiraffers.togedaeng.backend.domain.dog.entity.Status;

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
public class UserResponseDto {
	private Long id;
	private String nickname;
	private Gender gender;
	private LocalDate birth;
	private String email;
	private Status status;
	private LocalDateTime createdAt;
}

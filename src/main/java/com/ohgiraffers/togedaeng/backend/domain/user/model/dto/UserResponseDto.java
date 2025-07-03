package com.ohgiraffers.togedaeng.backend.domain.user.model.dto;

import java.time.LocalDate;
import java.time.LocalDateTime;

import com.ohgiraffers.togedaeng.backend.domain.Ndog.entity.Gender;
import com.ohgiraffers.togedaeng.backend.domain.user.model.entity.UserStatus;

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
	private String provider;
	private UserStatus status;
	private LocalDateTime createdAt;
}

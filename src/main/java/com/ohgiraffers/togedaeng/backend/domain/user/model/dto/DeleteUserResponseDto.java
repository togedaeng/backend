package com.ohgiraffers.togedaeng.backend.domain.user.model.dto;

import java.time.LocalDateTime;

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
public class DeleteUserResponseDto {
	private Long id;
	private String nickname;
	private Status status;
	private LocalDateTime deletedAt;
} 
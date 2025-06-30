package com.ohgiraffers.togedaeng.backend.global.auth.dto;

import com.ohgiraffers.togedaeng.backend.domain.user.model.entity.User;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class AuthResponseDto {
	private User user;
	private TokenResponseDto token;
}

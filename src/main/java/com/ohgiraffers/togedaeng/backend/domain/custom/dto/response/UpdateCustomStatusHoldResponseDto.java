package com.ohgiraffers.togedaeng.backend.domain.custom.dto.response;

import java.time.LocalDateTime;

import com.ohgiraffers.togedaeng.backend.domain.custom.entity.ImageValidationError;
import com.ohgiraffers.togedaeng.backend.domain.custom.entity.Status;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
public class UpdateCustomStatusHoldResponseDto {

	private Long id;
	private Long dogId;
	private Long adminId;
	private Status status;
	private ImageValidationError reason;
	private LocalDateTime holdCreatedAt;
}

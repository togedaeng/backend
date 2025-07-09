package com.ohgiraffers.togedaeng.backend.domain.custom.dto.response;

import java.time.LocalDateTime;
import java.util.List;

import com.ohgiraffers.togedaeng.backend.domain.custom.entity.Status;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
public class CustomListByDogIdResponseDto {
	private Long id;
	private Long adminId;
	private Status status;
	private LocalDateTime createdAt;     // 요청 등록일
	private LocalDateTime startedAt;     // 요청 진행 시작일
	private LocalDateTime completedAt;   // 요청 완료일
	private LocalDateTime canceledAt;    // 요청 취소일
	private List<HoldSimpleDto> holds;
}

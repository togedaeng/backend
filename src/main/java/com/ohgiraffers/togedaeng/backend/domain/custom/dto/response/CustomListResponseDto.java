package com.ohgiraffers.togedaeng.backend.domain.custom.dto.response;

import java.time.LocalDateTime;

import com.ohgiraffers.togedaeng.backend.domain.custom.entity.Status;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
public class CustomListResponseDto {

	private Long id;
	private String dogName;
	private String ownerNickname;
	private String adminNickname;
	private Status status;               // 요청 상태
	private LocalDateTime createdAt;     // 요청 등록일
	private LocalDateTime startedAt;     // 요청 진행 시작일
	private LocalDateTime holdCreatedAt; // Hold 테이블에서 가져옴
	private LocalDateTime completedAt;   // 요청 완료일
	private LocalDateTime canceledAt;    // 요청 취소일

	@Override
	public String toString() {
		return "CustomListResponseDto{" +
			"id=" + id +
			", dogName='" + dogName + '\'' +
			", ownerNickname='" + ownerNickname + '\'' +
			", adminNickname='" + adminNickname + '\'' +
			", status=" + status +
			", createdAt=" + createdAt +
			", startedAt=" + startedAt +
			", holdCreatedAt=" + holdCreatedAt +
			", completedAt=" + completedAt +
			", canceledAt=" + canceledAt +
			'}';
	}
}

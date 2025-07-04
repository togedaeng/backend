package com.ohgiraffers.togedaeng.backend.domain.custom.dto.response;

import java.time.LocalDateTime;

import com.ohgiraffers.togedaeng.backend.domain.custom.entity.Status;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class UpdateCustomStatusInProgressResponseDto {

	private Long id;
	private Long dogId;
	private Long adminId;
	private Status status;
	private LocalDateTime startedAt;

	@Override
	public String toString() {
		return "UpdateCustomStatusInProgressResponseDto{" +
			"id=" + id +
			", dogId=" + dogId +
			", adminId=" + adminId +
			", status=" + status +
			", startedAt=" + startedAt +
			'}';
	}
}

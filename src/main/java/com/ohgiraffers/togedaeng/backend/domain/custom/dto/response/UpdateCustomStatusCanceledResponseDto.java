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
public class UpdateCustomStatusCanceledResponseDto {

	private Long id;
	private Long dogId;
	private Long adminId;
	private Status status;
	private LocalDateTime canceled_at;

	@Override
	public String toString() {
		return "UpdateCustomStatusCanceledResponseDto{" +
			"id=" + id +
			", dogId=" + dogId +
			", adminId=" + adminId +
			", status=" + status +
			", canceled_at=" + canceled_at +
			'}';
	}
}

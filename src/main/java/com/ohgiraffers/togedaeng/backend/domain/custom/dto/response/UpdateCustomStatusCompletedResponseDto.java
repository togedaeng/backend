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
public class UpdateCustomStatusCompletedResponseDto {

	private Long id;
	private Long dogId;
	private Long adminId;
	private Status status;
	private String renderedImageUrl;
	private LocalDateTime completedAt;

	@Override
	public String toString() {
		return "UpdateCustomStatusCompletedResponseDto{" +
			"id=" + id +
			", dogId=" + dogId +
			", adminId=" + adminId +
			", status=" + status +
			", renderedImageUrl='" + renderedImageUrl + '\'' +
			", completed_at=" + completedAt +
			'}';
	}
}

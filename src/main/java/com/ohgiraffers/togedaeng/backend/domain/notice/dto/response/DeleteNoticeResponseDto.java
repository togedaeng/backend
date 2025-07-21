package com.ohgiraffers.togedaeng.backend.domain.notice.dto.response;

import java.time.LocalDateTime;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
public class DeleteNoticeResponseDto {

	private Long noticeId;
	private String title;
	private LocalDateTime deletedAt;

	@Override
	public String toString() {
		return "DeleteNoticeResponseDto{" +
			"noticeId=" + noticeId +
			", deletedAt=" + deletedAt +
			'}';
	}
}

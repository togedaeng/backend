package com.ohgiraffers.togedaeng.backend.domain.notice.dto.response;

import java.time.LocalDateTime;

import com.ohgiraffers.togedaeng.backend.domain.notice.entity.Category;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
public class NoticeListResponseDto {

	private Long id;
	private Category category;			// 공지 카테고리
	private String title;				// 공지 제목
	private String adminNickname; 		// 작성자 닉네임
	private LocalDateTime createdAt;	// 작성일

	@Override
	public String toString() {
		return "NoticeListResponseDto{" +
			"id=" + id +
			", category=" + category +
			", title='" + title + '\'' +
			", adminNickname='" + adminNickname + '\'' +
			", createdAt=" + createdAt +
			'}';
	}
}

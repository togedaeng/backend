package com.ohgiraffers.togedaeng.backend.domain.notice.dto.response;

import java.time.LocalDateTime;
import java.util.List;

import com.ohgiraffers.togedaeng.backend.domain.notice.entity.Category;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class UpdateNoticeResponseDto {

	private Long id;
	private String adminNickname;
	private Category category;
	private String title;
	private String content;
	private List<String> imageUrls;
	private LocalDateTime updatedAt;

	@Override
	public String toString() {
		return "UpdateNoticeResponseDto{" +
			"id=" + id +
			", adminNickname='" + adminNickname + '\'' +
			", category=" + category +
			", title='" + title + '\'' +
			", content='" + content + '\'' +
			", imageUrls=" + imageUrls +
			", updatedAt=" + updatedAt +
			'}';
	}
}

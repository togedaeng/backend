package com.ohgiraffers.togedaeng.backend.domain.notice.dto.response;

import java.time.LocalDateTime;
import java.util.List;

import com.ohgiraffers.togedaeng.backend.domain.notice.entity.Category;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
public class NoticeDetailResponseDto {

	private Long id;
	private Category category;
	private String title;
	private String content;
	private String authorNickname; // 작성자 닉네임
	private List<String> imageUrls;
	private LocalDateTime createdAt;
	private LocalDateTime updatedAt;

	@Override
	public String toString() {
		return "NoticeDetailResponseDto{" +
			"id=" + id +
			", category=" + category +
			", title='" + title + '\'' +
			", content='" + content + '\'' +
			", authorNickname='" + authorNickname + '\'' +
			", imageUrls=" + imageUrls +
			", createdAt=" + createdAt +
			", updatedAt=" + updatedAt +
			'}';
	}
}

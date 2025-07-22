package com.ohgiraffers.togedaeng.backend.domain.notice.dto.response;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

import com.ohgiraffers.togedaeng.backend.domain.notice.dto.request.NoticeImageDto;
import com.ohgiraffers.togedaeng.backend.domain.notice.entity.Category;
import com.ohgiraffers.togedaeng.backend.domain.notice.entity.Notice;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@NoArgsConstructor
@Getter
@Setter
public class NoticeDetailResponseDto {

	private Long id;
	private Category category;
	private String title;
	private String content;
	private String authorNickname; // 작성자 닉네임
	private List<NoticeImageDto> images;
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
			", createdAt=" + createdAt +
			", updatedAt=" + updatedAt +
			'}';
	}

	public NoticeDetailResponseDto(Long id, Category category, String title, String content, String nickname,
		List<NoticeImageDto> images,
		LocalDateTime createdAt, LocalDateTime updatedAt) {
		this.id = id;
		this.category = category;
		this.title = title;
		this.content = content;
		this.authorNickname = authorNickname;
		this.images = images;
		this.createdAt = createdAt;
		this.updatedAt = updatedAt;
	}
}

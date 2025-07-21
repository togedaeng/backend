package com.ohgiraffers.togedaeng.backend.domain.notice.dto.response;

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
public class CreateNoticeResponseDto {

	private Long id;
	private String adminNickname;
	private Category category;
	private String title;
	private String content;
	private List<String> imageUrls;

	@Override
	public String toString() {
		return "CreateNoticeResponseDto{" +
			"id=" + id +
			", adminNickname='" + adminNickname + '\'' +
			", category=" + category +
			", title='" + title + '\'' +
			", content='" + content + '\'' +
			", imageUrls=" + imageUrls +
			'}';
	}
}

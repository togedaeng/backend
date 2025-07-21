package com.ohgiraffers.togedaeng.backend.domain.notice.dto.request;

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
public class CreateNoticeRequestDto {

	private Category category;
	private String title;
	private String content;
	private List<String> imageUrls;

	@Override
	public String toString() {
		return "CreateNoticeRequestDto{" +
			"category=" + category +
			", title='" + title + '\'' +
			", content='" + content + '\'' +
			", imageUrls=" + imageUrls +
			'}';
	}
}

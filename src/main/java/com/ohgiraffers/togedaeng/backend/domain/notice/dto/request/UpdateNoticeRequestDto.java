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
public class UpdateNoticeRequestDto {

	private String title;
	private String content;
	private Category category;
	private List<Long> deleteImageIds;

	@Override
	public String toString() {
		return "UpdateNoticeRequestDto{" +
			"title='" + title + '\'' +
			", content='" + content + '\'' +
			", category=" + category +
			", deleteImageIds=" + deleteImageIds +
			'}';
	}
}

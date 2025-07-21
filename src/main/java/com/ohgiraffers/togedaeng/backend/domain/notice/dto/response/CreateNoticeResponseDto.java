package com.ohgiraffers.togedaeng.backend.domain.notice.dto.response;

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
	private String imageUrl; // 이미지가 없는 경우 null이 될 수 있음


	@Override
	public String toString() {
		return "CreateNoticeResponseDto{" +
			"id=" + id +
			", adminNickname='" + adminNickname + '\'' +
			", category=" + category +
			", title='" + title + '\'' +
			", content='" + content + '\'' +
			", imageUrl='" + imageUrl + '\'' +
			'}';
	}
}

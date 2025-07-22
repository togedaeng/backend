package com.ohgiraffers.togedaeng.backend.domain.inquiry.dto.request;

import java.util.List;

import com.ohgiraffers.togedaeng.backend.domain.inquiry.entity.Category;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class UpdateInquiryRequestDto {

	private Category category;
	private String title;
	private String content;
	private List<Long> deleteImageIds; // 삭제할 기존 이미지의 ID 리스트
}

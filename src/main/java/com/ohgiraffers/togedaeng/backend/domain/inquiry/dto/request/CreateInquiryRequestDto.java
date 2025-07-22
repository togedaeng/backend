package com.ohgiraffers.togedaeng.backend.domain.inquiry.dto.request;

import com.ohgiraffers.togedaeng.backend.domain.inquiry.entity.Category;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class CreateInquiryRequestDto {

	private Category category;
	private String title;
	private String content;
}

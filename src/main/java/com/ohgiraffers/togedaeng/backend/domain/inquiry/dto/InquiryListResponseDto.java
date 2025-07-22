package com.ohgiraffers.togedaeng.backend.domain.inquiry.dto;

import java.time.LocalDateTime;

import com.ohgiraffers.togedaeng.backend.domain.inquiry.entity.Category;
import com.ohgiraffers.togedaeng.backend.domain.inquiry.entity.Inquiry;
import com.ohgiraffers.togedaeng.backend.domain.inquiry.entity.Status;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class InquiryListResponseDto {

	private Long id;
	private Category category;
	private String title;
	private String authorNickname; // 작성자 닉네임
	private Status status; // 답변 상태
	private LocalDateTime createdAt;

	private InquiryListResponseDto(Long id, Category category, String title, String authorNickname,
		LocalDateTime createdAt, Status status) {
		this.id = id;
		this.category = category;
		this.title = title;
		this.authorNickname = authorNickname;
		this.createdAt = createdAt;
		this.status = status;
	}

	public static InquiryListResponseDto from(Inquiry inquiry) {
		return new InquiryListResponseDto(
			inquiry.getId(),
			inquiry.getCategory(),
			inquiry.getTitle(),
			inquiry.getUser().getNickname(),
			inquiry.getCreatedAt(),
			inquiry.getStatus()
		);
	}
}

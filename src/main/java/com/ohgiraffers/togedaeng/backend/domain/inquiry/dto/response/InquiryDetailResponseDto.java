package com.ohgiraffers.togedaeng.backend.domain.inquiry.dto.response;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

import com.ohgiraffers.togedaeng.backend.domain.inquiry.entity.Category;
import com.ohgiraffers.togedaeng.backend.domain.inquiry.entity.Inquiry;
import com.ohgiraffers.togedaeng.backend.domain.inquiry.entity.InquiryImage;
import com.ohgiraffers.togedaeng.backend.domain.inquiry.entity.Status;

import lombok.Getter;

@Getter
public class InquiryDetailResponseDto {

	private final Long inquiryId;
	private final Category category;
	private final String title;
	private final String content;
	private final String authorNickname;
	private final List<String> imageUrls;
	private final Status status;
	private final LocalDateTime createdAt;
	private final InquiryAnswerDto answer; // 답변 정보 (null일 수 있음)

	// private 생성자
	private InquiryDetailResponseDto(Long inquiryId, Category category, String title, String content, String authorNickname, List<String> imageUrls, Status status, LocalDateTime createdAt, InquiryAnswerDto answer) {
		this.inquiryId = inquiryId;
		this.category = category;
		this.title = title;
		this.content = content;
		this.authorNickname = authorNickname;
		this.imageUrls = imageUrls;
		this.status = status;
		this.createdAt = createdAt;
		this.answer = answer;
	}

	// Inquiry 엔티티를 DTO로 변환하는 정적 팩토리 메소드
	public static InquiryDetailResponseDto from(Inquiry inquiry) {
		// 답변이 존재할 경우에만 DTO로 변환, 없으면 null
		InquiryAnswerDto answerDto = inquiry.getInquiryAnswer() != null ?
			InquiryAnswerDto.from(inquiry.getInquiryAnswer()) : null;

		return new InquiryDetailResponseDto(
			inquiry.getId(),
			inquiry.getCategory(),
			inquiry.getTitle(),
			inquiry.getContent(),
			inquiry.getUser().getNickname(),
			inquiry.getImages().stream()
				.map(InquiryImage::getImageUrl)
				.collect(Collectors.toList()),
			inquiry.getStatus(),
			inquiry.getCreatedAt(),
			answerDto
		);
	}
}

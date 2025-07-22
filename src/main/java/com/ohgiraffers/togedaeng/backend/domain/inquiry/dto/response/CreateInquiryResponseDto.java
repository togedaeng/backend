package com.ohgiraffers.togedaeng.backend.domain.inquiry.dto.response;

import com.ohgiraffers.togedaeng.backend.domain.inquiry.entity.Category;
import com.ohgiraffers.togedaeng.backend.domain.inquiry.entity.Inquiry;
import com.ohgiraffers.togedaeng.backend.domain.inquiry.entity.InquiryImage;
import com.ohgiraffers.togedaeng.backend.domain.inquiry.entity.Status;
import lombok.Getter;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Getter
public class CreateInquiryResponseDto {

	private final Long id;
	private final String authorNickname;
	private final Category category;
	private final String title;
	private final String content;
	private final List<String> imageUrls;
	private final Status status;
	private final LocalDateTime createdAt;

	public static CreateInquiryResponseDto from(Inquiry inquiry) {
		List<String> imageUrls = inquiry.getImages().stream()
			.map(InquiryImage::getImageUrl)
			.collect(Collectors.toList());

		return new CreateInquiryResponseDto(
			inquiry.getId(),
			inquiry.getUser().getNickname(),
			inquiry.getCategory(),
			inquiry.getTitle(),
			inquiry.getContent(),
			imageUrls,
			inquiry.getStatus(),
			inquiry.getCreatedAt()
		);
	}

	// private constructor
	private CreateInquiryResponseDto(Long id, String authorNickname, Category category, String title, String content, List<String> imageUrls, Status status, LocalDateTime createdAt) {
		this.id = id;
		this.authorNickname = authorNickname;
		this.category = category;
		this.title = title;
		this.content = content;
		this.imageUrls = imageUrls;
		this.status = status;
		this.createdAt = createdAt;
	}
}

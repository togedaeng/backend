package com.ohgiraffers.togedaeng.backend.domain.inquiry.dto.response;

import java.time.LocalDateTime;

import com.ohgiraffers.togedaeng.backend.domain.inquiry.entity.InquiryAnswer;

import lombok.Getter;

@Getter
public class InquiryAnswerDto {

	private final Long answerId;
	private final String content;
	private final String authorNickname; // 답변 작성자 (관리자) 닉네임
	private final LocalDateTime createdAt;

	// private 생성자로 팩토리 메소드를 통한 생성 강제
	private InquiryAnswerDto(Long answerId, String content, String authorNickname, LocalDateTime createdAt) {
		this.answerId = answerId;
		this.content = content;
		this.authorNickname = authorNickname;
		this.createdAt = createdAt;
	}

	// InquiryAnswer 엔티티를 DTO로 변환하는 정적 팩토리 메소드
	public static InquiryAnswerDto from(InquiryAnswer answer) {
		return new InquiryAnswerDto(
			answer.getId(),
			answer.getComment(), // 엔티티의 comment 필드 사용
			answer.getUser().getNickname(),
			answer.getCreatedAt()
		);
	}
}

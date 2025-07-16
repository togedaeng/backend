package com.ohgiraffers.togedaeng.backend.domain.custom.dto.response;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

import com.ohgiraffers.togedaeng.backend.domain.custom.entity.ImageValidationError;
import com.ohgiraffers.togedaeng.backend.domain.custom.entity.Status;
import com.ohgiraffers.togedaeng.backend.domain.dog.entity.Gender;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
public class CustomDetailResponseDto {

	private Long id;
	private Status status;
	private String requesterEmail;
	private String requesterNickname;
	private String adminNickname;
	private LocalDateTime createdAt;
	private LocalDateTime startedAt;
	private LocalDateTime holdCreatedAt;
	private ImageValidationError holdReason;
	private LocalDateTime completedAt;
	private LocalDateTime canceledAt;

	private String dogName;
	private Gender dogGender;
	private LocalDate dogBirth;
	private List<String> personalities; // 성격 1, 2
	private List<String> dogImages;     // 이미지 URL 여러 장

	@Override
	public String toString() {
		return "CustomDetailResponseDto{" +
			"id=" + id +
			", status=" + status +
			", requesterEmail='" + requesterEmail + '\'' +
			", requesterNickname='" + requesterNickname + '\'' +
			", adminNickname='" + adminNickname + '\'' +
			", createdAt=" + createdAt +
			", startedAt=" + startedAt +
			", holdCreatedAt=" + holdCreatedAt +
			", holdReason='" + holdReason + '\'' +
			", completedAt=" + completedAt +
			", canceledAt=" + canceledAt +
			", dogName='" + dogName + '\'' +
			", dogGender=" + dogGender +
			", dogBirth=" + dogBirth +
			", personalities=" + personalities +
			", dogImages=" + dogImages +
			'}';
	}
}

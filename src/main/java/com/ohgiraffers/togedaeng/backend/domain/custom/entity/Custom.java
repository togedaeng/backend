package com.ohgiraffers.togedaeng.backend.domain.custom.entity;

import java.time.LocalDate;
import java.time.LocalDateTime;

import com.ohgiraffers.togedaeng.backend.domain.dog.entity.Gender;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

@Entity
@Table(name = "dogs_custom_requests")
public class Custom {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "id")
	private Long customId;

	@Column(name = "user_id", nullable = false)
	private Long userId;

	// 반려견 이름
	private String name;

	// 반려견 성별
	@Enumerated(EnumType.STRING)
	private Gender gender;

	// 반려견 생년월일
	private LocalDate birth;

	// 성격

	// 주인을 부르는 애칭
	@Column(name = "call_name")
	private String callName;

	// 요청 사진 URL
	@Column(name = "image_url")
	private String imageUrl;

	// 렌더링 완료된 모델 URL
	@Column(name = "rendered_url")
	private String renderedUrl;

	// 커스텀 상태
	@Enumerated(EnumType.STRING)
	private Status status;

	// 요청일
	@Column(name = "craeted_at")
	private LocalDateTime createdAt;
}

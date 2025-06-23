package com.ohgiraffers.togedaeng.backend.domain.dog.entity;

import java.time.LocalDate;
import java.time.LocalDateTime;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@SuppressWarnings("checkstyle:RightCurly")
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@Entity
@Table(name = "dogs")
public class Dog {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	@Column(name = "user_id", nullable = false)
	private Long userId;

	@Column(name = "personality_combination_id", nullable = false)
	private Long personalityCombinationId;

	// 반려견 이름
	private String name;

	// 반려견 성별
	@Enumerated(EnumType.STRING)
	private Gender gender;

	// 반려견 생년월일
	private LocalDate birth;

	// 주인을 부르는 애칭
	@Column(name = "call_name")
	private String callName;

	// 강아지 상태
	@Enumerated(EnumType.STRING)
	private Status status;

	// 원본 이미지 URL
	@Column(name = "image_url")
	private String imageUrl;

	// 렌더링 이미지 URL
	@Column(name = "rendered_url")
	private String renderedUrl;

	// 등록일
	@Column(name = "created_at")
	private LocalDateTime createdAt;

	// 수정일
	@Column(name = "updated_at")
	private LocalDateTime updatedAt;

	// 삭제일
	@Column(name = "deleted_at")
	private LocalDateTime deletedAt;
}
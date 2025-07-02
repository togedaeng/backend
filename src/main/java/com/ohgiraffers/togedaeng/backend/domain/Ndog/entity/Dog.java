package com.ohgiraffers.togedaeng.backend.domain.Ndog.entity;

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

	// 반려견 이름
	@Column(length = 50, nullable = false)
	private String name;

	// 반려견 성별
	@Enumerated(EnumType.STRING)
	private Gender gender;

	// 반려견 생년월일
	private LocalDate birth;

	@Enumerated(EnumType.STRING)
	private Status status;

	// 렌더링 이미지 URL
	@Column(name = "rendered_url")
	private String renderedUrl;

	@Column(name = "created_at")
	private LocalDateTime createdAt;

	@Column(name = "updated_at")
	private LocalDateTime updatedAt;

	@Column(name = "deleted_at")
	private LocalDateTime deletedAt;
}

package com.ohgiraffers.togedaeng.backend.domain.dog.entity;

import java.time.LocalDate;
import java.time.LocalDateTime;

import com.ohgiraffers.togedaeng.backend.domain.personality.entity.PersonalityCombination;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.OneToOne;
import jakarta.persistence.Table;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Builder
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
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

	// 반려견 주인 ID -> 추가
	@Column(name = "user_id", nullable = false)
	private Long userId;

	// 반려견 애칭 -> 추가
	@Column(name = "call_name", nullable = false)
	private String callName;

	// 반려견 성격 조합 ID -> 추가
	@Column(name = "personality_combination_id")
	private Long personalityCombinationId;

	// 반려견 생년월일
	private LocalDate birth;

	@Enumerated(EnumType.STRING)
	private Status status;

	@OneToOne(mappedBy = "dog", cascade = CascadeType.ALL, orphanRemoval = true)
	private PersonalityCombination personalityCombination;

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

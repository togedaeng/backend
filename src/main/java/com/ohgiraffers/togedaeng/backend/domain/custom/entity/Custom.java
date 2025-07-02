package com.ohgiraffers.togedaeng.backend.domain.custom.entity;

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
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@Entity
@Table(name = "customs")
public class Custom {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	// 요청 들어온 반려견 ID
	@Column(name = "dog_id", nullable = false)
	private Long dogId;

	// 담당자 ID (관리자)
	@Column(name = "admin_id", nullable = false)
	private Long adminId;

	@Enumerated(EnumType.STRING)
	private Status status;

	// 커스텀 등록일
	@Column(name = "created_at", nullable = false)
	private LocalDateTime createdAt;

	// 커스텀 시작일
	@Column(name = "started_at")
	private LocalDateTime startedAt;

	// 커스텀 완료일
	@Column(name = "completed_at")
	private LocalDateTime completedAt;

	// 커스텀 취소일
	@Column(name = "canceled_at")
	private LocalDateTime canceledAt;
}

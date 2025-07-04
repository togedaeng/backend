package com.ohgiraffers.togedaeng.backend.domain.custom.entity;

import java.time.LocalDateTime;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@Getter
@Setter
@Entity
@Table(name = "holds")
public class Hold {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	@Column(name = "custom_id", nullable = false)
	private Long customId;

	// 보류 사유
	private String reason;

	// 커스텀 보류일
	@Column(name = "created_at", nullable = false)
	private LocalDateTime createdAt;

	// 보류 수정일 (보류 사유 수정)
	@Column(name = "updated_at")
	private LocalDateTime updatedAt;

	// 보류 삭제일 (커스텀 요청 상태가 CANCELLED로 바뀌었을 때)
	@Column(name = "deleted_at")
	private LocalDateTime deletedAt;

	public Hold(Long customId, String reason, LocalDateTime createdAt) {
		this.customId = customId;
		this.reason = reason;
		this.createdAt = createdAt;
	}

}

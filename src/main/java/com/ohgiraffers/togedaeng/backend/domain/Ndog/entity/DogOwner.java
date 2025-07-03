package com.ohgiraffers.togedaeng.backend.domain.Ndog.entity;

import java.time.LocalDateTime;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import jakarta.validation.constraints.NotBlank;
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
@Table(name = "dog_owners")
public class DogOwner {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	@Column(name = "user_id", nullable = false)
	private Long userId;

	@Column(name = "dog_id", nullable = false)
	private Long dogId;

	@Column(length = 50, nullable = false)
	private String name;

	@Column(name = "created_at", nullable = false)
	private LocalDateTime createdAt;

	@Column(name = "updated_at")
	private LocalDateTime updatedAt;

	public DogOwner(Long userId, Long dogId, @NotBlank(message = "애칭은 필수입니다.") String callName, LocalDateTime createdAt) {
		this.userId = userId;
		this.dogId = dogId;
		this.name = callName;
		this.createdAt = createdAt;
	}
}

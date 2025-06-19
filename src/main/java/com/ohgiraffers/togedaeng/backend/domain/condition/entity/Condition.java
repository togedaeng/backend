package com.ohgiraffers.togedaeng.backend.domain.condition.entity;

import java.time.LocalDateTime;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@AllArgsConstructor
@Getter
@Setter
@ToString
@Entity
@Table(name = "dog_condition")
public class Condition {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	@Column(name = "dog_id", nullable = false)
	private Long dogId;

	// 포만감
	private int fullness;

	// 수분감
	private int waterful;

	// 친밀도
	private int affection;

	// 반려견레벨
	private int level;

	// 경험치
	private int exp;

	@Column(name = "updated_at")
	private LocalDateTime updatedAt;

	public Condition() {
	}
}

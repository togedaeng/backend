package com.ohgiraffers.togedaeng.backend.domain.personality.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

@Entity
@Table(name = "dog_personalities")
public class DogPersonality {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	// 성격 이름
	@Column(nullable = false)
	private String name;

	// 성격 설명
	@Column(nullable = false)
	private String description;
}

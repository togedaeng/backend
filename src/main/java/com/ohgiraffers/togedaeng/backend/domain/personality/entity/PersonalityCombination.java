package com.ohgiraffers.togedaeng.backend.domain.personality.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

@Entity
@Table(name = "personality_combinations")
public class PersonalityCombination {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	// 성격 ID 1
	@Column(name = "personality_id_1", nullable = false)
	private Long personalityId1;

	// 성격 ID 2
	@Column(name = "personality_id_2")
	private Long personalityId2;
}

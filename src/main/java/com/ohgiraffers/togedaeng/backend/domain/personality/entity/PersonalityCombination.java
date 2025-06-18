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

	public PersonalityCombination() {
	}

	public PersonalityCombination(Long id, Long personalityId1, Long personalityId2) {
		this.id = id;
		this.personalityId1 = personalityId1;
		this.personalityId2 = personalityId2;
	}

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public Long getPersonalityId1() {
		return personalityId1;
	}

	public void setPersonalityId1(Long personalityId1) {
		this.personalityId1 = personalityId1;
	}

	public Long getPersonalityId2() {
		return personalityId2;
	}

	public void setPersonalityId2(Long personalityId2) {
		this.personalityId2 = personalityId2;
	}

	@Override
	public String toString() {
		return "PersonalityCombination{" +
			"id=" + id +
			", personalityId1=" + personalityId1 +
			", personalityId2=" + personalityId2 +
			'}';
	}
}

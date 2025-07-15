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

	public DogPersonality() {
	}

	public DogPersonality(Long id, String name, String description) {
		this.id = id;
		this.name = name;
		this.description = description;
	}

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	@Override
	public String toString() {
		return "DogPersonality{" +
			"id=" + id +
			", name='" + name + '\'' +
			", description='" + description + '\'' +
			'}';
	}
}

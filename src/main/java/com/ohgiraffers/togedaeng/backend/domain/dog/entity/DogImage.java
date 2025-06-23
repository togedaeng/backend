package com.ohgiraffers.togedaeng.backend.domain.dog.entity;

import java.time.LocalDateTime;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@Entity
@Table(name = "dog_image")
public class DogImage {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	@Column(name = "dog_id", nullable = false)
	private Long dogId;

	@Column(name = "image_url", nullable = false)
	private String imageUrl;

	@Column(name = "uploaded_at")
	private LocalDateTime uploadedAt = LocalDateTime.now();

	public DogImage(Long id, Long dogId, String imageUrl, LocalDateTime uploadedAt) {
		this.id = id;
		this.dogId = dogId;
		this.imageUrl = imageUrl;
		this.uploadedAt = uploadedAt;
	}
}


// package com.ohgiraffers.togedaeng.backend.domain.dog.entity;
//
// import java.time.LocalDateTime;
//
// import jakarta.persistence.Column;
// import jakarta.persistence.Entity;
// import jakarta.persistence.EnumType;
// import jakarta.persistence.Enumerated;
// import jakarta.persistence.GeneratedValue;
// import jakarta.persistence.GenerationType;
// import jakarta.persistence.Id;
// import jakarta.persistence.Table;
// import lombok.Getter;
// import lombok.NoArgsConstructor;
// import lombok.Setter;
//
// @Getter
// @Setter
// @NoArgsConstructor
// @Entity
// @Table(name = "dog_image")
// public class DogImage {
//
// 	@Id
// 	@GeneratedValue(strategy = GenerationType.IDENTITY)
// 	private Long id;
//
// 	@Column(name = "dog_id", nullable = false)
// 	private Long dogId;
//
// 	@Column(name = "image_url", nullable = false)
// 	private String imageUrl;
//
// 	@Column(name = "model_url")
// 	private String modelUrl;
//
// 	@Enumerated(EnumType.STRING)
// 	private Type type;
//
// 	@Column(name = "created_at")
// 	private LocalDateTime createdAt = LocalDateTime.now();
//
// 	@Column(name = "updated_at")
// 	private LocalDateTime updatedAt;
//
// 	public DogImage(Long id, Long dogId, String imageUrl, String modelUrl, Type type, LocalDateTime createdAt,
// 		LocalDateTime updatedAt) {
// 		this.id = id;
// 		this.dogId = dogId;
// 		this.imageUrl = imageUrl;
// 		this.modelUrl = modelUrl;
// 		this.type = type;
// 		this.createdAt = createdAt;
// 		this.updatedAt = updatedAt;
// 	}
// }
//

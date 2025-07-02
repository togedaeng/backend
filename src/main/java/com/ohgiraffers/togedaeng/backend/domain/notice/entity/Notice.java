// package com.ohgiraffers.togedaeng.backend.domain.notice.entity;
//
// import jakarta.persistence.Column;
// import jakarta.persistence.Entity;
// import jakarta.persistence.EnumType;
// import jakarta.persistence.Enumerated;
// import jakarta.persistence.GeneratedValue;
// import jakarta.persistence.GenerationType;
// import jakarta.persistence.Id;
// import jakarta.persistence.Table;
//
// @Entity
// @Table(name = "notices")
// public class Notice {
//
// 	@Id
// 	@GeneratedValue(strategy = GenerationType.IDENTITY)
// 	private Long id;
//
// 	@Column(name = "user_id", nullable = false)
// 	private Long userId;
//
// 	// 공지 제목
// 	private String title;
//
//
//
// 	// 공지 카테고리
// 	@Enumerated(EnumType.STRING)
// 	private Category category;
// }

package com.ohgiraffers.togedaeng.backend.domain.inquiry.entity;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import com.ohgiraffers.togedaeng.backend.domain.user.model.entity.User;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
import jakarta.persistence.OneToOne;
import jakarta.persistence.Table;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@Getter
@Setter
@Builder
@Entity
@Table(name = "inquiries")
public class Inquiry {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "user_id", nullable = false)
	private User user;

	// 문의 카테고리
	@Enumerated(EnumType.STRING)
	private Category category;

	// 문의 제목
	@Column(nullable = false)
	private String title;

	// 문의 내용
	@Column(nullable = false)
	private String content;

	// 이미지 url
	@OneToMany(mappedBy = "inquiry", cascade = CascadeType.ALL, orphanRemoval = true)
	private List<InquiryImage> images = new ArrayList<>();

	@OneToOne(mappedBy = "inquiry", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
	private InquiryAnswer inquiryAnswer;

	@Enumerated(EnumType.STRING)
	private Status status = Status.WAITING;

	@Column(name = "created_at", nullable = false)
	private LocalDateTime createdAt;

	@Column(name = "updated_at")
	private LocalDateTime updatedAt;

	public void addImage(InquiryImage image) {
		this.images.add(image);
		image.setInquiry(this);
	}

	public void update(Category category, String title, String content) {
		this.category = category;
		this.title = title;
		this.content = content;
		this.updatedAt = LocalDateTime.now();
	}
}

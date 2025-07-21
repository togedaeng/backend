package com.ohgiraffers.togedaeng.backend.domain.notice.entity;

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
@Table(name = "notices")
public class Notice {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "user_id", nullable = false)
	private User user;

	// 공지 카테고리
	@Enumerated(EnumType.STRING)
	private Category category;

	// 공지 제목
	@Column(nullable = false)
	private String title;

	// 공지 내용
	@Column(nullable = false)
	private String content;

	// 이미지 url
	@Builder.Default
	@OneToMany(mappedBy = "notice", cascade = CascadeType.ALL, orphanRemoval = true)
	private List<NoticeImage> images = new ArrayList<>();

	@Enumerated(EnumType.STRING)
	private Status status = Status.PUBLISHED;

	@Column(name = "created_at", nullable = false)
	private LocalDateTime createdAt;

	@Column(name = "updated_at")
	private LocalDateTime updatedAt;

	@Column(name = "deleted_at")
	private LocalDateTime deletedAt;

	public void update(String title, String content, Category category) {
		this.title = title;
		this.content = content;
		this.category = category;
		this.updatedAt = LocalDateTime.now();
	}

	public void addImage(NoticeImage noticeImage) {
		this.images.add(noticeImage);
		noticeImage.setNotice(this);
	}

	public void softDelete() {
		this.status = Status.DELETED;
		this.deletedAt = LocalDateTime.now();
	}
}

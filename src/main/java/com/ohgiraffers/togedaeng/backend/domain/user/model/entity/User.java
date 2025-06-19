package com.ohgiraffers.togedaeng.backend.domain.user.model.entity;

import java.time.LocalDate;
import java.time.LocalDateTime;

import com.ohgiraffers.togedaeng.backend.domain.dog.entity.Gender;
import com.ohgiraffers.togedaeng.backend.domain.dog.entity.Status;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.PrePersist;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

@Builder
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@ToString // 콘솔 테스트용
@Entity
@Table(name = "users")
public class User {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "id")
	private Long id;

	@Column(name = "nickname", nullable = false)
	private String nickname;

	@Column(name = "gender", nullable = false)
	private Gender gender;

	@Column(name = "birth", nullable = false)
	private LocalDate birth;

	@Column(name = "email", nullable = false)
	private String email;

	@Builder.Default
	@Column(name = "role", nullable = false)
	private Role role = Role.USER;

	@Column(name = "status", nullable = false)
	private Status status;

	@Builder.Default
	@Column(name = "created_at", nullable = false)
	private LocalDateTime createdAt = LocalDateTime.now();

	@Column(name = "updated_at")
	private LocalDateTime updatedAt;

	@Column(name = "deleted_at")
	private LocalDateTime deletedAt;

	@PrePersist
	public void prePersist() {
		this.createdAt = (this.createdAt == null) ? LocalDateTime.now() : this.createdAt;
	}
}

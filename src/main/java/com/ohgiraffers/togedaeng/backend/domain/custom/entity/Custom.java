package com.ohgiraffers.togedaeng.backend.domain.custom.entity;

import java.time.LocalDate;

import com.nimbusds.openid.connect.sdk.claims.Gender;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

@Entity
@Table(name = "dogs_custom_requests")
public class Custom {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "id")
	private Long customId;

	// private Long userId;

	// 반려견 이름
	private String name;

	// 반려견 성별
	private Gender gender;

	@Column(name = "birth_date")
	private LocalDate birthDate;

}

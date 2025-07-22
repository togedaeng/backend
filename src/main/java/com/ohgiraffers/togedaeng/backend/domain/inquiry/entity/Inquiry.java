package com.ohgiraffers.togedaeng.backend.domain.inquiry.entity;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

@Entity
@Table(name = "inquiries")
public class Inquiry {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;


}

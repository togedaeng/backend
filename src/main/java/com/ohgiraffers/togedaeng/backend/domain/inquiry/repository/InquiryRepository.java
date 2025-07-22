package com.ohgiraffers.togedaeng.backend.domain.inquiry.repository;

import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.ohgiraffers.togedaeng.backend.domain.inquiry.entity.Inquiry;

public interface InquiryRepository extends JpaRepository<Inquiry, Long> {

	@Query(value = "SELECT i FROM Inquiry i JOIN FETCH i.user u ORDER BY i.createdAt DESC",
		countQuery = "SELECT COUNT(i) FROM Inquiry i")
	Page<Inquiry> findAllWithUser(Pageable pageable);

	@Query("SELECT i FROM Inquiry i " +
		"JOIN FETCH i.user u " +
		"LEFT JOIN FETCH i.images " +
		"LEFT JOIN FETCH i.inquiryAnswer ia " +
		"LEFT JOIN FETCH ia.user " + // 답변 작성자 정보
		"WHERE i.id = :inquiryId")
	Optional<Inquiry> findInquiryDetailsById(@Param("inquiryId") Long inquiryId);
}

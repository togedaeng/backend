package com.ohgiraffers.togedaeng.backend.domain.inquiry.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import com.ohgiraffers.togedaeng.backend.domain.inquiry.entity.Inquiry;

public interface InquiryRepository extends JpaRepository<Inquiry, Long> {

	@Query(value = "SELECT i FROM Inquiry i JOIN FETCH i.user u ORDER BY i.createdAt DESC",
		countQuery = "SELECT COUNT(i) FROM Inquiry i")
	Page<Inquiry> findAllWithUser(Pageable pageable);
}

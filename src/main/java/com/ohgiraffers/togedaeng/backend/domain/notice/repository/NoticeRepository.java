package com.ohgiraffers.togedaeng.backend.domain.notice.repository;

import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.ohgiraffers.togedaeng.backend.domain.notice.entity.Notice;

public interface NoticeRepository extends JpaRepository<Notice, Long> {

	@Query("SELECT n FROM Notice n JOIN FETCH n.user")
	Page<Notice> findAllWithUser(Pageable pageable);

	@Query("SELECT n FROM Notice n JOIN FETCH n.user WHERE n.id = :id")
	Optional<Notice> findByIdWithUser(@Param("id") Long id);
}

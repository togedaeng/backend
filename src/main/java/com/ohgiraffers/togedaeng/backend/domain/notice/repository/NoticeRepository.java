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

	@Query("SELECT DISTINCT n FROM Notice n " +
		"JOIN FETCH n.user u " +
		"LEFT JOIN FETCH n.images i " +
		"WHERE n.id = :noticeId")
	Optional<Notice> findNoticeDetailsById(@Param("noticeId") Long noticeId);

}

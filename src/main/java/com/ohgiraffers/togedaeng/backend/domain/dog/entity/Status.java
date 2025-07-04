package com.ohgiraffers.togedaeng.backend.domain.dog.entity;

public enum Status {
	REGISTERED, // 등록
	APPROVED,   // 승인
	SUSPENDED,  // 정지 (요청이 보류 or 취소)
	REMOVED     // 삭제
}

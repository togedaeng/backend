package com.ohgiraffers.togedaeng.backend.domain.notice.service;

import org.springframework.stereotype.Service;

import com.ohgiraffers.togedaeng.backend.domain.notice.repository.NoticeRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class NoticeService {

	private final NoticeRepository noticeRepository;

}

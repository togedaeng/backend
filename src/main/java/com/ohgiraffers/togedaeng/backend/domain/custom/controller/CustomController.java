package com.ohgiraffers.togedaeng.backend.domain.custom.controller;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.ohgiraffers.togedaeng.backend.domain.Ndog.controller.DogController;

@RestController
@RequestMapping("/api/custom")
public class CustomController {

	Logger log = LoggerFactory.getLogger(DogController.class);

	// 커스텀 요청 조회

	// 커스텀 상태 변경
}

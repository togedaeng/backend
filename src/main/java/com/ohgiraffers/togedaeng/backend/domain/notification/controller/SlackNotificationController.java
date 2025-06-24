package com.ohgiraffers.togedaeng.backend.domain.notification.controller;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.ohgiraffers.togedaeng.backend.domain.notification.dto.request.SendSlackNotifyRequestDto;
import com.ohgiraffers.togedaeng.backend.domain.notification.service.SlackNotificationService;

@RestController
@RequestMapping("api/slack")
public class SlackNotificationController {
	private final SlackNotificationService slackNotificationService;

	public SlackNotificationController(SlackNotificationService slackNotificationService) {
		this.slackNotificationService = slackNotificationService;
	}

	/**
	 * Slack 메세지 전송
	 *
	 * @param sendSlackNotifyRequestDto 강아지 커스텀 요청 Dto
	 * @return 처리 상태 반환
	 * */
	@PostMapping("/request")
	public ResponseEntity<Object> sendSlackNotification(
		@RequestBody SendSlackNotifyRequestDto sendSlackNotifyRequestDto) {
		slackNotificationService.sendSlackNotification(sendSlackNotifyRequestDto.getMessage());
		return new ResponseEntity<>(HttpStatus.OK);
	}
}

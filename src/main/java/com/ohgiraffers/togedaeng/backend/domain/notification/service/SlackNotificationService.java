package com.ohgiraffers.togedaeng.backend.domain.notification.service;

import java.io.IOException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import com.ohgiraffers.togedaeng.backend.domain.dog.dto.response.CreateDogResponseDto;
import com.slack.api.Slack;
import com.slack.api.webhook.Payload;
import com.slack.api.webhook.WebhookResponse;

@Service
public class SlackNotificationService {
	private static final Logger logger = LoggerFactory.getLogger(SlackNotificationService.class);
	private final Slack slack = Slack.getInstance();

	@Value("${slack.webhook-url}")
	private String webhookUrl;

	/**
	 * Slack 메세지 전송
	 * @param createDogResponseDto 등록된 강아지 DTO
	 */
	public void sendSlackNotification(CreateDogResponseDto createDogResponseDto) {
		/*
		 * 이런 알람이 가도록 해야함.
		 * “[nickname]의 커스텀 요청이 들어왔습니다.
		 * 현재 대기 중인 커스텀 요청 수 : 7”
		 * */
		String paramText = createDogResponseDto.getName();
		Payload payload = Payload.builder().text(paramText).build();
		try {
			WebhookResponse response = slack.send(webhookUrl, payload);
			logger.info("Slack notification sent to Slack");
		} catch (IOException e) {
			throw new RuntimeException(e);
		}
	}
}

package com.ohgiraffers.togedaeng.backend.domain.notification.service;

import java.io.IOException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

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
	 *
	 * @param paramText 전송 메세지
	 */
	public void sendSlackNotification(String paramText) {
		Payload payload = Payload.builder().text(paramText).build();
		try {
			WebhookResponse response = slack.send(webhookUrl, payload);
			logger.info("Slack notification sent to Slack");
		} catch (IOException e) {
			throw new RuntimeException(e);
		}
	}
}

package com.ohgiraffers.togedaeng.backend.domain.notification.service;

import java.io.IOException;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import com.ohgiraffers.togedaeng.backend.domain.dog.dto.response.CreateDogResponseDto;
import com.ohgiraffers.togedaeng.backend.domain.dog.dto.response.DogResponseDto;
import com.ohgiraffers.togedaeng.backend.domain.dog.entity.Status;
import com.ohgiraffers.togedaeng.backend.domain.dog.service.DogService;
import com.ohgiraffers.togedaeng.backend.domain.user.model.entity.User;
import com.ohgiraffers.togedaeng.backend.domain.user.repository.UserRepository;
import com.slack.api.Slack;
import com.slack.api.webhook.Payload;

@Service
public class SlackNotificationService {
	private static final Logger log = LoggerFactory.getLogger(SlackNotificationService.class);
	private final Slack slack = Slack.getInstance();
	private final UserRepository userRepository;
	private final DogService dogService;

	@Value("${spring.slack.webhook_url}")
	private String webhookUrl;

	public SlackNotificationService(UserRepository userRepository, DogService dogService) {
		this.userRepository = userRepository;
		this.dogService = dogService;
	}

	/**
	 * Slack 메세지 전송
	 * @param createDogResponseDto 등록된 강아지 DTO
	 */
	public void sendSlackNotification(CreateDogResponseDto createDogResponseDto) {
		Long userId = createDogResponseDto.getUserId();
		User user = userRepository.findById(userId).orElse(null);
		if (user == null) {
			throw new IllegalArgumentException("사용자 객체가 null입니다.");
		}
		String nickname = user.getNickname();
		if (nickname == null) {
			throw new IllegalStateException("사용자 ID " + userId + "의 닉네임이 설정되지 않았습니다");
		}
		int waitingCount = 0;
		List<DogResponseDto> checkStatus = dogService.getAllDogs();
		for (DogResponseDto dogResponseDto : checkStatus) {
			if (dogResponseDto.getStatus() == Status.REQUESTED) {
				waitingCount++;
			}
		}
		String paramText = nickname + "의 커스텀 요청이 들어왔습니다. \n현재 대기 중인 커스텀 요청 수 : " + waitingCount;
		Payload payload = Payload.builder().text(paramText).build();
		try {
			slack.send(webhookUrl, payload);
			log.info("Slack notification sent to Slack");
		} catch (IOException e) {
			throw new RuntimeException(e);
		}
	}
}

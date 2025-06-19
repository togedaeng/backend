package com.ohgiraffers.togedaeng.backend.domain.condition.schedule;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import com.ohgiraffers.togedaeng.backend.domain.condition.entity.Condition;
import com.ohgiraffers.togedaeng.backend.domain.condition.repository.ConditionRepository;

@Component
public class ConditionScheduler {

	Logger log = LoggerFactory.getLogger(ConditionScheduler.class);

	private final ConditionRepository conditionRepository;

	public ConditionScheduler(ConditionRepository conditionRepository) {
		this.conditionRepository = conditionRepository;
	}

	@Scheduled(fixedRate = 60 * 60 * 1000)  // 1시간마다 실행
	public void decreaseConditions() {
		log.info("Decreasing conditions");

		List<Condition> conditions = conditionRepository.findAll();

		for (Condition c : conditions) {
			Duration duration = Duration.between(c.getUpdatedAt(), LocalDateTime.now());

			if (duration.toHours() >= 3) {
				c.setFullness(Math.max(c.getFullness() - 5, 0));
			}
			if (duration.toHours() >= 2) {
				c.setWaterful(Math.max(c.getWaterful() - 5, 0));
			}
			if (duration.toHours() >= 6) {
				c.setAffection(Math.max(c.getAffection() - 10, 0));
			}

			c.setUpdatedAt(LocalDateTime.now());
			conditionRepository.save(c);
		}

		log.info("All conditions decreased");
	}
}

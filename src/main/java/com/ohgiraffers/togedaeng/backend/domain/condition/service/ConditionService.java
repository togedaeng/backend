package com.ohgiraffers.togedaeng.backend.domain.condition.service;

import org.springframework.stereotype.Service;

import com.ohgiraffers.togedaeng.backend.domain.condition.repository.ConditionRepository;

@Service
public class ConditionService {

	private ConditionRepository conditionRepository;

	public ConditionService(ConditionRepository conditionRepository) {
		this.conditionRepository = conditionRepository;
	}

	// 레벨 계산 메서드
	private int calculateLevel(int exp) {
		if (exp >= 2250)
			return 10;
		if (exp >= 1800)
			return 9;
		if (exp >= 1400)
			return 8;
		if (exp >= 1050)
			return 7;
		if (exp >= 750)
			return 6;
		if (exp >= 500)
			return 5;
		if (exp >= 300)
			return 4;
		if (exp >= 150)
			return 3;
		if (exp >= 50)
			return 2;
		return 1;
	}
}

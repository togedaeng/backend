package com.ohgiraffers.togedaeng.backend.domain.condition.service;

import org.springframework.stereotype.Service;

import com.ohgiraffers.togedaeng.backend.domain.condition.repository.ConditionRepository;

@Service
public class ConditionService {

	private ConditionRepository conditionRepository;

	public ConditionService(ConditionRepository conditionRepository) {
		this.conditionRepository = conditionRepository;
	}

}

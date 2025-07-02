package com.ohgiraffers.togedaeng.backend.domain.Ndog.controller;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/dog")
public class DogController {

	Logger log = LoggerFactory.getLogger(DogController.class);
}

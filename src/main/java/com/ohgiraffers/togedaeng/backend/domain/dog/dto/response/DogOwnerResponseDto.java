package com.ohgiraffers.togedaeng.backend.domain.dog.dto.response;

import java.time.LocalDateTime;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
public class DogOwnerResponseDto {

	private Long id;
	private String userNickname;
	private Long dogId;
	private String name; // 부르는 이름=callname
	private LocalDateTime createdAt;
	private LocalDateTime updatedAt;
}

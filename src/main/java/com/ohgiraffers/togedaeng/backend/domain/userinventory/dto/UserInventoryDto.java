package com.ohgiraffers.togedaeng.backend.domain.userinventory.dto;

import java.time.LocalDateTime;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class UserInventoryDto {
	private Long id;
	private Long itemId;
	private Long userId;
	private Integer quantity;
	private LocalDateTime createdAt;
	private LocalDateTime updatedAt;
}


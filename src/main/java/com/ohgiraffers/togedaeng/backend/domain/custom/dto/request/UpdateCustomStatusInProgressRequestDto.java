package com.ohgiraffers.togedaeng.backend.domain.custom.dto.request;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
public class UpdateCustomStatusInProgressRequestDto {

	private Long adminId;

	@Override
	public String toString() {
		return "UpdateCustomStatusInProgressDto{" +
			", adminId=" + adminId +
			'}';
	}
}

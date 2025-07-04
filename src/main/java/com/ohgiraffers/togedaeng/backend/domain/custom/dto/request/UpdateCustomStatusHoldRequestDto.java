package com.ohgiraffers.togedaeng.backend.domain.custom.dto.request;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
public class UpdateCustomStatusHoldRequestDto {

	private Long adminId;
	private String reason;

	@Override
	public String toString() {
		return "UpdateCustomStatusHoldRequestDto{" +
			"adminId=" + adminId +
			", reason='" + reason + '\'' +
			'}';
	}
}

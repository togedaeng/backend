package com.ohgiraffers.togedaeng.backend.domain.custom.dto.request;

import com.ohgiraffers.togedaeng.backend.domain.custom.entity.ImageValidationError;

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
	private ImageValidationError reason;

	@Override
	public String toString() {
		return "UpdateCustomStatusHoldRequestDto{" +
			"adminId=" + adminId +
			", reason='" + reason + '\'' +
			'}';
	}
}

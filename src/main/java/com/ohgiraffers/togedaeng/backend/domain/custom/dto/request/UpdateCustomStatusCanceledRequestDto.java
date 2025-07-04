package com.ohgiraffers.togedaeng.backend.domain.custom.dto.request;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
public class UpdateCustomStatusCanceledRequestDto {

	private Long adminId;

	@Override
	public String toString() {
		return "UpdateCustomStatusCanceledRequestDto{" +
			"adminId=" + adminId +
			'}';
	}
}

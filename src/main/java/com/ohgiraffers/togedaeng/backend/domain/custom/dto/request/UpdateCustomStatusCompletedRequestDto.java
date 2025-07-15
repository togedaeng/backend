package com.ohgiraffers.togedaeng.backend.domain.custom.dto.request;

import org.springframework.web.multipart.MultipartFile;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
public class UpdateCustomStatusCompletedRequestDto {

	private Long adminId;
	private MultipartFile renderedImage;

	@Override
	public String toString() {
		return "UpdateCustomStatusCompletedRequestDto{" +
			"adminId=" + adminId +
			", renderedImage=" + renderedImage +
			'}';
	}
}

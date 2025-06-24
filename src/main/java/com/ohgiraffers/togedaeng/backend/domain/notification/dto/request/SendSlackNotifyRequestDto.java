package com.ohgiraffers.togedaeng.backend.domain.notification.dto.request;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class SendSlackNotifyRequestDto {
	private String nickname;
	private String waitingCount;

	@Override
	public String toString() {
		return "SendSlackNotifyRequestDto{" +
			"nickname='" + nickname + '\'' +
			", waitingCount='" + waitingCount + '\'' +
			'}';
	}
}



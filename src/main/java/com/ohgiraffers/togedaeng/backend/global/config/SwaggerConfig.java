package com.ohgiraffers.togedaeng.backend.global.config;

import org.springdoc.core.models.GroupedOpenApi;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import io.swagger.v3.oas.models.info.Info;

@Configuration
public class SwaggerConfig {
	@Bean
	public GroupedOpenApi DogGroupedOpenApi() {
		return GroupedOpenApi
			.builder()
			.group("dog")
			.pathsToMatch("/api/dog/**")
			.addOpenApiCustomizer(
				openApi ->
					openApi.setInfo(
						new Info()
							.title("Togedaeng API")
							.description("투게댕 백엔드 API")
							.version("1.0.0") // 문서 버전: 1.0.0
					)
			)
			.build();
	}

	@Bean
	public GroupedOpenApi UserGroupedOpenApi() {
		return GroupedOpenApi
			.builder()
			.group("user")
			.pathsToMatch("/api/user/**")
			.addOpenApiCustomizer(
				openApi ->
					openApi.setInfo(
						new Info()
							.title("Togedaeng API")
							.description("투게댕 백엔드 API")
							.version("1.0.0") // 문서 버전: 1.0.0
					)
			)
			.build();
	}
}

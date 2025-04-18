package com.backend.config;

import org.springdoc.core.models.*;
import org.springframework.context.annotation.*;

@Configuration
public class SwaggerConfig {

	@Bean
	public GroupedOpenApi publicApi() {
		return GroupedOpenApi.builder()
				.group("public")
				.pathsToMatch("/**")
				.build();
	}
}
package com.backend.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import software.amazon.awssdk.auth.credentials.*;
import software.amazon.awssdk.regions.*;
import software.amazon.awssdk.services.s3.*;

@Configuration
public class AwsConfig {

	@Value("${aws.region}")
	private String region;

	@Value("${aws.access-key-id}")
	private String accessKeyId;

	@Value("${aws.secret-access-key}")
	private String secretAccessKey;

	@Bean
	public S3Client s3Client() {
		return S3Client.builder()
				.region(Region.of(region))
				.credentialsProvider(StaticCredentialsProvider.create(
						AwsBasicCredentials.create(accessKeyId, secretAccessKey)))
				.build();
	}
}
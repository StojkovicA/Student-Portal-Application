package com.backend.service;

import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.model.PutObjectRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.io.*;

@Service
public class AwsService {

	@Autowired
	private S3Client s3Client;

	@Value("${aws.s3.bucket-name}")
	private String bucketName;

	@Value("${aws.s3.bucket-url}") // Example: https://my-bucket.s3.amazonaws.com
	private String bucketUrl;

	public String uploadFile(File file, String subfolder, String fileName) {
		String key = subfolder + "/" + fileName;
		System.out.println(file.getName());

		PutObjectRequest putObjectRequest = PutObjectRequest.builder()
				.bucket(bucketName)
				.key(key)
				.build();

		s3Client.putObject(putObjectRequest, file.toPath());

		System.out.println( bucketUrl + "/" + key);

		return bucketUrl + "/" + key;
	}

}
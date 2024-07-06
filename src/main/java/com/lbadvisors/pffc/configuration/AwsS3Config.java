package com.lbadvisors.pffc.configuration;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import software.amazon.awssdk.auth.credentials.AwsBasicCredentials;
import software.amazon.awssdk.auth.credentials.StaticCredentialsProvider;
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.s3.S3Client;

@Configuration
public class AwsS3Config {

        @Autowired
        AwsProperties awsProperties;

        @Bean
        public S3Client s3Client() {

                AwsBasicCredentials awsCreds = AwsBasicCredentials.create(awsProperties.getAccessKey(),
                                awsProperties.getSecretKey());

                S3Client s3Client = S3Client.builder()
                                .region(Region.of(
                                                awsProperties.getRegion()))
                                .credentialsProvider(StaticCredentialsProvider.create(awsCreds))
                                .build();

                return s3Client;
        };
}
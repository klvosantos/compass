package com.example.compass.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import software.amazon.awssdk.core.client.config.ClientOverrideConfiguration;
import software.amazon.awssdk.services.sqs.SqsClient;
import software.amazon.awssdk.regions.Region;

import java.net.URI;

@Configuration
public class AwsSqsConfig {

    @Bean
    public SqsClient sqsClient() {
        String region = System.getenv("AWS_REGION"); // Fetch region from environment variable
        String endpoint = System.getenv("LOCALSTACK_ENDPOINT"); // Fetch LocalStack endpoint
        return SqsClient.builder()
                .region(Region.of(region != null ? region : "us-east-1")) // Default to us-east-1 if not set
                .endpointOverride(URI.create(endpoint != null ? endpoint : "http://localhost:4566")) // LocalStack endpoint
                .overrideConfiguration(ClientOverrideConfiguration.builder().build())
                .build();
    }

}

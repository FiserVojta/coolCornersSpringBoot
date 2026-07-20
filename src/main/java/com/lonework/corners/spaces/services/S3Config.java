package com.lonework.corners.spaces.services;


import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import software.amazon.awssdk.auth.credentials.AwsBasicCredentials;
import software.amazon.awssdk.auth.credentials.StaticCredentialsProvider;
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.S3Configuration;
import software.amazon.awssdk.services.s3.presigner.S3Presigner;

import java.net.URI;

@Configuration
public class S3Config {

    private static final Logger log = LoggerFactory.getLogger(S3Config.class);

    @Bean
    public S3Client s3Client(DigitalOceanSpacesProperties properties) {
        String accessKey = properties.getAccessKey();
        String secretKey = properties.getSecretKey();
        // Log a non-sensitive fingerprint so we can verify WHICH credentials the app actually loaded.
        // Never log the full secret.
        log.info("S3 client config -> endpoint={} region={} bucket={} accessKeyPrefix={} accessKeyLen={} secretKeyLen={}",
                properties.getEndpoint(),
                properties.getRegion(),
                properties.getBucket(),
                accessKey != null && accessKey.length() >= 4 ? accessKey.substring(0, 4) : "n/a",
                accessKey != null ? accessKey.length() : 0,
                secretKey != null ? secretKey.length() : 0);

        AwsBasicCredentials credentials = AwsBasicCredentials.create(accessKey, secretKey);

        return S3Client.builder()
                .region(Region.of(properties.getRegion()))
                .credentialsProvider(StaticCredentialsProvider.create(credentials))
                .endpointOverride(URI.create(properties.getEndpoint()))
                // Scaleway (and other S3-compatible stores) reject the AWS SDK v2 "aws-chunked"
                // streaming-payload PUT with a bodyless 400. Send a normal signed PUT instead.
                .serviceConfiguration(S3Configuration.builder()
                        .chunkedEncodingEnabled(false)
                        .build())
                .build();
    }

    @Bean
    public S3Presigner s3Presigner(DigitalOceanSpacesProperties properties) {
        AwsBasicCredentials credentials = AwsBasicCredentials.create(properties.getAccessKey(), properties.getSecretKey());

        return S3Presigner.builder()
                .region(Region.of(properties.getRegion()))
                .credentialsProvider(StaticCredentialsProvider.create(credentials))
                .endpointOverride(URI.create(properties.getEndpoint()))
                .serviceConfiguration(S3Configuration.builder()
                        .chunkedEncodingEnabled(false)
                        .build())
                .build();
    }
}

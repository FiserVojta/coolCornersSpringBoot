package com.lonework.corners.spaces.services;

import org.springframework.stereotype.Service;
import software.amazon.awssdk.core.sync.RequestBody;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.model.*;

import java.io.InputStream;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class DigitalOceanSpacesService {

    private final S3Client s3Client;
    private final DigitalOceanSpacesProperties properties;

    public DigitalOceanSpacesService(S3Client s3Client, DigitalOceanSpacesProperties properties) {
        this.s3Client = s3Client;
        this.properties = properties;
    }

    /**
     * Upload a file to DigitalOcean Spaces
     */
    public String uploadFile(String key, InputStream inputStream, long contentLength, String contentType) {
        PutObjectRequest putObjectRequest = PutObjectRequest.builder()
                .bucket(properties.getBucket())
                .key(key)
                .contentType(contentType)
                .build();

        try {
            s3Client.putObject(putObjectRequest, RequestBody.fromInputStream(inputStream, contentLength));
        } catch (S3Exception e) {
            String code = e.awsErrorDetails() != null ? e.awsErrorDetails().errorCode() : "n/a";
            String msg = e.awsErrorDetails() != null ? e.awsErrorDetails().errorMessage() : "n/a";
            throw new RuntimeException(
                    "S3 upload failed: bucket=" + properties.getBucket()
                            + " key=" + key
                            + " status=" + e.statusCode()
                            + " code=" + code
                            + " message=" + msg,
                    e);
        }

        return getFileUrl(key);
    }

    /**
     * Upload a file from byte array
     */
    public String uploadFile(String key, byte[] content, String contentType) {
        PutObjectRequest putObjectRequest = PutObjectRequest.builder()
                .bucket(properties.getBucket())
                .key(key)
                .contentType(contentType)
                .build();

        s3Client.putObject(putObjectRequest, RequestBody.fromBytes(content));

        return getFileUrl(key);
    }

    /**
     * Download a file from Spaces
     */
    public InputStream downloadFile(String key) {
        GetObjectRequest getObjectRequest = GetObjectRequest.builder()
                .bucket(properties.getBucket())
                .key(key)
                .build();

        return s3Client.getObject(getObjectRequest);
    }

    /**
     * Delete a file from Spaces
     */
    public void deleteFile(String key) {
        DeleteObjectRequest deleteObjectRequest = DeleteObjectRequest.builder()
                .bucket(properties.getBucket())
                .key(key)
                .build();

        s3Client.deleteObject(deleteObjectRequest);
    }

    /**
     * List all files in the bucket
     */
    public List<String> listFiles() {
        ListObjectsV2Request listRequest = ListObjectsV2Request.builder()
                .bucket(properties.getBucket())
                .build();

        ListObjectsV2Response listResponse = s3Client.listObjectsV2(listRequest);

        return listResponse.contents().stream()
                .map(S3Object::key)
                .collect(Collectors.toList());
    }

    /**
     * Check if a file exists
     */
    public boolean fileExists(String key) {
        try {
            HeadObjectRequest headObjectRequest = HeadObjectRequest.builder()
                    .bucket(properties.getBucket())
                    .key(key)
                    .build();

            s3Client.headObject(headObjectRequest);
            return true;
        } catch (NoSuchKeyException e) {
            return false;
        }
    }

    /**
     * Get the public URL of a file
     */
    public String getFileUrl(String key) {
        return String.format("%s/%s/%s",
                properties.getEndpoint(),
                properties.getBucket(),
                key);
    }

    /**
     * Generate a pre-signed URL for temporary access (optional)
     */
    public String generatePresignedUrl(String key, int expirationMinutes) {
        // Note: Pre-signed URLs require additional AWS SDK setup
        // This is a simplified version - you may want to implement proper pre-signed URLs
        return getFileUrl(key);
    }
}
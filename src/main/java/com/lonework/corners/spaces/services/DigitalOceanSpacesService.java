package com.lonework.corners.spaces.services;

import com.lonework.corners.spaces.api.PresignedUpload;
import org.springframework.stereotype.Service;
import software.amazon.awssdk.core.sync.RequestBody;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.model.*;
import software.amazon.awssdk.services.s3.presigner.S3Presigner;
import software.amazon.awssdk.services.s3.presigner.model.PutObjectPresignRequest;

import java.io.InputStream;
import java.time.Duration;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
public class DigitalOceanSpacesService {

    /** How long a presigned browser upload URL stays valid. */
    private static final Duration PRESIGN_EXPIRY = Duration.ofMinutes(15);

    private final S3Client s3Client;
    private final S3Presigner s3Presigner;
    private final DigitalOceanSpacesProperties properties;

    public DigitalOceanSpacesService(S3Client s3Client, S3Presigner s3Presigner, DigitalOceanSpacesProperties properties) {
        this.s3Client = s3Client;
        this.s3Presigner = s3Presigner;
        this.properties = properties;
    }

    /**
     * Upload a file to DigitalOcean Spaces
     */
    public String uploadFile(String key, InputStream inputStream, long contentLength, String contentType) {
        PutObjectRequest.Builder builder = PutObjectRequest.builder()
                .bucket(properties.getBucket())
                .key(key)
                .contentType(contentType);
        if (properties.isPublicReadAcl()) {
            builder.acl(ObjectCannedACL.PUBLIC_READ); // Make publicly accessible; disabled for already-public buckets
        }
        PutObjectRequest putObjectRequest = builder.build();

        try {
            s3Client.putObject(putObjectRequest, RequestBody.fromInputStream(inputStream, contentLength));
        } catch (S3Exception e) {
            String code = e.awsErrorDetails() != null ? e.awsErrorDetails().errorCode() : "n/a";
            String msg = e.awsErrorDetails() != null ? e.awsErrorDetails().errorMessage() : "n/a";
            String raw = e.awsErrorDetails() != null && e.awsErrorDetails().rawResponse() != null
                    ? e.awsErrorDetails().rawResponse().asUtf8String()
                    : "n/a";
            throw new RuntimeException(
                    "S3 upload failed: bucket=" + properties.getBucket()
                            + " endpoint=" + properties.getEndpoint()
                            + " region=" + properties.getRegion()
                            + " key=" + key
                            + " status=" + e.statusCode()
                            + " code=" + code
                            + " message=" + msg
                            + " raw=" + raw,
                    e);
        }

        return getFileUrl(key);
    }

    /**
     * Upload a file from byte array
     */
    public String uploadFile(String key, byte[] content, String contentType) {
        PutObjectRequest.Builder builder = PutObjectRequest.builder()
                .bucket(properties.getBucket())
                .key(key)
                .contentType(contentType);
        if (properties.isPublicReadAcl()) {
            builder.acl(ObjectCannedACL.PUBLIC_READ);
        }
        PutObjectRequest putObjectRequest = builder.build();

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
     * Presign a PUT so the browser can upload straight to object storage, skipping the
     * backend hop entirely. The returned headers are part of the signature and must be
     * sent verbatim with the PUT.
     */
    public PresignedUpload presignUpload(String key, String contentType) {
        PutObjectRequest.Builder builder = PutObjectRequest.builder()
                .bucket(properties.getBucket())
                .key(key)
                .contentType(contentType);
        Map<String, String> headers = new LinkedHashMap<>();
        headers.put("Content-Type", contentType);
        if (properties.isPublicReadAcl()) {
            builder.acl(ObjectCannedACL.PUBLIC_READ);
            headers.put("x-amz-acl", "public-read");
        }

        PutObjectPresignRequest presignRequest = PutObjectPresignRequest.builder()
                .signatureDuration(PRESIGN_EXPIRY)
                .putObjectRequest(builder.build())
                .build();

        String url = s3Presigner.presignPutObject(presignRequest).url().toString();
        return new PresignedUpload(url, headers);
    }
}
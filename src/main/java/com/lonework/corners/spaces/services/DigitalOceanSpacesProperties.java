package com.lonework.corners.spaces.services;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

@Configuration
@ConfigurationProperties(prefix = "digitalocean.spaces")
public class DigitalOceanSpacesProperties {
    private String accessKey;
    private String secretKey;
    private String region;
    private String bucket;
    private String endpoint;
    /**
     * Whether to send the {@code public-read} canned ACL on uploads.
     * DigitalOcean Spaces relies on this; Scaleway public buckets do not need it and may reject it.
     * Defaults to {@code true} to preserve existing (DO/prod) behaviour.
     */
    private boolean publicReadAcl = true;

    // Getters and setters
    public String getAccessKey() {
        return accessKey;
    }

    public void setAccessKey(String accessKey) {
        this.accessKey = accessKey;
    }

    public String getSecretKey() {
        return secretKey;
    }

    public void setSecretKey(String secretKey) {
        this.secretKey = secretKey;
    }

    public String getRegion() {
        return region;
    }

    public void setRegion(String region) {
        this.region = region;
    }

    public String getBucket() {
        return bucket;
    }

    public void setBucket(String bucket) {
        this.bucket = bucket;
    }

    public String getEndpoint() {
        return endpoint;
    }

    public void setEndpoint(String endpoint) {
        this.endpoint = endpoint;
    }

    public boolean isPublicReadAcl() {
        return publicReadAcl;
    }

    public void setPublicReadAcl(boolean publicReadAcl) {
        this.publicReadAcl = publicReadAcl;
    }
}
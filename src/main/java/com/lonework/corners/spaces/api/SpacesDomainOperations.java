package com.lonework.corners.spaces.api;

import com.lonework.corners.spaces.services.DigitalOceanSpacesService;
import org.springframework.stereotype.Component;

import java.io.InputStream;

@Component
public class SpacesDomainOperations implements SpacesOperations {

    private final DigitalOceanSpacesService spacesService;

    public SpacesDomainOperations(DigitalOceanSpacesService spacesService) {
        this.spacesService = spacesService;
    }

    @Override
    public String uploadFile(String key, InputStream inputStream, long contentLength, String contentType) {
        return spacesService.uploadFile(key, inputStream, contentLength, contentType);
    }

    @Override
    public String uploadFile(String key, byte[] content, String contentType) {
        return spacesService.uploadFile(key, content, contentType);
    }

    @Override
    public InputStream downloadFile(String key) {
        return spacesService.downloadFile(key);
    }
}

package com.lonework.corners.spaces.controller;

import com.lonework.corners.spaces.services.DigitalOceanSpacesService;
import jakarta.inject.Inject;
import org.springframework.stereotype.Service;
import org.springframework.web.context.annotation.ApplicationScope;

import java.io.InputStream;


@ApplicationScope
@Service
public class SpacesFacade {


    @Inject
    DigitalOceanSpacesService spacesService;

    public String uploadFile(String key, InputStream inputStream, long contentLength, String contentType) {
        return spacesService.uploadFile(
                key,
                inputStream,
                contentLength,
                contentType);
    }

    public InputStream downloadFile(String key){
        return spacesService.downloadFile(key);
    }
}

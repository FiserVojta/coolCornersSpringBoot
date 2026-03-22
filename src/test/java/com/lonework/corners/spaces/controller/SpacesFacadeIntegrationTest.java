package com.lonework.corners.spaces.controller;

import com.lonework.corners.spaces.services.DigitalOceanSpacesService;
import com.lonework.corners.support.FacadeIntegrationTestSupport;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.mock.mockito.MockBean;

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;


class SpacesFacadeIntegrationTest extends FacadeIntegrationTestSupport {

    @Autowired
    private SpacesFacade spacesFacade;

    @MockBean
    private DigitalOceanSpacesService digitalOceanSpacesService;

    @Test
    void uploadFileDelegatesToStorageService() {
        InputStream inputStream = new ByteArrayInputStream("payload".getBytes(StandardCharsets.UTF_8));
        when(digitalOceanSpacesService.uploadFile(eq("facade-key"), any(InputStream.class), eq(7L), eq("text/plain")))
                .thenReturn("https://spaces.example/facade-key");

        String fileUrl = spacesFacade.uploadFile("facade-key", inputStream, 7L, "text/plain");

        assertEquals("https://spaces.example/facade-key", fileUrl);
    }

    @Test
    void downloadFileDelegatesToStorageService() {
        InputStream inputStream = new ByteArrayInputStream("payload".getBytes(StandardCharsets.UTF_8));
        when(digitalOceanSpacesService.downloadFile("facade-key")).thenReturn(inputStream);

        InputStream downloadedFile = spacesFacade.downloadFile("facade-key");

        assertSame(inputStream, downloadedFile);
    }
}

package com.lonework.corners.files.controller;

import com.lonework.corners.common.model.EntityStatus;
import com.lonework.corners.files.model.CornerFile;
import com.lonework.corners.spaces.services.DigitalOceanSpacesService;
import com.lonework.corners.support.FacadeIntegrationTestSupport;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.mock.web.MockMultipartFile;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.when;


class FileFacadeIntegrationTest extends FacadeIntegrationTestSupport {

    @Autowired
    private FileFacade fileFacade;

    @MockBean
    private DigitalOceanSpacesService digitalOceanSpacesService;

    @Test
    void uploadFilePersistsMetadataAndReturnsStoredFile() {
        when(digitalOceanSpacesService.uploadFile(anyString(), any(InputStream.class), anyLong(), anyString()))
                .thenReturn("https://spaces.example/uploaded-file");
        MockMultipartFile file = new MockMultipartFile(
                "file",
                "photo.jpg",
                "image/jpeg",
                "image-content".getBytes(StandardCharsets.UTF_8)
        );

        CornerFile uploadedFile = fileFacade.uploadFile(file, "integration@example.com");
        flushAndClear();

        CornerFile persistedFile = entityManager.find(CornerFile.class, uploadedFile.getId());

        assertNotNull(persistedFile);
        assertEquals("integration@example.com", persistedFile.getCreatedBy());
        assertEquals(EntityStatus.ACTIVE, persistedFile.getEntityStatus());
        assertEquals("https://spaces.example/uploaded-file", persistedFile.getUrl());
        assertEquals(true, persistedFile.getName().endsWith("-photo.jpg"));
        // Non-decodable bytes: upload still succeeds, just without a thumbnail.
        assertNull(persistedFile.getThumbnailUrl());
        assertNull(persistedFile.getThumbnailName());
    }

    @Test
    void uploadRealImageGeneratesAndStoresThumbnail() throws IOException {
        when(digitalOceanSpacesService.uploadFile(anyString(), any(InputStream.class), anyLong(), anyString()))
                .thenReturn("https://spaces.example/original.png");
        when(digitalOceanSpacesService.uploadFile(anyString(), any(byte[].class), anyString()))
                .thenReturn("https://spaces.example/thumbnail.jpg");
        MockMultipartFile file = new MockMultipartFile(
                "file",
                "photo.png",
                "image/png",
                pngBytes(800, 600)
        );

        CornerFile uploadedFile = fileFacade.uploadFile(file, "integration@example.com");
        flushAndClear();

        CornerFile persistedFile = entityManager.find(CornerFile.class, uploadedFile.getId());

        assertNotNull(persistedFile);
        assertEquals("https://spaces.example/original.png", persistedFile.getUrl());
        assertEquals("https://spaces.example/thumbnail.jpg", persistedFile.getThumbnailUrl());
        assertNotNull(persistedFile.getThumbnailName());
        assertTrue(persistedFile.getThumbnailName().startsWith("thumb-"));
    }

    private static byte[] pngBytes(int width, int height) throws IOException {
        BufferedImage image = new BufferedImage(width, height, BufferedImage.TYPE_INT_RGB);
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        ImageIO.write(image, "png", out);
        return out.toByteArray();
    }

    @Test
    void getFileUsesStoredKeyForDownload() {
        CornerFile file = createCornerFile("stored-key", "integration@example.com");
        InputStream stream = new ByteArrayInputStream("download".getBytes(StandardCharsets.UTF_8));
        when(digitalOceanSpacesService.downloadFile("stored-key")).thenReturn(stream);
        flushAndClear();

        InputStream downloadedFile = fileFacade.getFile(file.getId());

        assertSame(stream, downloadedFile);
    }

    @Test
    void getFileMetadataReturnsPersistedEntity() {
        CornerFile file = createCornerFile("metadata-key", "integration@example.com");
        flushAndClear();

        CornerFile metadata = fileFacade.getFileMetadata(file.getId());

        assertNotNull(metadata);
        assertEquals(file.getId(), metadata.getId());
        assertEquals("metadata-key", metadata.getName());
    }
}

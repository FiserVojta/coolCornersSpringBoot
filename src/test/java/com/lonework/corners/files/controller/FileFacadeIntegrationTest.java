package com.lonework.corners.files.controller;

import com.lonework.corners.common.model.EntityStatus;
import com.lonework.corners.files.model.CornerFile;
import com.lonework.corners.files.model.DTO.CornerFileCompleteRequest;
import com.lonework.corners.files.model.DTO.CornerFilePresignRequest;
import com.lonework.corners.files.model.DTO.CornerFilePresignResponse;
import com.lonework.corners.spaces.api.PresignedUpload;
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
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertThrows;
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
    void presignUploadGeneratesKeyAndUrlsWithThumbnailForImages() {
        stubPresignAndPublicUrls();

        CornerFilePresignResponse response =
                fileFacade.presignUpload(new CornerFilePresignRequest("photo.png", "image/png", true));

        assertTrue(response.key().endsWith("-photo.png"));
        assertEquals("https://signed.example/" + response.key(), response.uploadUrl());
        assertEquals("https://spaces.example/" + response.key(), response.publicUrl());
        assertEquals("image/png", response.headers().get("Content-Type"));
        assertEquals("thumb-" + response.key(), response.thumbnailKey());
        assertEquals("https://signed.example/thumb-" + response.key(), response.thumbnailUploadUrl());
        assertEquals("https://spaces.example/thumb-" + response.key(), response.thumbnailPublicUrl());
        // Client-side thumbnails are always re-encoded as JPEG, whatever the original format.
        assertEquals("image/jpeg", response.thumbnailHeaders().get("Content-Type"));
    }

    @Test
    void presignUploadSkipsThumbnailForNonImages() {
        stubPresignAndPublicUrls();

        CornerFilePresignResponse response =
                fileFacade.presignUpload(new CornerFilePresignRequest("doc.pdf", "application/pdf", true));

        assertNotNull(response.uploadUrl());
        assertNull(response.thumbnailKey());
        assertNull(response.thumbnailUploadUrl());
        assertNull(response.thumbnailPublicUrl());
        assertNull(response.thumbnailHeaders());
    }

    @Test
    void presignUploadStripsPathFromFileName() {
        stubPresignAndPublicUrls();

        CornerFilePresignResponse response =
                fileFacade.presignUpload(new CornerFilePresignRequest("../nested/dir\\evil.jpg", "image/jpeg", false));

        assertTrue(response.key().endsWith("-evil.jpg"));
        assertFalse(response.key().contains("/"));
        assertFalse(response.key().contains("\\"));
    }

    @Test
    void completeUploadPersistsFileWithThumbnail() {
        stubPresignAndPublicUrls();
        when(digitalOceanSpacesService.fileExists("abc-photo.jpg")).thenReturn(true);
        when(digitalOceanSpacesService.fileExists("thumb-abc-photo.jpg")).thenReturn(true);

        CornerFile file = fileFacade.completeUpload(
                new CornerFileCompleteRequest("abc-photo.jpg", "thumb-abc-photo.jpg"),
                "integration@example.com");
        flushAndClear();

        CornerFile persisted = entityManager.find(CornerFile.class, file.getId());
        assertNotNull(persisted);
        assertEquals("abc-photo.jpg", persisted.getName());
        assertEquals("https://spaces.example/abc-photo.jpg", persisted.getUrl());
        assertEquals("thumb-abc-photo.jpg", persisted.getThumbnailName());
        assertEquals("https://spaces.example/thumb-abc-photo.jpg", persisted.getThumbnailUrl());
        assertEquals("integration@example.com", persisted.getCreatedBy());
        assertEquals(EntityStatus.ACTIVE, persisted.getEntityStatus());
        assertNotNull(persisted.getCreatedAt());
    }

    @Test
    void completeUploadWithoutThumbnailPersistsPlainFile() {
        stubPresignAndPublicUrls();
        when(digitalOceanSpacesService.fileExists("abc-doc.pdf")).thenReturn(true);

        CornerFile file = fileFacade.completeUpload(
                new CornerFileCompleteRequest("abc-doc.pdf", null),
                "integration@example.com");
        flushAndClear();

        CornerFile persisted = entityManager.find(CornerFile.class, file.getId());
        assertNotNull(persisted);
        assertEquals("https://spaces.example/abc-doc.pdf", persisted.getUrl());
        assertNull(persisted.getThumbnailName());
        assertNull(persisted.getThumbnailUrl());
    }

    @Test
    void completeUploadDropsThumbnailWhenThumbnailObjectMissing() {
        stubPresignAndPublicUrls();
        when(digitalOceanSpacesService.fileExists("abc-photo.jpg")).thenReturn(true);
        when(digitalOceanSpacesService.fileExists("thumb-abc-photo.jpg")).thenReturn(false);

        CornerFile file = fileFacade.completeUpload(
                new CornerFileCompleteRequest("abc-photo.jpg", "thumb-abc-photo.jpg"),
                "integration@example.com");
        flushAndClear();

        CornerFile persisted = entityManager.find(CornerFile.class, file.getId());
        assertNotNull(persisted);
        assertEquals("https://spaces.example/abc-photo.jpg", persisted.getUrl());
        assertNull(persisted.getThumbnailName());
        assertNull(persisted.getThumbnailUrl());
    }

    @Test
    void completeUploadRejectsWhenObjectWasNeverUploaded() {
        when(digitalOceanSpacesService.fileExists("missing-key")).thenReturn(false);

        assertThrows(IllegalArgumentException.class, () -> fileFacade.completeUpload(
                new CornerFileCompleteRequest("missing-key", null),
                "integration@example.com"));
    }

    @Test
    void completeUploadRejectsBlankKey() {
        assertThrows(IllegalArgumentException.class, () -> fileFacade.completeUpload(
                new CornerFileCompleteRequest("  ", null),
                "integration@example.com"));
    }

    private void stubPresignAndPublicUrls() {
        when(digitalOceanSpacesService.presignUpload(anyString(), anyString()))
                .thenAnswer(inv -> new PresignedUpload(
                        "https://signed.example/" + inv.getArgument(0, String.class),
                        Map.of("Content-Type", inv.getArgument(1, String.class))));
        when(digitalOceanSpacesService.getFileUrl(anyString()))
                .thenAnswer(inv -> "https://spaces.example/" + inv.getArgument(0, String.class));
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

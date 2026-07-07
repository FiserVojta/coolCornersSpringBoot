package com.lonework.corners.files.controller;

import com.lonework.corners.common.model.EntityStatus;
import com.lonework.corners.common.model.PagedResult;
import com.lonework.corners.files.model.CornerFile;
import com.lonework.corners.files.model.DTO.CornerFileList;
import com.lonework.corners.spaces.api.SpacesOperations;
import jakarta.inject.Inject;
import jakarta.persistence.EntityManager;
import jakarta.transaction.Transactional;
import net.coobird.thumbnailator.Thumbnails;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.web.context.annotation.ApplicationScope;
import org.springframework.web.multipart.MultipartFile;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.time.ZonedDateTime;
import java.util.List;
import java.util.UUID;


@ApplicationScope
@Service
public class FileFacade {

    private static final Logger log = LoggerFactory.getLogger(FileFacade.class);

    /** Longest-side pixel bound for gallery thumbnails; aspect ratio is preserved. */
    private static final int THUMBNAIL_MAX_DIMENSION = 400;
    private static final double THUMBNAIL_QUALITY = 0.8;

    @Inject
    EntityManager entityManager;

    @Inject
    SpacesOperations spacesOperations;

    @Transactional
    public CornerFile uploadFile(MultipartFile file, String createdBy) {
        try {
            byte[] bytes = file.getBytes();
            String contentType = file.getContentType();
            String fileName = UUID.randomUUID() + "-" + file.getOriginalFilename();
            String fileUrl = spacesOperations.uploadFile(
                    fileName,
                    new ByteArrayInputStream(bytes),
                    bytes.length,
                    contentType
            );
            var cornerFile = new CornerFile();
            cornerFile.setCreatedAt(ZonedDateTime.now());
            cornerFile.setCreatedBy(createdBy);
            cornerFile.setName(fileName);
            cornerFile.setUrl(fileUrl);
            cornerFile.setEntityStatus(EntityStatus.ACTIVE);

            if (contentType != null && contentType.startsWith("image/")) {
                byte[] thumbnail = generateThumbnail(bytes);
                if (thumbnail != null) {
                    String thumbnailKey = "thumb-" + fileName;
                    String thumbnailUrl = spacesOperations.uploadFile(thumbnailKey, thumbnail, "image/jpeg");
                    cornerFile.setThumbnailName(thumbnailKey);
                    cornerFile.setThumbnailUrl(thumbnailUrl);
                }
            }

            entityManager.persist(cornerFile);
            return cornerFile;
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * Produce a JPEG thumbnail bounded by {@link #THUMBNAIL_MAX_DIMENSION} on its longest side.
     * Returns {@code null} (and never throws) when the bytes are not a decodable image, so a bad
     * or unsupported upload degrades to "no thumbnail" instead of failing the whole upload.
     */
    private byte[] generateThumbnail(byte[] source) {
        try {
            var out = new ByteArrayOutputStream();
            Thumbnails.of(new ByteArrayInputStream(source))
                    .size(THUMBNAIL_MAX_DIMENSION, THUMBNAIL_MAX_DIMENSION)
                    .outputFormat("jpg")
                    .outputQuality(THUMBNAIL_QUALITY)
                    .toOutputStream(out);
            return out.toByteArray();
        } catch (Exception e) {
            log.warn("Thumbnail generation failed; storing file without a thumbnail: {}", e.getMessage());
            return null;
        }
    }


    public InputStream getFile(Long fileId) {
        var url = entityManager.find(CornerFile.class, fileId).getName();
        return spacesOperations.downloadFile(url);
    }

    public CornerFile getFileMetadata(Long fileId) {
        return entityManager.find(CornerFile.class, fileId);
    }

    public PagedResult<CornerFileList> getFiles() {
        var files = entityManager.createQuery("select c.id, c.url, c.thumbnailUrl, c.name from CornerFile c", CornerFileList.class).getResultList();
        return new PagedResult<>(files, 1L);
    }

    public List<CornerFileList> getCornerFilesList(List<CornerFile> files) {
        return files.stream().map(CornerFileList::new).toList();
    }
}

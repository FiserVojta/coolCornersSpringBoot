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
import net.coobird.thumbnailator.filters.ImageFilter;
import net.coobird.thumbnailator.util.exif.ExifFilterUtils;
import net.coobird.thumbnailator.util.exif.ExifUtils;
import net.coobird.thumbnailator.util.exif.Orientation;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.web.context.annotation.ApplicationScope;
import org.springframework.web.multipart.MultipartFile;

import javax.imageio.ImageIO;
import javax.imageio.ImageReadParam;
import javax.imageio.ImageReader;
import javax.imageio.stream.ImageInputStream;
import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.time.ZonedDateTime;
import java.util.Iterator;
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
            String contentType = file.getContentType();
            String fileName = UUID.randomUUID() + "-" + file.getOriginalFilename();
            String fileUrl;
            // Stream straight to object storage — multipart uploads are disk-backed, so the
            // file never has to fit on the heap.
            try (InputStream in = file.getInputStream()) {
                fileUrl = spacesOperations.uploadFile(fileName, in, file.getSize(), contentType);
            }
            var cornerFile = new CornerFile();
            cornerFile.setCreatedAt(ZonedDateTime.now());
            cornerFile.setCreatedBy(createdBy);
            cornerFile.setName(fileName);
            cornerFile.setUrl(fileUrl);
            cornerFile.setEntityStatus(EntityStatus.ACTIVE);

            if (contentType != null && contentType.startsWith("image/")) {
                byte[] thumbnail = generateThumbnail(file);
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
     * The source is decoded with subsampling so the raster held on the heap stays close to the
     * thumbnail size instead of the image's full resolution (a large photo can otherwise decode
     * to hundreds of MB). Returns {@code null} (and never throws) when the bytes are not a
     * decodable image, so a bad or unsupported upload degrades to "no thumbnail" instead of
     * failing the whole upload.
     */
    private byte[] generateThumbnail(MultipartFile file) {
        try (InputStream in = file.getInputStream();
             ImageInputStream imageIn = ImageIO.createImageInputStream(in)) {
            if (imageIn == null) {
                return null;
            }
            Iterator<ImageReader> readers = ImageIO.getImageReaders(imageIn);
            if (!readers.hasNext()) {
                return null;
            }
            ImageReader reader = readers.next();
            try {
                reader.setInput(imageIn);
                ImageFilter orientationFilter = exifOrientationFilter(reader);
                BufferedImage source = readSubsampled(reader);
                var thumbnailBuilder = Thumbnails.of(source)
                        .size(THUMBNAIL_MAX_DIMENSION, THUMBNAIL_MAX_DIMENSION)
                        .outputFormat("jpg")
                        .outputQuality(THUMBNAIL_QUALITY);
                if (orientationFilter != null) {
                    thumbnailBuilder.addFilter(orientationFilter);
                }
                var out = new ByteArrayOutputStream();
                thumbnailBuilder.toOutputStream(out);
                return out.toByteArray();
            } finally {
                reader.dispose();
            }
        } catch (Exception e) {
            log.warn("Thumbnail generation failed; storing file without a thumbnail: {}", e.getMessage());
            return null;
        }
    }

    /**
     * Decode at a reduced resolution: keep at least 2x the thumbnail bound per side so the
     * final resize still has enough pixels to average over.
     */
    private BufferedImage readSubsampled(ImageReader reader) throws IOException {
        int longestSide = Math.max(reader.getWidth(0), reader.getHeight(0));
        int sampling = Math.max(1, longestSide / (2 * THUMBNAIL_MAX_DIMENSION));
        ImageReadParam param = reader.getDefaultReadParam();
        param.setSourceSubsampling(sampling, sampling, 0, 0);
        return reader.read(0, param);
    }

    /**
     * Subsampled decode bypasses Thumbnailator's automatic EXIF handling, so the rotation
     * filter has to be applied explicitly. Images without EXIF orientation get no filter.
     */
    private ImageFilter exifOrientationFilter(ImageReader reader) {
        try {
            Orientation orientation = ExifUtils.getExifOrientation(reader, 0);
            if (orientation == null || orientation == Orientation.TOP_LEFT) {
                return null;
            }
            return ExifFilterUtils.getFilterForOrientation(orientation);
        } catch (Exception e) {
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

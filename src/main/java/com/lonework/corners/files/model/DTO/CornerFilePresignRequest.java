package com.lonework.corners.files.model.DTO;

public record CornerFilePresignRequest(
        String fileName,
        String contentType,
        boolean withThumbnail
) {
}

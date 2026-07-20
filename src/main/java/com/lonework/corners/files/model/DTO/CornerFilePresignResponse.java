package com.lonework.corners.files.model.DTO;

import java.util.Map;

/**
 * Everything the browser needs to upload directly to object storage: presigned PUT URLs
 * (headers are part of the signature and must be sent verbatim) plus the final public URLs.
 * Thumbnail fields are null when no thumbnail upload was requested or the type is not an image.
 */
public record CornerFilePresignResponse(
        String key,
        String uploadUrl,
        String publicUrl,
        Map<String, String> headers,
        String thumbnailKey,
        String thumbnailUploadUrl,
        String thumbnailPublicUrl,
        Map<String, String> thumbnailHeaders
) {
}

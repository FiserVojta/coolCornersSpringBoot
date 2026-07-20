package com.lonework.corners.spaces.api;

import java.util.Map;

/**
 * A presigned PUT the browser can perform directly against object storage.
 * {@code headers} must be sent verbatim with the PUT — they are part of the signature.
 */
public record PresignedUpload(
        String url,
        Map<String, String> headers
) {
}

package com.lonework.corners.tag.model;

import java.time.ZonedDateTime;

/**
 * Tag row for the admin moderation listing. {@code usageCount} is the number of places, trips,
 * wanders and travels the tag is attached to.
 */
public record TagAdminResponse(
        Long id,
        String name,
        String normalizedName,
        String creator,
        TagStatus status,
        ZonedDateTime createdAt,
        long usageCount
) {
}

package com.lonework.corners.tag.model;

/**
 * Moderation status of a tag. HIDDEN tags stay attached where they already are, but are excluded
 * from listings and suggestions and cannot be newly assigned or re-created.
 */
public enum TagStatus {
    ACTIVE,
    HIDDEN
}

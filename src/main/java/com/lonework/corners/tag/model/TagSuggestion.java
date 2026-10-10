package com.lonework.corners.tag.model;

/**
 * Lightweight tag search result used for autocomplete. {@code usageCount} is the number of
 * places, trips, wanders and travels the tag is attached to.
 */
public record TagSuggestion(
        Long id,
        String name,
        String normalizedName,
        long usageCount
) {
}

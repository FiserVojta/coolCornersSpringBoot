package com.lonework.corners.tag.model;

import java.util.List;

public record TagSearchRequest(
        List<Long> placeId,
        List<Long> tripId
) {
}

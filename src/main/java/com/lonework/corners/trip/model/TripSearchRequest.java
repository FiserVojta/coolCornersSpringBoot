package com.lonework.corners.trip.model;

import java.util.List;

public record TripSearchRequest(
        List<Long> categories,
        List<Long> tags,
        String createdBy,
        Double minRating,
        String orderBy,
        String order
) {
    public TripSearchRequest(List<Long> categories, List<Long> tags, String createdBy, Double minRating) {
        this(categories, tags, createdBy, minRating, null, null);
    }
}

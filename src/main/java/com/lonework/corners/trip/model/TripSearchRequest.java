package com.lonework.corners.trip.model;

import com.lonework.corners.common.model.QueryOrder;

import java.util.List;

public record TripSearchRequest(
        List<Long> categories,
        List<Long> tags,
        String createdBy,
        Double minRating,
        TripSort sortBy,
        QueryOrder sortDir
) {
    public TripSearchRequest(List<Long> categories, List<Long> tags, String createdBy, Double minRating) {
        this(categories, tags, createdBy, minRating, null, null);
    }
}

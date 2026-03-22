package com.lonework.corners.trip.model;

import java.util.List;

public record TripSearchRequest(
        List<Long> categories,
        List<Long> tags,
        String createdBy
) {
}

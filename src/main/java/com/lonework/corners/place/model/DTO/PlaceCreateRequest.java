package com.lonework.corners.place.model.DTO;

import org.locationtech.jts.geom.Geometry;

import java.util.List;

public record PlaceCreateRequest(
        String name,
        String description,
        Double rating,
        String phoneNumber,
        Double price,
        String openingHours,
        String image,
        String gallery,
        Long categoryId,
        Geometry geometry,
        List<Long> tags
) {
    public PlaceCreateRequest {
        if (rating == null) {
            rating = 0.0;
        }
    }
}

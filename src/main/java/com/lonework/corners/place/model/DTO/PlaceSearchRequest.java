package com.lonework.corners.place.model.DTO;

import java.util.List;

public record PlaceSearchRequest(
        Double rating,
        List<Long> tags,
        List<Long> category,
        List<Long> cities
) {
}

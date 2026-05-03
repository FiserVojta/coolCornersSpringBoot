package com.lonework.corners.place.model.DTO;

import org.springframework.lang.Nullable;

import java.util.List;

public record PlaceListRequest(
        @Nullable List<Long> placeIds
) {
}

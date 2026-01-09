package com.lonework.corners.place.model.DTO;

import org.locationtech.jts.geom.Geometry;


public record GooglePlaceCreateRequest(
        String placeId,
        String name,
        Geometry geometry
) {
}

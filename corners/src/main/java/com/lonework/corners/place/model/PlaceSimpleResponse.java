package com.lonework.corners.place.model;

import org.geotools.api.feature.simple.SimpleFeature;


public record PlaceSimpleResponse(
        Long id,
        String name,
        String image,
        Double rating,
        SimpleFeature feature
) {
}

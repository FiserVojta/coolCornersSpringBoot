package com.lonework.corners.model.response;

import org.geotools.api.feature.simple.SimpleFeature;


public record PlaceSimpleResponse(
        Long id,
        String name,
        String image,
        Double rating,
        SimpleFeature feature
) {
}

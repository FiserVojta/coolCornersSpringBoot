package com.lonework.corners.place.model;

import com.lonework.corners.comment.model.Comment;
import org.geotools.api.feature.simple.SimpleFeature;

import java.util.List;


public record PlaceDetailResponse(
        Long id,
        String name,
        String description,
        String phoneNumber,
        String website,
        Double rating,
        SimpleFeature feature,
        String openingHours,
        List<Comment> comments
) {

    public PlaceDetailResponse(Place place, SimpleFeature feature) {
        this(
                place.getId(),
                place.getName(),
                place.getDescription(),
                place.getPhoneNumber(),
                null,
                null,
                feature,
                place.getOpeningHours(),
                place.getComments()
        );
    }
}

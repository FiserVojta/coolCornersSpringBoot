package com.lonework.corners.trip.model;

import com.lonework.corners.comment.model.Comment;
import com.lonework.corners.place.model.PlaceSimpleResponse;
import org.geotools.api.feature.simple.SimpleFeature;

import java.util.List;


public record TripDetailResponse(Long id,
                                 String name,
                                 String description,
                                 String creator,
                                 Integer duration,
                                 Double rating,
                                 SimpleFeature feature,
                                 List<PlaceSimpleResponse> places,
                                 List<Comment> comments) {
    public TripDetailResponse(Trip trip, List<PlaceSimpleResponse> places, SimpleFeature feature) {
        this(
                trip.getId(),
                trip.getName(),
                trip.getDescription(),
                trip.getCreator(),
                trip.getDuration(),
                trip.getRating(),
                feature,
                places,
                null
        );
    }

}

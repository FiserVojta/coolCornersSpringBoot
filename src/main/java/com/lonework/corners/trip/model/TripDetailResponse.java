package com.lonework.corners.trip.model;

import com.lonework.corners.comment.model.Comment;
import com.lonework.corners.place.model.GooglePlace;
import com.lonework.corners.place.model.PlaceSimpleResponse;
import com.lonework.corners.tag.model.Tag;
import org.geotools.api.feature.simple.SimpleFeature;

import java.util.ArrayList;
import java.util.List;


public record TripDetailResponse(Long id,
                                 String name,
                                 String description,
                                 String creator,
                                 Integer duration,
                                 Double rating,
                                 SimpleFeature feature,
                                 List<PlaceSimpleResponse> places,
                                 List<Comment> comments,
                                 List<Tag> tags,
                                 List<GooglePlace> googlePlaces,
                                 String createdBy) {
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
                trip.getComments(),
                trip.getTags(),
                trip.getGooglePlaces(),
                trip.getCreatedBy()
        );
    }

}

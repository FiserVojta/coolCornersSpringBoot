package com.lonework.corners.trip.model.DTO;

import com.lonework.corners.category.model.Category;
import com.lonework.corners.comment.model.Comment;
import com.lonework.corners.files.model.DTO.CornerFileList;
import com.lonework.corners.place.model.GooglePlace;
import com.lonework.corners.place.model.PlaceSimpleResponse;
import com.lonework.corners.tag.model.Tag;
import com.lonework.corners.trip.model.Trip;
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
                                 List<Comment> comments,
                                 List<Tag> tags,
                                 List<GooglePlace> googlePlaces,
                                 String createdBy,
                                 Category category,
                                 List<CornerFileList> files) {
    public TripDetailResponse(Trip trip, List<PlaceSimpleResponse> places, SimpleFeature feature, List<CornerFileList> files) {
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
                trip.getCreatedBy(),
                trip.getCategory(),
                files
        );
    }

}

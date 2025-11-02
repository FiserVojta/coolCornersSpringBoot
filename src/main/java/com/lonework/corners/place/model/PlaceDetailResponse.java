package com.lonework.corners.place.model;

import com.lonework.corners.category.model.Category;
import com.lonework.corners.comment.model.Comment;
import com.lonework.corners.tag.model.Tag;
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
        List<Tag> tags,
        List<Comment> comments,
        Category category,
        String createdBy
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
                place.getTags(),
                place.getComments(),
                place.getCategory(),
                place.getCreatedBy()
        );
    }
}

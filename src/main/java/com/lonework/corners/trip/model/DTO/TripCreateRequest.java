package com.lonework.corners.trip.model.DTO;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.lonework.corners.files.model.DTO.CornerFileCreateLinkRequest;
import com.lonework.corners.place.model.DTO.GooglePlaceCreateRequest;
import org.locationtech.jts.geom.Geometry;

import java.util.List;

public record TripCreateRequest(
        List<Long> placeIds,
        Long categoryId,
        List<Long> tags,
        String name,
        String author,
        String description,
        Integer duration,
        @JsonProperty("Type") String type,
        Geometry geometry,
        List<CornerFileCreateLinkRequest> files,
        List<GooglePlaceCreateRequest> googlePlaces
) {
}

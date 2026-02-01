package com.lonework.corners.trip.model.DTO;

import com.lonework.corners.files.model.DTO.CornerFileCreateLinkRequest;
import com.lonework.corners.place.model.DTO.GooglePlaceCreateRequest;

import java.util.List;


public record TripUpdateRequest(List<Long> placeIds,
                                Long categoryId,
                                String description,
                                List<Long> tags,
                                List<GooglePlaceCreateRequest> googlePlaces,
                                List<CornerFileCreateLinkRequest> files,
                                CornerFileCreateLinkRequest backgroundImage) {
}

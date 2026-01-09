package com.lonework.corners.trip.model;

import java.util.List;


public record TripUpdateRequest(List<Long> placeIds,
                                Long categoryId,
                                String description) {
}

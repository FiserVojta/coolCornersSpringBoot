package com.lonework.corners.trip.api;

import com.lonework.corners.trip.model.Trip;

import java.util.List;

public interface TripOperations {

    List<Trip> findTripsByIds(List<Long> ids);
}

package com.lonework.corners.model.request;

import java.util.List;

public class TagSearchRequest {

    private List<Long> placeId;
    private List<Long> tripId;

    public TagSearchRequest() {
    }

    public List<Long> getPlaceId() {
        return placeId;
    }

    public void setPlaceId(List<Long> placeId) {
        this.placeId = placeId;
    }

    public List<Long> getTripId() {
        return tripId;
    }

    public void setTripId(List<Long> tripId) {
        this.tripId = tripId;
    }

}

package com.lonework.corners.model.request;

import java.util.List;

import org.springframework.lang.Nullable;

public class PlaceListRequest {

    @Nullable
    private List<Long> PlaceIds;

    public PlaceListRequest() {
    }

    public List<Long> getPlaceIds() {
        return PlaceIds;
    }

    public void setPlaceIds(List<Long> placeIds) {
        PlaceIds = placeIds;
    }

}

package com.lonework.corners.place.model.DTO;

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

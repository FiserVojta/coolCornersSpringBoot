package com.lonework.corners.model.request;

import java.util.List;

public class TagSearchRequest {

    private List<Long> placeId;

    public TagSearchRequest() {
    }

    public List<Long> getPlaceId() {
        return placeId;
    }

    public void setPlaceId(List<Long> placeId) {
        this.placeId = placeId;
    }

}

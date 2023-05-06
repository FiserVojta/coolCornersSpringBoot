package com.lonework.corners.model.request;

import java.util.Set;

import com.lonework.corners.model.Tag;

public class PlaceSearchRequest {

    private Long city_id;
    private Double rating;

    public PlaceSearchRequest() {
    }

    public Double getRating() {
        return rating;
    }

    public void setRating(Double rating) {
        this.rating = rating;
    }

    public Long getCity_id() {
        return city_id;
    }

    public void setCity_id(Long city_id) {
        this.city_id = city_id;
    }

}

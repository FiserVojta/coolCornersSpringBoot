package com.lonework.corners.model.request;

import java.util.Set;

import com.lonework.corners.model.Tag;

public class PlaceSearchRequest {

    private String city;
    private Double rating;

    public PlaceSearchRequest() {
    }

    public String getCity() {
        return city;
    }

    public void setCity(String city) {
        this.city = city;
    }

    public Double getRating() {
        return rating;
    }

    public void setRating(Double rating) {
        this.rating = rating;
    }

}

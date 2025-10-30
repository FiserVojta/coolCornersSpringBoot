package com.lonework.corners.place.model.DTO;

import java.util.List;


public class PlaceSearchRequest {


    private Double rating;

    private List<Long> tags;

    private List<Long> category;

    private List<Long> cities;

    public PlaceSearchRequest() {
    }
    public List<Long> getTags() {
        return tags;
    }

    public void setTags(List<Long> tags) {
        this.tags = tags;
    }

    public List<Long> getCategory() {
        return category;
    }

    public void setCategory(List<Long> category) {
        this.category = category;
    }

    public Double getRating() {
        return rating;
    }

    public void setRating(Double rating) {
        this.rating = rating;
    }

    public List<Long> getCities() {
        return cities;
    }

    public void setCities(List<Long> cities) {
        this.cities = cities;
    }
}

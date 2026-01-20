package com.lonework.corners.trip.model;

import java.util.List;

public class TripSearchRequest {

    private List<Long> categories;

    private List<Long> tags;


    public TripSearchRequest() {
    }

    public List<Long> getCategories() {
        return categories;
    }

    public void setCategories(List<Long> categories) {
        this.categories = categories;
    }

    public List<Long> getTags() {
        return tags;
    }

    public void setTags(List<Long> tags) {
        this.tags = tags;
    }


}

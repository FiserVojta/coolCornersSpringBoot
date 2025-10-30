package com.lonework.corners.trip.model;

import java.util.List;

public class TripSearchRequest {

    private List<Long> categories;

    private List<Long> cities;

    private List<Long> countries;

    private List<Long> tags;

    private List<Long> durations;

    public TripSearchRequest() {
    }

    public List<Long> getCategories() {
        return categories;
    }

    public void setCategories(List<Long> categories) {
        this.categories = categories;
    }

    public List<Long> getCities() {
        return cities;
    }

    public void setCities(List<Long> cities) {
        this.cities = cities;
    }

    public List<Long> getCountries() {
        return countries;
    }

    public void setCountries(List<Long> countries) {
        this.countries = countries;
    }

    public List<Long> getTags() {
        return tags;
    }

    public void setTags(List<Long> tags) {
        this.tags = tags;
    }

    public List<Long> getDurations() {
        return durations;
    }

    public void setDurations(List<Long> durations) {
        this.durations = durations;
    }

}

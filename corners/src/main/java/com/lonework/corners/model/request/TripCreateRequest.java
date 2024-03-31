package com.lonework.corners.model.request;

import java.util.List;

import com.lonework.corners.model.Trip;

public class TripCreateRequest {

    private List<Long> placeIdList;

    private List<Long> categoryIdList;

    private List<Long> tagIdList;

    private String name;

    private String author;

    private String description;

    private Integer duration;

    // like if its trip in
    private String Type;

    private String state;

    private String city;

    public TripCreateRequest() {
    }

    public Trip getTrip() {
        Trip trip = new Trip();
        trip.setCreator(author);
        trip.setCategory(null);
        trip.setDescription(description);
        trip.setDuration(duration);
        trip.setName(name);
        return trip;
    }

    public List<Long> getPlaceIdList() {
        return placeIdList;
    }

    public void setPlaceIdList(List<Long> placeIdList) {
        this.placeIdList = placeIdList;
    }

    public List<Long> getCategoryIdList() {
        return categoryIdList;
    }

    public void setCategoryIdList(List<Long> categoryIdList) {
        this.categoryIdList = categoryIdList;
    }

    public List<Long> getTagIdList() {
        return tagIdList;
    }

    public void setTagIdList(List<Long> tagIdList) {
        this.tagIdList = tagIdList;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getAuthor() {
        return author;
    }

    public void setAuthor(String author) {
        this.author = author;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public Integer getDuration() {
        return duration;
    }

    public void setDuration(Integer duration) {
        this.duration = duration;
    }

    public String getType() {
        return Type;
    }

    public void setType(String type) {
        Type = type;
    }

    public String getState() {
        return state;
    }

    public void setState(String state) {
        this.state = state;
    }

    public String getCity() {
        return city;
    }

    public void setCity(String city) {
        this.city = city;
    }

}

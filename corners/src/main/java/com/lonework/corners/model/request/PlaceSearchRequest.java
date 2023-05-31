package com.lonework.corners.model.request;

import java.util.List;
import java.util.Set;

import org.springframework.lang.Nullable;

import com.lonework.corners.model.Category;
import com.lonework.corners.model.Tag;

public class PlaceSearchRequest {

    @Nullable
    private List<Long> cityIds;
    private Double rating;
    private List<Long> tagIds;
    private List<Long> categoryIds;

    private Boolean openNow;
    private List<Long> stateIds;
    private locationRange location;

    private ResultOrder order;

    public PlaceSearchRequest() {
    }

    public void setCityIds(List<Long> cityIds) {
        this.cityIds = cityIds;
    }

    public Boolean getOpenNow() {
        return openNow;
    }

    public void setOpenNow(Boolean openNow) {
        this.openNow = openNow;
    }

    public List<Long> getStateIds() {
        return stateIds;
    }

    public void setStateIds(List<Long> stateIds) {
        this.stateIds = stateIds;
    }

    public locationRange getLocation() {
        return location;
    }

    public void setLocation(locationRange location) {
        this.location = location;
    }

    public ResultOrder getOrder() {
        return order;
    }

    public void setOrder(ResultOrder order) {
        this.order = order;
    }

    public Double getRating() {
        return rating;
    }

    public void setRating(Double rating) {
        this.rating = rating;
    }

    public List<Long> getTagIds() {
        return tagIds;
    }

    public void setTagIds(List<Long> tagIds) {
        this.tagIds = tagIds;
    }

    public List<Long> getCategoryIds() {
        return categoryIds;
    }

    public void setCategoryIds(List<Long> categoryIds) {
        this.categoryIds = categoryIds;
    }

    public List<Long> getCityIds() {
        return cityIds;
    }

    public void setCityId(List<Long> cityIds) {
        this.cityIds = cityIds;
    }

}

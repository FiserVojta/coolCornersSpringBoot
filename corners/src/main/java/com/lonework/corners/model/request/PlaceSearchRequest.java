package com.lonework.corners.model.request;

import java.util.List;
import java.util.Set;

import org.springframework.lang.Nullable;

import com.lonework.corners.model.Category;
import com.lonework.corners.model.Tag;

public class PlaceSearchRequest {

    @Nullable
    private Long cityId;
    private Double rating;
    private List<Long> tagIds;
    private List<Long> categoryIds;

    public PlaceSearchRequest() {
    }

    public Long getCityId() {
        return cityId;
    }

    public void setCityId(Long cityId) {
        this.cityId = cityId;
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

}

package com.lonework.corners.model.request;

import java.util.List;
import java.util.Set;

import org.springframework.lang.Nullable;

import com.lonework.corners.model.Category;
import com.lonework.corners.model.Tag;

public class PlaceSearchRequest {
//    private Double rating;
//    private List<Long> tagIds;

    private List<Long> category;

//    private Boolean openNow;
//
//    private ResultOrder order;

    public PlaceSearchRequest() {
    }


//    public Boolean getOpenNow() {
//        return openNow;
//    }
//
//    public void setOpenNow(Boolean openNow) {
//        this.openNow = openNow;
//    }
//
//
//    public ResultOrder getOrder() {
//        return order;
//    }
//
//    public void setOrder(ResultOrder order) {
//        this.order = order;
//    }
//
//    public Double getRating() {
//        return rating;
//    }
//
//    public void setRating(Double rating) {
//        this.rating = rating;
//    }
//
//    public List<Long> getTagIds() {
//        return tagIds;
//    }
//
//    public void setTagIds(List<Long> tagIds) {
//        this.tagIds = tagIds;
//    }

    public List<Long> getCategory() {
        return category;
    }

    public void setCategory(List<Long> category) {
        this.category = category;
    }
}

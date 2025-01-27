package com.lonework.corners.model.request;

import java.util.List;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.lonework.corners.model.Trip;
import org.locationtech.jts.geom.Geometry;


public class TripCreateRequest {

    @JsonProperty
    private List<Long> placeIdList;

    @JsonProperty
    private Long categoryId;

    @JsonProperty
    private List<Long> tagIdList;

    @JsonProperty
    private String name;

    @JsonProperty
    private String author;

    @JsonProperty
    private String description;

    @JsonProperty
    private Integer duration;

    @JsonProperty
    private String Type;

    @JsonProperty
    private Geometry geometry;

    public TripCreateRequest() {
    }

    public List<Long> getPlaceIdList() {
        return placeIdList;
    }

    public void setPlaceIdList(List<Long> placeIdList) {
        this.placeIdList = placeIdList;
    }

    public Long getCategoryId() {
        return categoryId;
    }

    public void setCategoryId(Long categoryId) {
        this.categoryId = categoryId;
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

    public Geometry getGeometry() {
        return geometry;
    }

    public void setGeometry(Geometry geometry) {
        this.geometry = geometry;
    }
}

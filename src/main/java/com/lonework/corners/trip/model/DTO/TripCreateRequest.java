package com.lonework.corners.trip.model.DTO;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.lonework.corners.files.model.DTO.CornerFileCreateLinkRequest;
import com.lonework.corners.place.model.DTO.GooglePlaceCreateRequest;
import org.locationtech.jts.geom.Geometry;

import java.util.List;


public class TripCreateRequest {

    @JsonProperty
    private List<Long> placeIds;

    @JsonProperty
    private Long categoryId;

    @JsonProperty
    private List<Long> tags;

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

    @JsonProperty
    private List<CornerFileCreateLinkRequest> files;

    @JsonProperty
    private List<GooglePlaceCreateRequest> googlePlaces;

    public TripCreateRequest() {
    }

    public List<Long> getPlaceIds() {
        return placeIds;
    }

    public void setPlaceIds(List<Long> placeIds) {
        this.placeIds = placeIds;
    }

    public Long getCategoryId() {
        return categoryId;
    }

    public void setCategoryId(Long categoryId) {
        this.categoryId = categoryId;
    }

    public List<Long> getTags() {
        return tags;
    }

    public void setTags(List<Long> tagIdList) {
        this.tags = tagIdList;
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

    public List<CornerFileCreateLinkRequest> getFiles() {
        return files;
    }

    public void setFiles(List<CornerFileCreateLinkRequest> files) {
        this.files = files;
    }

    public List<GooglePlaceCreateRequest> getGooglePlaces() {
        return googlePlaces;
    }

    public void setGooglePlaces(List<GooglePlaceCreateRequest> googlePlaces) {
        this.googlePlaces = googlePlaces;
    }
}

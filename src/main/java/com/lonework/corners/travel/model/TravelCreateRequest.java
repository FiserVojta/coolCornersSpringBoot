package com.lonework.corners.travel.model;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.time.LocalDate;
import java.util.List;


public record TravelCreateRequest(
        @JsonProperty String title,
        @JsonProperty String description,
        @JsonProperty String location,
        @JsonProperty LocalDate startDate,
        @JsonProperty LocalDate endDate,
        @JsonProperty TravelVisibility visibility,
        @JsonProperty Long categoryId,
        @JsonProperty List<Long> tags,
        @JsonProperty Long coverImageId,
        @JsonProperty List<TravelPhotoRequest> photos,
        @JsonProperty List<TravelPlaceRequest> places,
        @JsonProperty List<TravelDayNoteRequest> dayNotes
) {
}

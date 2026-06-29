package com.lonework.corners.travel.model;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.time.LocalDate;


public record TravelPhotoRequest(
        @JsonProperty Long fileId,
        @JsonProperty Double latitude,
        @JsonProperty Double longitude,
        @JsonProperty LocalDate takenOn
) {
}

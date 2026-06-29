package com.lonework.corners.travel.model;

import com.fasterxml.jackson.annotation.JsonProperty;


public record TravelPlaceRequest(
        @JsonProperty String name,
        @JsonProperty Double latitude,
        @JsonProperty Double longitude
) {
}

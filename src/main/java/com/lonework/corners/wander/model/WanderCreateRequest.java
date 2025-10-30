package com.lonework.corners.wander.model;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.time.ZonedDateTime;
import java.util.List;


public record WanderCreateRequest(
        @JsonProperty String description,
        @JsonProperty Integer capacity,
        @JsonProperty ZonedDateTime startTime,
        @JsonProperty List<Long> wanderers,
        @JsonProperty List<Long> tags,
        @JsonProperty Long category,
        @JsonProperty List<WanderPartCreateRequest> wanderParts
) {

    public record WanderPartCreateRequest(
            @JsonProperty List<Long> places,
            @JsonProperty List<Long> trips,
            @JsonProperty Integer order
    ) {
    }
}

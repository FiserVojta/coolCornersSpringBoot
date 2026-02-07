package com.lonework.corners.wander.model;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.List;


public record WanderPartUpdateRequest(
        @JsonProperty List<Long> places,
        @JsonProperty List<Long> trips,
        @JsonProperty Integer order,
        @JsonProperty String name
) {
}

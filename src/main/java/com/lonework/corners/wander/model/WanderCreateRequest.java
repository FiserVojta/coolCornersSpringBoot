package com.lonework.corners.wander.model;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.time.LocalDateTime;
import java.util.List;


public record WanderCreateRequest(
        @JsonProperty String description,
        @JsonProperty Integer capacity,
        @JsonProperty LocalDateTime startTime,
        @JsonProperty WanderType wanderType,
        @JsonProperty List<Long> wanderers,
        @JsonProperty List<Long> tags,
        @JsonProperty Long category,
        @JsonProperty List<WanderPartCreateRequest> wanderParts
) {
}

package com.lonework.corners.trip.model.DTO;


import com.fasterxml.jackson.annotation.JsonProperty;
import org.springframework.lang.NonNull;


public record TripRateRequest(
        @JsonProperty @NonNull Integer rating,
        @JsonProperty String createdBy
) {
}

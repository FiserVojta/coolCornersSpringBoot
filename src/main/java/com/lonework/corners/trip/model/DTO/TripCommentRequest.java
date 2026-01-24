package com.lonework.corners.trip.model.DTO;


import com.fasterxml.jackson.annotation.JsonProperty;
import org.springframework.lang.NonNull;


public record TripCommentRequest(
        @JsonProperty @NonNull String value,
        @JsonProperty String createdBy
) {
}

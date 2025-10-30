package com.lonework.corners.place.model.DTO;

import com.fasterxml.jackson.annotation.JsonProperty;
import org.springframework.lang.NonNull;


public record PlaceRateRequest(
        @JsonProperty @NonNull Integer rating,
        @JsonProperty String createdBy
) {
}

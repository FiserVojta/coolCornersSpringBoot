package com.lonework.corners.user.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import org.springframework.lang.NonNull;

public record UserRateRequest(
        @JsonProperty @NonNull Integer rating,
        @JsonProperty String createdBy
) {
}

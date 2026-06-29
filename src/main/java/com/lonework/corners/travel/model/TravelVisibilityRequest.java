package com.lonework.corners.travel.model;

import com.fasterxml.jackson.annotation.JsonProperty;


public record TravelVisibilityRequest(
        @JsonProperty TravelVisibility visibility
) {
}

package com.lonework.corners.travel.model;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.List;


public record TravelPlaceRequest(
        @JsonProperty String name,
        @JsonProperty Double latitude,
        @JsonProperty Double longitude,
        /** How the traveller got here from the previous place; null uses the travel's mode. */
        @JsonProperty TravelTransportMode transportMode,
        /** Routed line of the leg arriving here as [lat, lng] pairs; invalid lines are dropped. */
        @JsonProperty List<List<Double>> routeGeometry
) {
}

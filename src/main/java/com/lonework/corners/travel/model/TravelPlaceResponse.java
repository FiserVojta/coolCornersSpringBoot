package com.lonework.corners.travel.model;

import java.util.List;


public record TravelPlaceResponse(
        Long id,
        String name,
        Double latitude,
        Double longitude,
        /** How the traveller got here from the previous place; null uses the travel's mode. */
        TravelTransportMode transportMode,
        /** Cached routed line of the leg arriving here as [lat, lng] pairs, or null. */
        List<List<Double>> routeGeometry
) {
    public static TravelPlaceResponse from(TravelPlace place) {
        return new TravelPlaceResponse(
                place.getId(),
                place.getName(),
                place.getLatitude(),
                place.getLongitude(),
                place.getTransportMode(),
                place.getRouteGeometry()
        );
    }
}

package com.lonework.corners.travel.model;


public record TravelPlaceResponse(
        Long id,
        String name,
        Double latitude,
        Double longitude
) {
    public static TravelPlaceResponse from(TravelPlace place) {
        return new TravelPlaceResponse(
                place.getId(),
                place.getName(),
                place.getLatitude(),
                place.getLongitude()
        );
    }
}

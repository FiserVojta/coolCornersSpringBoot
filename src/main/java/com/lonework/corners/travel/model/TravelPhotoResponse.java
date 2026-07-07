package com.lonework.corners.travel.model;

import java.time.LocalDate;


public record TravelPhotoResponse(
        Long id,
        Long fileId,
        String name,
        String url,
        String thumbnailUrl,
        Double latitude,
        Double longitude,
        LocalDate takenOn
) {
    public static TravelPhotoResponse from(TravelPhoto photo) {
        return new TravelPhotoResponse(
                photo.getId(),
                photo.getFile() != null ? photo.getFile().getId() : null,
                photo.getFile() != null ? photo.getFile().getName() : null,
                photo.getFile() != null ? photo.getFile().getUrl() : null,
                photo.getFile() != null ? photo.getFile().getThumbnailUrl() : null,
                photo.getLatitude(),
                photo.getLongitude(),
                photo.getTakenOn()
        );
    }
}

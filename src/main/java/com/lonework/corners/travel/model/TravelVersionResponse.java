package com.lonework.corners.travel.model;

import com.lonework.corners.files.model.CornerFile;

import java.time.LocalDate;


/**
 * One version of a trip — somebody's own run of it, with their own photos.
 * Deliberately slim: enough to render a card that links to the full travel.
 */
public record TravelVersionResponse(
        Long id,
        String title,
        LocalDate startDate,
        LocalDate endDate,
        CornerFile coverImage,
        int photoCount,
        TravelOwner owner,
        Double rating
) {
    public static TravelVersionResponse from(Travel travel) {
        return new TravelVersionResponse(
                travel.getId(),
                travel.getTitle(),
                travel.getStartDate(),
                travel.getEndDate(),
                travel.getCoverImage(),
                travel.getPhotos() != null ? travel.getPhotos().size() : 0,
                TravelOwner.from(travel.getOwner()),
                travel.getRating()
        );
    }
}

package com.lonework.corners.travel.model;

import com.lonework.corners.category.model.Category;
import com.lonework.corners.files.model.CornerFile;
import com.lonework.corners.tag.model.Tag;

import java.time.LocalDate;
import java.time.ZonedDateTime;
import java.util.List;


public record TravelDetailResponse(
        Long id,
        String title,
        String description,
        String location,
        LocalDate startDate,
        LocalDate endDate,
        TravelVisibility visibility,
        String shareToken,
        TravelOwner owner,
        CornerFile coverImage,
        List<TravelPhotoResponse> photos,
        List<TravelPlaceResponse> places,
        List<TravelDayNoteResponse> dayNotes,
        Double rating,
        Integer myRating,
        Category category,
        List<Tag> tags,
        ZonedDateTime createdAt
) {
    /**
     * @param includeShareToken include the share token only when the viewer owns the travel,
     *                          so it is never leaked to non-owners.
     */
    public static TravelDetailResponse from(Travel travel, boolean includeShareToken) {
        return from(travel, includeShareToken, null);
    }

    /**
     * @param myRating the current viewer's own rating of this travel, or null when they
     *                 haven't rated it (anonymous/shared views always pass null).
     */
    public static TravelDetailResponse from(Travel travel, boolean includeShareToken, Integer myRating) {
        return new TravelDetailResponse(
                travel.getId(),
                travel.getTitle(),
                travel.getDescription(),
                travel.getLocation(),
                travel.getStartDate(),
                travel.getEndDate(),
                travel.getVisibility(),
                includeShareToken ? travel.getShareToken() : null,
                TravelOwner.from(travel.getOwner()),
                travel.getCoverImage(),
                travel.getPhotos() != null
                        ? travel.getPhotos().stream().map(TravelPhotoResponse::from).toList()
                        : List.of(),
                travel.getPlaces() != null
                        ? travel.getPlaces().stream().map(TravelPlaceResponse::from).toList()
                        : List.of(),
                travel.getDayNotes() != null
                        ? travel.getDayNotes().stream().map(TravelDayNoteResponse::from).toList()
                        : List.of(),
                travel.getRating(),
                myRating,
                travel.getCategory(),
                travel.getTags() != null ? travel.getTags() : List.of(),
                travel.getCreatedAt()
        );
    }
}

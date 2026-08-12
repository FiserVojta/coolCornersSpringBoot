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
        ZonedDateTime createdAt,
        /** The original this travel is a version of, or null when this travel is the original. */
        Long originTravelId,
        /**
         * How many times this trip was done in total — the original plus every version of it,
         * including versions the viewer may not open. Null when the group wasn't resolved.
         */
        Integer timesDone,
        /** The other versions of this trip the viewer is allowed to see; never includes this travel. */
        List<TravelVersionResponse> otherVersions
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
        return from(travel, includeShareToken, myRating, null, List.of());
    }

    /**
     * @param timesDone     size of this trip's version group (original + versions), or null when unknown.
     * @param otherVersions versions of the same trip the viewer may see, excluding this travel.
     */
    public static TravelDetailResponse from(Travel travel, boolean includeShareToken, Integer myRating,
                                            Integer timesDone, List<TravelVersionResponse> otherVersions) {
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
                travel.getCreatedAt(),
                travel.getOriginTravel() != null ? travel.getOriginTravel().getId() : null,
                timesDone,
                otherVersions != null ? otherVersions : List.of()
        );
    }
}

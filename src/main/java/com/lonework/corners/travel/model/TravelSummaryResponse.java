package com.lonework.corners.travel.model;

import com.lonework.corners.category.model.Category;
import com.lonework.corners.files.model.CornerFile;
import com.lonework.corners.tag.model.Tag;

import java.time.LocalDate;
import java.util.List;


public record TravelSummaryResponse(
        Long id,
        String title,
        String location,
        LocalDate startDate,
        LocalDate endDate,
        TravelVisibility visibility,
        CornerFile coverImage,
        int photoCount,
        TravelOwner owner,
        Double rating,
        Category category,
        List<Tag> tags
) {
    public static TravelSummaryResponse from(Travel travel) {
        return new TravelSummaryResponse(
                travel.getId(),
                travel.getTitle(),
                travel.getLocation(),
                travel.getStartDate(),
                travel.getEndDate(),
                travel.getVisibility(),
                travel.getCoverImage(),
                travel.getPhotos() != null ? travel.getPhotos().size() : 0,
                TravelOwner.from(travel.getOwner()),
                travel.getRating(),
                travel.getCategory(),
                travel.getTags() != null ? travel.getTags() : List.of()
        );
    }
}

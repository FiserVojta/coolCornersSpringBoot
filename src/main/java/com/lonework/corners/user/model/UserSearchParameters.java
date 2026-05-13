package com.lonework.corners.user.model;

public record UserSearchParameters(
        String search,
        Double minRating
) {
}

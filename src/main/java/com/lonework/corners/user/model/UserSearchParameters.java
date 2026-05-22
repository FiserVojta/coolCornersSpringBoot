package com.lonework.corners.user.model;

import com.lonework.corners.common.model.QueryOrder;

public record UserSearchParameters(
        String search,
        Double minRating,
        UserSort sortBy,
        QueryOrder sortDir
) {
}

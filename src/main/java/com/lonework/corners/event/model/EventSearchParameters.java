package com.lonework.corners.event.model;

import com.lonework.corners.common.model.QueryOrder;

import java.util.List;

public record EventSearchParameters(
        String createdBy,
        String place,
        List<Long> categories,
        EventSort sortBy,
        QueryOrder sortDir
) {
}

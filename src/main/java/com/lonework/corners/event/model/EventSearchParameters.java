package com.lonework.corners.event.model;

import java.util.List;

public record EventSearchParameters(
        String createdBy,
        String place,
        List<Long> categories
) {
}

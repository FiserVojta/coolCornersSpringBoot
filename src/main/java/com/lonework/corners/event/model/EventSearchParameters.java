package com.lonework.corners.event.model;

import com.lonework.corners.common.model.QueryOrder;
import org.springframework.format.annotation.DateTimeFormat;

import java.time.ZonedDateTime;
import java.util.List;

public record EventSearchParameters(
        String createdBy,
        String place,
        List<Long> categories,
        EventSort sortBy,
        QueryOrder sortDir,
        String search,
        @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) ZonedDateTime startsFrom,
        @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) ZonedDateTime startsUntil
) {
}

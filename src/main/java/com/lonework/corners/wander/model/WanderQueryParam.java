package com.lonework.corners.wander.model;

import org.springframework.data.repository.query.Param;

import java.time.LocalDateTime;
import java.util.List;


public record WanderQueryParam(
        @Param("startsFrom") LocalDateTime startsFrom,
        @Param("startsUntil") LocalDateTime startsUntil,
        @Param("createdBy") Long createdBy,
        @Param("categories") List<Long> categories,
        @Param("tags") List<Long> tags,
        @Param("tags") List<String> participants
) {
}

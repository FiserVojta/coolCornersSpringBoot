package com.lonework.corners.event.model.DTO;

import java.time.ZonedDateTime;


public record EventCreateRequest(
        String name,
        String description,
        String venue,
        ZonedDateTime startTime,
        String time,
        String createdBy,
        Integer capacity,
        Integer duration,
        Double price,
        Long categoryId
) {
}

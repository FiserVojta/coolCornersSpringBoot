package com.lonework.corners.event.model.DTO;

public record EventCreateRequest(
        String name,
        String description,
        String location,
        String date,
        String time,
        String createdBy
) {
}

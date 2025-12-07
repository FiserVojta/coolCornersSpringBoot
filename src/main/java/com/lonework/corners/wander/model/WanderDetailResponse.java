package com.lonework.corners.wander.model;

import com.lonework.corners.user.model.User;

import java.time.LocalDateTime;
import java.util.List;


public record WanderDetailResponse(Long id,
                                   Integer signed,
                                   Integer capacity,
                                   LocalDateTime date,
                                   Long duration,
                                   String category,
                                   List<String> tags,
                                   User createdBy,
                                   String name) {

    public WanderDetailResponse(Wander wander){
        this(
                wander.getId(),
                wander.getWanderers().size(),
                wander.getCapacity() != null ? wander.getCapacity() : 0,
                wander.getStartTime(),
                null,
                wander.getCategory() != null ? wander.getCategory().getName() : null,
                wander.getTags().stream().map(tag -> tag.getName()).toList(),
                wander.getCreatedBy(),
                null

        );
    }
}

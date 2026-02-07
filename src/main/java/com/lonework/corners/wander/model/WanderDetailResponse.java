package com.lonework.corners.wander.model;

import com.lonework.corners.category.model.Category;
import com.lonework.corners.tag.model.Tag;
import com.lonework.corners.user.model.User;

import java.time.LocalDateTime;
import java.util.List;


public record WanderDetailResponse(Long id,
                                   String description,
                                   Integer signed,
                                   Integer capacity,
                                   LocalDateTime date,
                                   Long duration,
                                   Category category,
                                   List<Tag> tags,
                                   User createdBy,
                                   String name,
                                   List<WanderPart> wanderParts) {

    public WanderDetailResponse(Wander wander){
        this(
                wander.getId(),
                wander.getDescription(),
                wander.getWanderers().size(),
                wander.getCapacity() != null ? wander.getCapacity() : 0,
                wander.getStartTime(),
                null,
                 wander.getCategory(),
                wander.getTags(),
                wander.getCreatedBy(),
                null,
                wander.getWanderParts()

        );
    }
}

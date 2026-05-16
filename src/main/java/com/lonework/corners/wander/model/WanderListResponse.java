package com.lonework.corners.wander.model;

import com.lonework.corners.files.model.CornerFile;
import com.lonework.corners.user.model.User;

import java.time.LocalDateTime;
import java.util.List;


public record WanderListResponse(Long id,
                                 String description,
                                 Integer signed,
                                 Integer capacity,
                                 LocalDateTime startTime,
                                 Long duration,
                                 String category,
                                 List<String> tags,
                                 User createdBy,
                                 String name,
                                 List<Long> wandererIds,
                                 CornerFile backgroundImage
) {
    public WanderListResponse(Wander wander) {
        this(
                wander.getId(),
                wander.getDescription(),
                wander.getWanderers() != null ? wander.getWanderers().size() : 0,
                wander.getCapacity() != null ? wander.getCapacity() : 0,
                wander.getStartTime(),
                null,
                wander.getCategory() != null ? wander.getCategory().getName() : null,
                wander.getTags() != null ? wander.getTags().stream().map(tag -> tag.getName()).toList() : List.of(),
                wander.getCreatedBy(),
                null,
                wander.getWanderers() != null ? wander.getWanderers().stream().map(u -> u.getId()).toList() : List.of(),
                wander.getBackgroundImage()
        );
    }

    public boolean isFull() {
        return capacity != null && signed != null && signed >= capacity;
    }

    public boolean isOpen() {
        return !isFull();
    }
}
